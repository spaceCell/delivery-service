package ru.iprody.deliveryservice.web.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Временное окно доставки в ответе")
public class TimeWindowResponse {

    private LocalTime startTime;
    private LocalTime endTime;
}
