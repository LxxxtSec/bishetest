package com.uav.audit.dto;

import lombok.Data;

@Data
public class UavStatusDTO {

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
}
