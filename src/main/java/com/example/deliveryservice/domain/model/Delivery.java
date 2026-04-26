package com.example.deliveryservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DeliveryStatus status;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Embedded
    private TimeWindow timeWindow;

    @Column(nullable = false)
    private String trackingNumber;

    public Delivery(
            Long newOrderId,
            DeliveryStatus newStatus,
            DeliveryAddress newDeliveryAddress,
            LocalDate newDeliveryDate,
            TimeWindow newTimeWindow,
            String newTrackingNumber
    ) {
        changeOrderId(newOrderId);
        changeStatus(newStatus);
        changeDeliveryAddress(newDeliveryAddress);
        changeDeliveryDate(newDeliveryDate);
        changeTimeWindow(newTimeWindow);
        changeTrackingNumber(newTrackingNumber);
    }

    public void update(
            Long newOrderId,
            DeliveryStatus newStatus,
            DeliveryAddress newDeliveryAddress,
            LocalDate newDeliveryDate,
            TimeWindow newTimeWindow,
            String newTrackingNumber
    ) {
        changeOrderId(newOrderId);
        if (newStatus != null) {
            changeStatus(newStatus);
        }
        changeDeliveryAddress(newDeliveryAddress);
        changeDeliveryDate(newDeliveryDate);
        changeTimeWindow(newTimeWindow);
        changeTrackingNumber(newTrackingNumber);
    }

    public void changeOrderId(Long newOrderId) {
        if (newOrderId == null || newOrderId <= 0) {
            throw new IllegalArgumentException("Order id must be greater than zero");
        }
        orderId = newOrderId;
    }

    public void changeStatus(DeliveryStatus newStatus) {
        status = newStatus == null ? DeliveryStatus.CREATED : newStatus;
    }

    public void changeDeliveryAddress(DeliveryAddress newDeliveryAddress) {
        if (newDeliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address must be provided");
        }
        deliveryAddress = newDeliveryAddress;
    }

    public void changeDeliveryDate(LocalDate newDeliveryDate) {
        if (newDeliveryDate == null) {
            throw new IllegalArgumentException("Delivery date must be provided");
        }
        deliveryDate = newDeliveryDate;
    }

    public void changeTimeWindow(TimeWindow newTimeWindow) {
        if (newTimeWindow == null) {
            throw new IllegalArgumentException("Time window must be provided");
        }
        timeWindow = newTimeWindow;
    }

    public void changeTrackingNumber(String newTrackingNumber) {
        if (newTrackingNumber == null || newTrackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number must be provided");
        }
        trackingNumber = newTrackingNumber.trim();
    }
}
