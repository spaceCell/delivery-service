package ru.iprody.deliveryservice.web.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на создание или обновление доставки")
public class DeliveryRequest {

    @Schema(description = "Идентификатор заказа", example = "1")
    private Long orderId;
    @Schema(description = "Статус доставки", example = "SCHEDULED")
    private DeliveryStatus status;
    @Schema(description = "Адрес доставки")
    private DeliveryAddressRequest deliveryAddress;
    @Schema(description = "Дата доставки", example = "2026-03-20")
    private LocalDate deliveryDate;
    @Schema(description = "Временное окно доставки")
    private TimeWindowRequest timeWindow;
    @Schema(description = "Трек-номер", example = "TRK-10001")
    private String trackingNumber;
}
