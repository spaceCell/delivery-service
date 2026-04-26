package com.example.deliveryservice.application.dto;

public record DeliveryAddressDetails(
        String street,
        String city,
        String postalCode,
        String country
) {
}
