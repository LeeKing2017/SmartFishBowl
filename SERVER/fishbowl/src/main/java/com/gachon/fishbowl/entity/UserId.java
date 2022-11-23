package com.gachon.fishbowl.entity;

import com.gachon.fishbowl.entity.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserId {
    @Id
    @Column(name = "USER_ID") //email 정보
    private String id;

    @Column(name = "USER_PW") //sub 정보
    private String pw;

    @Column(name = "FIREBASE_TOKEN") //sub 정보
    private String fireBaseToken;

    @Enumerated(EnumType.STRING) //enum 이름을 저장
    @Column(name = "ROLE", nullable = false)
    private Role role;
}
