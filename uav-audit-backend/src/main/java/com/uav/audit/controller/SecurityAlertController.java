package com.uav.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.uav.audit.aspect.AuditRecord;
import com.uav.audit.dto.Result;
import com.uav.audit.entity.SecurityAlert;
import com.uav.audit.service.SecurityAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class SecurityAlertController {

    private final SecurityAlertService securityAlertService;

    @GetMapping
    @AuditRecord(operationType = "ALERT_QUERY", description = "查看告警列表", operationObject = "告警管理")
    public Result<Map<String, Object>> queryAlerts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer isResolved) {

        IPage<SecurityAlert> page = securityAlertService.queryAlerts(pageNum, pageSize, isResolved);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @PostMapping("/{id}/resolve")
    @AuditRecord(operationType = "ALERT_RESOLVE", description = "处理告警", operationObject = "告警管理")
    public Result<Void> resolveAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String resolveBy = authentication.getName();
        String comment = request.get("comment");
        securityAlertService.resolveAlert(id, resolveBy, comment);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        long total = securityAlertService.count();
        long unresolved = securityAlertService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SecurityAlert>()
                        .eq(SecurityAlert::getIsResolved, 0)
        );
        long critical = securityAlertService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SecurityAlert>()
                        .eq(SecurityAlert::getAlertLevel, "critical")
                        .eq(SecurityAlert::getIsResolved, 0)
        );

        result.put("total", total);
        result.put("unresolved", unresolved);
        result.put("critical", critical);
        result.put("resolved", total - unresolved);

        return Result.success(result);
    }
}
