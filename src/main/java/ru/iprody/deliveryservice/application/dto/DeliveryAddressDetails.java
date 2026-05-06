package ru.iprody.deliveryservice.application.dto;

public record DeliveryAddressDetails(
        String street,
        String city,
        String postalCode,
        String country
) {
}
