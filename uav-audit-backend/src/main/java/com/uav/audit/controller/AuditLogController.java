package com.uav.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.uav.audit.aspect.AuditRecord;
import com.uav.audit.dto.AuditLogQueryDTO;
import com.uav.audit.dto.Result;
import com.uav.audit.entity.AuditLog;
import com.uav.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @AuditRecord(operationType = "QUERY", description = "查询审计日志", operationObject = "审计日志")
    public Result<Map<String, Object>> queryLogs(AuditLogQueryDTO queryDTO) {
        IPage<AuditLog> page = auditLogService.queryLogs(queryDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<AuditLog> getLogById(@PathVariable Long id) {
        AuditLog log = auditLogService.getById(id);
        return Result.success(log);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 今日操作数
        long todayCount = auditLogService.count(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuditLog>()
                .ge(AuditLog::getLogTime, java.time.LocalDate.now().atStartOfDay())
        );

        // 总操作数
        long totalCount = auditLogService.count();

        result.put("todayCount", todayCount);
        result.put("totalCount", totalCount);

        return Result.success(result);
    }

    @GetMapping("/verify-integrity")
    public Result<Map<String, Object>> verifyIntegrity() {
        boolean isValid = auditLogService.verifyIntegrity();
        Map<String, Object> result = new HashMap<>();
        result.put("valid", isValid);
        result.put("message", isValid ? "日志完整性验证通过" : "日志已被篡改");
        return Result.success(result);
    }

    @GetMapping("/export/excel")
    public Result<byte[]> exportExcel(AuditLogQueryDTO queryDTO) {
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10000);
        IPage<AuditLog> page = auditLogService.queryLogs(queryDTO);

        byte[] content = generateExcel(page.getRecords());
        return Result.success(content);
    }

    private byte[] generateExcel(java.util.List<AuditLog> logs) {
        return "Excel export placeholder".getBytes();
    }
}
