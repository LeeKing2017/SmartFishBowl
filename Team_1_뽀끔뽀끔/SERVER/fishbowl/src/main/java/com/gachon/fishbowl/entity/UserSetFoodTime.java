package com.gachon.fishbowl.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class UserSetFoodTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID")
    private UserSet userSet;

    @Column(name = "USER_SET_FIRST_FOOD_TIME")
    private LocalTime firstTime;

    @Column(name = "USER_SET_SECOND_FOOD_TIME")
    private LocalTime secondTime;

    @Column(name = "USER_SET_THIRD_FOOD_TIME")
    private LocalTime thirdTime;

    @Column(name = "NUMBER_OF_FIRST_FEEDS")
    private Integer numberOfFirstFeedings;

    @Column(name = "NUMBER_OF_SECOND_FEEDS")
    private Integer numberOfSecondFeedings;

    @Column(name = "NUMBER_OF_THIRD_FEEDS")
    private Integer numberOfThirdFeedings;
}
