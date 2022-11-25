package com.gachon.fishbowl.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedTimeDto {

    String token;

    LocalTime firstTime;
    LocalTime secondTime;
    LocalTime thirdTime;

    Integer numberOfFirstFeedings;
    Integer numberOfSecondFeedings;
    Integer numberOfThirdFeedings;
}
