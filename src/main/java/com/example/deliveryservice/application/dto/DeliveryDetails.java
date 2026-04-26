package com.example.deliveryservice.application.dto;

import com.example.deliveryservice.domain.model.DeliveryStatus;

import java.time.LocalDate;

public record DeliveryDetails(
        Long id,
        Long orderId,
        DeliveryStatus status,
        DeliveryAddressDetails deliveryAddress,
        LocalDate deliveryDate,
        TimeWindowDetails timeWindow,
        String trackingNumber
) {
}
