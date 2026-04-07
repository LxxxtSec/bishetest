package com.uav.audit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String operationType;

    private String operationDesc;

    private String operationObject;

    private String ipAddress;

    private String userAgent;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private Integer responseStatus;

    private String hashValue;

    private String prevHash;

    private LocalDateTime logTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
