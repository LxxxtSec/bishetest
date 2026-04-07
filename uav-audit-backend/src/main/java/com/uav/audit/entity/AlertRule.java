package com.uav.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("alert_rule")
public class AlertRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleName;

    private String ruleType;

    private Double thresholdValue;

    private Integer isEnabled;

    private String severity;

    private String description;

    private LocalDateTime createTime;
}
