package com.jinu.bitool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class RadiationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp; // 측정 시간
    private Double measuredValue;    // 실제 측정값 (ex: 3.02)

    private String sensorName;       // (선택) 센서 이름, 여러 개 있는 경우를 대비
}
