package com.uav.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("uav_status")
public class UavStatus {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uavId;

    private String uavName;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Double speed;

    private Double heading;

    private Integer battery;

    private Integer signalStrength;

    private String status;

    private Integer isOnline;

    private LocalDateTime lastHeartbeat;

    private LocalDateTime createTime;
}
