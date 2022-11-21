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
//아두이노에서 받아올 어항 데이터
public class Sensing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    //물 온도
    @NotNull
    @Column(name = "MEASURED_TEMPERATURE")
    private double measuredTemperature;
    //물 수위
    @NotNull
    @Column(name = "MEASURED_WATER_LEVEL")
    private int measuredWaterLevel;
    //물 탁도
    @NotNull
    @Column(name = "MEASURED_TURBIDITY")
    private double measuredTurbidity;
    //물 ph
    @NotNull
    @Column(name = "MEASURED_PH")
    private double measuredPh;
    //DeviceId 외래키
    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private DeviceId deviceId;
/*    데이터 받는 순간의 시간 값
    @NotNull
    @Column(name = "DATE")
    private LocalDateTime date = LocalDateTime.now();*/

}
