package com.gachon.fishbowl.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSetDTO {
    Double temperature;
    Integer waterLevel;
    Double turbidity;
    Double ph;
    Long deviceId;
}