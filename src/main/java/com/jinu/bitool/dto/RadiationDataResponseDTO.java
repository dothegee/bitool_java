package com.jinu.bitool.dto;

import java.time.LocalDateTime;

public record RadiationDataResponseDTO(
        LocalDateTime timestamp,
        Double measuredValue
) {}
