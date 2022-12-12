package com.gachon.fishbowl.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedTimeDto {

    Long deviceId;

    LocalTime firstTime;
    LocalTime secondTime;
    LocalTime thirdTime;

    Integer numberOfFirstFeedings;
    Integer numberOfSecondFeedings;
    Integer numberOfThirdFeedings;
}
