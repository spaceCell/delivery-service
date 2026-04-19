package com.example.deliveryservice.web.dto;

import java.time.LocalDate;

import com.example.deliveryservice.domain.model.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными доставки")
public class DeliveryResponse {

    @Schema(description = "Идентификатор доставки", example = "1")
    private Long id;
    @Schema(description = "Идентификатор заказа", example = "1")
    private Long orderId;
    @Schema(description = "Статус доставки", example = "IN_TRANSIT")
    private DeliveryStatus status;
    @Schema(description = "Адрес доставки")
    private DeliveryAddressResponse deliveryAddress;
    @Schema(description = "Дата доставки", example = "2026-03-21")
    private LocalDate deliveryDate;
    @Schema(description = "Временное окно доставки")
    private TimeWindowResponse timeWindow;
    @Schema(description = "Трек-номер", example = "TRK-10002")
    private String trackingNumber;
}
