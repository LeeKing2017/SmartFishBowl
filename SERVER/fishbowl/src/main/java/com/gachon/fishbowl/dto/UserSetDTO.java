package com.gachon.fishbowl.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSetDTO {
    String jwtToken;
    Double temperature;
    Integer waterLevel;
    Double turbidity;
    Double ph;
    Long deviceId;
}