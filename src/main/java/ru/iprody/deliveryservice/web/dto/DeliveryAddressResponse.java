package ru.iprody.deliveryservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Адрес доставки в ответе")
public class DeliveryAddressResponse {

    private String street;
    private String city;
    private String postalCode;
    private String country;
}
