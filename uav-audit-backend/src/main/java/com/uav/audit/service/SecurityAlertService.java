package com.uav.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.audit.entity.SecurityAlert;
import com.uav.audit.mapper.SecurityAlertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SecurityAlertService extends ServiceImpl<SecurityAlertMapper, SecurityAlert> {

    /**
     * 检测登录失败告警
     */
    public void checkLoginFailure(String username, String ipAddress, boolean success) {
        if (!success) {
            // 记录失败次数（简化实现）
            SecurityAlert alert = new SecurityAlert();
            alert.setAlertType("LOGIN_FAILURE");
            alert.setAlertLevel("medium");
            alert.setAlertMessage("用户 " + username + " 登录失败，IP: " + ipAddress);
            alert.setCreateTime(LocalDateTime.now());
            this.save(alert);
        }
    }

    /**
     * 检测非工作时间敏感操作
     */
    public void checkNonWorkHourOperation(String username, String operationType) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        // 18:00 - 次日 8:00 为非工作时间
        if (hour >= 18 || hour < 8) {
            SecurityAlert alert = new SecurityAlert();
            alert.setAlertType("NON_WORK_HOUR");
            alert.setAlertLevel("low");
            alert.setAlertMessage("用户 " + username + " 在非工作时间执行敏感操作: " + operationType);
            alert.setCreateTime(now);
            this.save(alert);
        }
    }

    /**
     * 解决告警
     */
    public void resolveAlert(Long alertId, String resolveBy, String comment) {
        SecurityAlert alert = this.getById(alertId);
        if (alert != null) {
            alert.setIsResolved(1);
            alert.setResolveTime(LocalDateTime.now());
            alert.setResolveBy(resolveBy);
            alert.setResolveComment(comment);
            this.updateById(alert);
        }
    }

    public IPage<SecurityAlert> queryAlerts(Integer pageNum, Integer pageSize, Integer isResolved) {
        Page<SecurityAlert> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SecurityAlert> wrapper = new LambdaQueryWrapper<>();
        if (isResolved != null) {
            wrapper.eq(SecurityAlert::getIsResolved, isResolved);
        }
        wrapper.orderByDesc(SecurityAlert::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 定时任务：检测高频操作
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkHighFrequencyOperation() {
        // 实现高频操作检测逻辑
    }
}
