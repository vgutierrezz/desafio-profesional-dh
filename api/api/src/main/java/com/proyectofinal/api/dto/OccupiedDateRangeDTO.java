package com.proyectofinal.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccupiedDateRangeDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}

