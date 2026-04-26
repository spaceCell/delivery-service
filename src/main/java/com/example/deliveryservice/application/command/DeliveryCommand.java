package com.example.deliveryservice.application.command;

import com.example.deliveryservice.application.dto.DeliveryAddressDetails;
import com.example.deliveryservice.application.dto.TimeWindowDetails;
import com.example.deliveryservice.domain.model.DeliveryStatus;

import java.time.LocalDate;

public record DeliveryCommand(
        Long orderId,
        DeliveryStatus status,
        DeliveryAddressDetails deliveryAddress,
        LocalDate deliveryDate,
        TimeWindowDetails timeWindow,
        String trackingNumber
) {
}
