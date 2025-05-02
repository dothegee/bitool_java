package com.jinu.bitool.controller;

import com.jinu.bitool.dto.RadiationDataResponseDTO;
import com.jinu.bitool.service.RadiationDataService;
import com.jinu.bitool.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/radiation")
public class RadiationDataController {

    private final RadiationDataService radiationDataService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RadiationDataResponseDTO>>> getRadiationData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<RadiationDataResponseDTO> data = radiationDataService.getDataBetween(start, end);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(new ApiResponse<>("SUCCESS", "측정값 조회 성공", data));
    }
}
