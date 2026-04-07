package com.uav.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.audit.dto.AuditLogQueryDTO;
import com.uav.audit.entity.AuditLog;
import com.uav.audit.mapper.AuditLogMapper;
import com.uav.audit.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService extends ServiceImpl<AuditLogMapper, AuditLog> {

    private final HashUtil hashUtil;

    @Transactional
    public AuditLog saveLog(AuditLog log) {
        // 获取上一条日志的哈希值
        AuditLog lastLog = this.getLastLog();
        String prevHash = lastLog != null ? lastLog.getHashValue() : "0";

        // 计算当前日志哈希
        log.setPrevHash(prevHash);
        log.setHashValue(hashUtil.calculateHash(log));
        log.setCreateTime(LocalDateTime.now());

        this.save(log);
        return log;
    }

    public AuditLog getLastLog() {
        return this.getOne(new LambdaQueryWrapper<AuditLog>()
                .orderByDesc(AuditLog::getId)
                .last("LIMIT 1"));
    }

    public IPage<AuditLog> queryLogs(AuditLogQueryDTO queryDTO) {
        Page<AuditLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
            wrapper.like(AuditLog::getUsername, queryDTO.getUsername());
        }
        if (queryDTO.getOperationType() != null && !queryDTO.getOperationType().isEmpty()) {
            wrapper.eq(AuditLog::getOperationType, queryDTO.getOperationType());
        }
        if (queryDTO.getOperationObject() != null && !queryDTO.getOperationObject().isEmpty()) {
            wrapper.like(AuditLog::getOperationObject, queryDTO.getOperationObject());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(AuditLog::getLogTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(AuditLog::getLogTime, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(AuditLog::getLogTime);
        return this.page(page, wrapper);
    }

    public boolean verifyIntegrity() {
        List<AuditLog> logs = this.list(new LambdaQueryWrapper<AuditLog>()
                .orderByAsc(AuditLog::getId));

        for (int i = 0; i < logs.size(); i++) {
            AuditLog log = logs.get(i);
            String expectedPrevHash = i == 0 ? "0" : logs.get(i - 1).getHashValue();

            if (!expectedPrevHash.equals(log.getPrevHash())) {
                return false;
            }

            String expectedHash = hashUtil.calculateHash(log);
            if (!expectedHash.equals(log.getHashValue())) {
                return false;
            }
        }
        return true;
    }
}
