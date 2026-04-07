package com.uav.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uav.audit.entity.AlertRule;
import com.uav.audit.entity.SecurityAlert;
import com.uav.audit.entity.UavStatus;
import com.uav.audit.mapper.AlertRuleMapper;
import com.uav.audit.mapper.SecurityAlertMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertDetectionService {

    private final AlertRuleMapper alertRuleMapper;
    private final SecurityAlertMapper securityAlertMapper;
    private final UavStatusService uavStatusService;

    // 禁飞区坐标（示例：上海某区域）
    private static final double NO_FLY_LAT_MIN = 31.2300;
    private static final double NO_FLY_LAT_MAX = 31.2400;
    private static final double NO_FLY_LNG_MIN = 121.4700;
    private static final double NO_FLY_LNG_MAX = 121.4800;

    /**
     * 检测无人机状态并生成告警
     */
    public void detectAlerts(UavStatus status) {
        // 1. 低电量告警
        checkLowBattery(status);

        // 2. 高度超限告警
        checkAltitudeExceed(status);

        // 3. 速度超限告警
        checkSpeedExceed(status);

        // 4. 禁飞区入侵告警
        checkNoFlyZone(status);
    }

    /**
     * 低电量检测
     */
    private void checkLowBattery(UavStatus status) {
        if (status.getBattery() == null || status.getBattery() >= 20) return;

        AlertRule rule = getRule("LOW_BATTERY");
        if (rule == null || rule.getIsEnabled() == 0) return;

        // 检查是否已存在未处理的相同告警
        if (!hasActiveAlert(status.getUavId(), "低电量")) {
            createAlert(status, "低电量", rule.getSeverity(),
                    "无人机电池电量低于20%，当前电量：" + status.getBattery() + "%");
        }
    }

    /**
     * 高度超限检测
     */
    private void checkAltitudeExceed(UavStatus status) {
        if (status.getAltitude() == null || status.getAltitude() <= 500) return;

        AlertRule rule = getRule("ALTITUDE_EXCEED");
        if (rule == null || rule.getIsEnabled() == 0) return;

        if (!hasActiveAlert(status.getUavId(), "高度")) {
            createAlert(status, "高度超限", rule.getSeverity(),
                    "无人机飞行高度超过500米限制，当前高度：" + status.getAltitude() + "米");
        }
    }

    /**
     * 速度超限检测
     */
    private void checkSpeedExceed(UavStatus status) {
        if (status.getSpeed() == null || status.getSpeed() <= 120) return;

        AlertRule rule = getRule("SPEED_EXCEED");
        if (rule == null || rule.getIsEnabled() == 0) return;

        if (!hasActiveAlert(status.getUavId(), "速度")) {
            createAlert(status, "速度超限", rule.getSeverity(),
                    "无人机飞行速度超过120km/h限制，当前速度：" + status.getSpeed() + "km/h");
        }
    }

    /**
     * 禁飞区检测
     */
    private void checkNoFlyZone(UavStatus status) {
        if (status.getLatitude() == null || status.getLongitude() == null) return;
        if (status.getAltitude() != null && status.getAltitude() == 0) return; // 降落状态不检测

        AlertRule rule = getRule("NO_FLY_ZONE");
        if (rule == null || rule.getIsEnabled() == 0) return;

        boolean inNoFlyZone = status.getLatitude() >= NO_FLY_LAT_MIN
                && status.getLatitude() <= NO_FLY_LAT_MAX
                && status.getLongitude() >= NO_FLY_LNG_MIN
                && status.getLongitude() <= NO_FLY_LNG_MAX;

        if (inNoFlyZone) {
            if (!hasActiveAlert(status.getUavId(), "禁飞区")) {
                createAlert(status, "禁飞区入侵", "critical",
                        String.format("无人机进入禁飞区域，当前位置：%.4f, %.4f",
                                status.getLatitude(), status.getLongitude()));
            }
        }
    }

    /**
     * 定时检测信号丢失（每30秒执行一次）
     */
    @Scheduled(fixedRate = 30000)
    public void checkSignalLost() {
        List<UavStatus> lostUavs = uavStatusService.checkSignalLost(30);
        AlertRule rule = getRule("SIGNAL_LOST");

        if (rule != null && rule.getIsEnabled() == 1) {
            for (UavStatus uav : lostUavs) {
                if (!hasActiveAlert(uav.getUavId(), "信号")) {
                    createAlert(uav, "信号丢失", rule.getSeverity(),
                            "无人机超过30秒无心跳，可能失去控制");
                }
            }
        }
    }

    /**
     * 获取告警规则
     */
    private AlertRule getRule(String ruleType) {
        return alertRuleMapper.selectOne(new LambdaQueryWrapper<AlertRule>()
                .eq(AlertRule::getRuleType, ruleType));
    }

    /**
     * 检查是否已存在活跃告警
     */
    private boolean hasActiveAlert(String uavId, String alertType) {
        Long count = securityAlertMapper.selectCount(new LambdaQueryWrapper<SecurityAlert>()
                .like(SecurityAlert::getAlertMessage, uavId)
                .like(SecurityAlert::getAlertMessage, alertType)
                .eq(SecurityAlert::getIsResolved, 0));
        return count > 0;
    }

    /**
     * 创建告警
     */
    private void createAlert(UavStatus uav, String alertType, String severity, String message) {
        SecurityAlert alert = new SecurityAlert();
        alert.setAlertType(alertType);
        alert.setAlertLevel(severity);
        alert.setAlertMessage("[" + uav.getUavId() + "]" + message);
        alert.setIsResolved(0);
        alert.setCreateTime(LocalDateTime.now());

        securityAlertMapper.insert(alert);
        log.warn("生成告警: {} - {} - {}", uav.getUavId(), alertType, message);
    }
}
