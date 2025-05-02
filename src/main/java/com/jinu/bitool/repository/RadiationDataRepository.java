package com.jinu.bitool.repository;

import com.jinu.bitool.entity.RadiationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RadiationDataRepository extends JpaRepository<RadiationData, Long> {

    // ✅ 특정 시간 범위 내 데이터 조회
    List<RadiationData> findByTimestampBetweenOrderByTimestampAsc(
            LocalDateTime start, LocalDateTime end
    );
}
