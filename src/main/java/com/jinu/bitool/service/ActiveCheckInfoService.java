package com.jinu.bitool.service;

import com.jinu.bitool.dto.ActiveCheckResponseDTO;
import com.jinu.bitool.repository.ActiveCheckInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// ✅ 4. 비즈니스 로직 계층입니다.
// - Repository에서 데이터를 가져오고 DTO로 변환하는 로직을 처리
// - Controller에서는 이 Service를 호출하여 응답 결과만 가져옵니다.
// - 기능의 변경·확장은 Service에서 담당하게 되어 역할이 분리됩니다.
//
//@Service
//@RequiredArgsConstructor
//public class ActiveCheckInfoService {
//
//    private final ActiveCheckInfoRepository repository;
//
//    public List<ActiveCheckResponseDTO> getDataBetween(LocalDateTime start, LocalDateTime end) {
//        return repository.findByDatetimeBetweenOrderByDatetimeAsc(start, end)
//                .stream()
//                .map(e -> new ActiveCheckResponseDTO(e.getDatetime(), e.getCalculatedact()))
//                .toList();
//    }
//}
@Service
@RequiredArgsConstructor
public class ActiveCheckInfoService {

    private final ActiveCheckInfoRepository repository;

    @Cacheable(
            value = "radiationActivityCache", // ✅ 캐시 저장소 이름
            key = "#start.toString() + ':' + #end.toString()", // ✅ 요청 범위 기준으로 캐시 키 생성
            unless = "#result == null || #result.isEmpty()" // ❌ 결과가 없으면 캐시하지 않음
    )
    public List<ActiveCheckResponseDTO> getDataBetween(LocalDateTime start, LocalDateTime end) {
        System.out.println("📡 DB에서 조회합니다: " + start + " ~ " + end); // ✅ 캐시 hit/miss 확인용 로그

        return repository.findByDatetimeBetweenOrderByDatetimeAsc(start, end)
                .stream()
                .map(e -> new ActiveCheckResponseDTO(
                        e.getDatetime(),
                        e.getCalculatedact(),
                        e.getGuide_activity(),
                        e.getUpper_limit(),
                        e.getLower_limit(),
                        e.getDuration()))
                .toList();
    }
}
