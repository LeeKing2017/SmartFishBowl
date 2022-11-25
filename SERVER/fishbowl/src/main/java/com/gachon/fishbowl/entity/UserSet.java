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

    //앱에서 설정한 물 온도
    @Column(name = "USER_SET_TEMPERATURE")
    private double userSetTemperature;

    //앱에서 설정한 물 수위
    @Column(name = "USER_SET_WATER_LEVEL")
    private int userSetWaterLevel;

    //앱에서 설정한 물 탁도
    @Column(name = "USER_SET_TURBIDITY")
    private double userSetTurbidity;

    //앱에서 설정한 물 ph
    @Column(name = "USER_SET_PH")
    private double userSetPh;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private DeviceId deviceId;
}
