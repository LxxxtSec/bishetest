package com.uav.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.uav.audit.aspect.AuditRecord;
import com.uav.audit.dto.Result;
import com.uav.audit.dto.UavStatusDTO;
import com.uav.audit.entity.UavStatus;
import com.uav.audit.service.AlertDetectionService;
import com.uav.audit.service.UavStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uav")
@RequiredArgsConstructor
@Slf4j
public class UavController {

    private final UavStatusService uavStatusService;
    private final AlertDetectionService alertDetectionService;

    /**
     * 接收无人机状态数据
     */
    @PostMapping("/status")
    @AuditRecord(operationType = "UAV_STATUS", description = "接收无人机状态数据", operationObject = "无人机监控")
    public Result<UavStatus> receiveStatus(@RequestBody UavStatusDTO dto) {
        log.info("收到无人机状态上报: uavId={}, battery={}, altitude={}, speed={}",
                dto.getUavId(), dto.getBattery(), dto.getAltitude(), dto.getSpeed());

        UavStatus status = uavStatusService.updateStatus(dto);

        // 触发告警检测
        try {
            log.info("开始告警检测...");
            alertDetectionService.detectAlerts(status);
            log.info("告警检测完成");
        } catch (Exception e) {
            log.error("告警检测失败: {}", e.getMessage(), e);
        }

        return Result.success(status);
    }

    /**
     * 接收无人机心跳
     */
    @PostMapping("/heartbeat")
    public Result<Void> receiveHeartbeat(@RequestBody Map<String, String> request) {
        String uavId = request.get("uavId");
        if (uavId != null) {
            uavStatusService.processHeartbeat(uavId);
        }
        return Result.success();
    }

    /**
     * 获取无人机列表
     */
    @GetMapping("/list")
    @AuditRecord(operationType = "UAV_QUERY", description = "查看无人机列表", operationObject = "无人机监控")
    public Result<Map<String, Object>> getUavList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        IPage<UavStatus> page = uavStatusService.getUavList(pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());

        return Result.success(result);
    }

    /**
     * 获取在线无人机
     */
    @GetMapping("/online")
    public Result<List<UavStatus>> getOnlineUavs() {
        List<UavStatus> uavs = uavStatusService.getOnlineUavs();
        return Result.success(uavs);
    }

    /**
     * 获取单个无人机详情
     */
    @GetMapping("/{uavId}")
    public Result<UavStatus> getUavById(@PathVariable String uavId) {
        UavStatus status = uavStatusService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UavStatus>()
                        .eq(UavStatus::getUavId, uavId));
        return Result.success(status);
    }
}
