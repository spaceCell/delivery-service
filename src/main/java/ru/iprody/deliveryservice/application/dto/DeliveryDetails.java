package ru.iprody.deliveryservice.application.dto;

import java.time.LocalDate;

import ru.iprody.deliveryservice.domain.model.DeliveryStatus;

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
