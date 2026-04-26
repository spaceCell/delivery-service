package ru.iprody.deliveryservice.application.command;

import java.time.LocalDate;

import ru.iprody.deliveryservice.application.dto.DeliveryAddressDetails;
import ru.iprody.deliveryservice.application.dto.TimeWindowDetails;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;

public record DeliveryCommand(
        Long orderId,
        DeliveryStatus status,
        DeliveryAddressDetails deliveryAddress,
        LocalDate deliveryDate,
        TimeWindowDetails timeWindow,
        String trackingNumber
) {
}
