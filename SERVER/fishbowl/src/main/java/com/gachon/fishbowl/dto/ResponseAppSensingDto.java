package com.gachon.fishbowl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseAppSensingDto {
    private Double measuredTemperature;
    private Integer measuredWaterLevel;
    private Double measuredTurbidity;
    private Double measuredPh;
}
