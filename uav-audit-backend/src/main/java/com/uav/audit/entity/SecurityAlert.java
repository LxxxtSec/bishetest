package com.uav.audit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("security_alert")
public class SecurityAlert {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String alertType;

    private String alertLevel;

    private String alertMessage;

    private Long relatedUserId;

    private Long relatedLogId;

    @TableField(fill = FieldFill.INSERT)
    private Integer isResolved;

    private LocalDateTime resolveTime;

    private String resolveBy;

    private String resolveComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
