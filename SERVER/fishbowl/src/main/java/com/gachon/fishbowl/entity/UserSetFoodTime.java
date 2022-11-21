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

    //앱에서 받아온 먹이 주는 시간
    @NotNull
    @Column(name = "USER_SET_FOOD_TIME")
    private LocalTime foodTime;

    @ManyToOne
    @JoinColumn(name = "ID")
    private Sensing sensing;
}
