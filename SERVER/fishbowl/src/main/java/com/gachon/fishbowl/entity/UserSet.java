package com.gachon.fishbowl.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    //앱에서 받아온 물 온도
    @NotNull
    @Column(name = "USER_SET_TEMPERATURE")
    private double userSetTemperature;
    //앱에서 받아온 물 수위
    @NotNull
    @Column(name = "USER_SET_WATER_LEVEL")
    private int userSetWaterLevel;
    //앱에서 받아온 물 탁도
    @NotNull
    @Column(name = "USER_SET_TURBIDITY")
    private double userSetTurbidity;
    //앱에서 받아온 물 ph
    @NotNull
    @Column(name = "USER_SET_PH")
    private double userSetPh;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private DeviceId deviceId;
}
