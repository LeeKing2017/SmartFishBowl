package com.gachon.fishbowl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserId {
    @Id
    @Column(name = "USER_ID")
    private String id;
}
