package ru.iprody.deliveryservice.web.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Временное окно доставки в запросе")
public class TimeWindowRequest {

    private LocalTime startTime;
    private LocalTime endTime;
}
