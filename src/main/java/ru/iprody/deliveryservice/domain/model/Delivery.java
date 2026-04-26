package ru.iprody.deliveryservice.domain.model;

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

    public Delivery(Long orderId, DeliveryStatus status, DeliveryAddress deliveryAddress, LocalDate deliveryDate,
                    TimeWindow timeWindow, String trackingNumber) {
        changeOrderId(orderId);
        changeStatus(status);
        changeDeliveryAddress(deliveryAddress);
        changeDeliveryDate(deliveryDate);
        changeTimeWindow(timeWindow);
        changeTrackingNumber(trackingNumber);
    }

    public void update(Long orderId, DeliveryStatus status, DeliveryAddress deliveryAddress, LocalDate deliveryDate,
                       TimeWindow timeWindow, String trackingNumber) {
        changeOrderId(orderId);
        changeStatus(status);
        changeDeliveryAddress(deliveryAddress);
        changeDeliveryDate(deliveryDate);
        changeTimeWindow(timeWindow);
        changeTrackingNumber(trackingNumber);
    }

    public void changeOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order id must be greater than zero");
        }
        this.orderId = orderId;
    }

    public void changeStatus(DeliveryStatus status) {
        this.status = status == null ? DeliveryStatus.CREATED : status;
    }

    public void changeDeliveryAddress(DeliveryAddress deliveryAddress) {
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address must be provided");
        }
        this.deliveryAddress = deliveryAddress;
    }

    public void changeDeliveryDate(LocalDate deliveryDate) {
        if (deliveryDate == null) {
            throw new IllegalArgumentException("Delivery date must be provided");
        }
        this.deliveryDate = deliveryDate;
    }

    public void changeTimeWindow(TimeWindow timeWindow) {
        if (timeWindow == null) {
            throw new IllegalArgumentException("Time window must be provided");
        }
        this.timeWindow = timeWindow;
    }

    public void changeTrackingNumber(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number must be provided");
        }
        this.trackingNumber = trackingNumber.trim();
    }
}
