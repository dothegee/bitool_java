package com.jinu.bitool.service;

import com.jinu.bitool.dto.RadiationDataResponseDTO;
import com.jinu.bitool.entity.RadiationData;
import com.jinu.bitool.repository.RadiationDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RadiationDataService {

    private final RadiationDataRepository repository;

    @Cacheable(
            value = "radiationDataCache", // ✅ 캐시 저장소 이름
            key = "#start.toString() + ':' + #end.toString()", // ✅ 캐시 키 (시간 범위 기준)
            unless = "#result == null || #result.isEmpty()" // ❌ 비어 있으면 저장 안 함
    )
    public List<RadiationDataResponseDTO> getDataBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetweenOrderByTimestampAsc(start, end)
                .stream()
                .map(data -> new RadiationDataResponseDTO(data.getTimestamp(), data.getMeasuredValue()))
                .toList();
    }
}
