package com.example.deliveryservice.application.dto;

import java.time.LocalTime;

public record TimeWindowDetails(
        LocalTime startTime,
        LocalTime endTime
) {
}
