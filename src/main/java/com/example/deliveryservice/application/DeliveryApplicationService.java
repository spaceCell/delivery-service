package com.example.deliveryservice.application;

import java.util.List;

import com.example.deliveryservice.application.command.DeliveryCommand;
import com.example.deliveryservice.application.dto.DeliveryAddressDetails;
import com.example.deliveryservice.application.dto.DeliveryDetails;
import com.example.deliveryservice.application.dto.TimeWindowDetails;
import com.example.deliveryservice.common.ResourceNotFoundException;
import com.example.deliveryservice.domain.model.Delivery;
import com.example.deliveryservice.domain.model.DeliveryAddress;
import com.example.deliveryservice.domain.model.TimeWindow;
import com.example.deliveryservice.domain.repository.DeliveryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryApplicationService {

    private static final String DELIVERY_NOT_FOUND_MESSAGE = "Delivery with id %d was not found";

    private final DeliveryRepository deliveryRepository;

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    public DeliveryDetails create(DeliveryCommand deliveryCommand) {
        Delivery delivery = new Delivery(
                deliveryCommand.orderId(),
                deliveryCommand.status(),
                toDeliveryAddress(deliveryCommand.deliveryAddress()),
                deliveryCommand.deliveryDate(),
                toTimeWindow(deliveryCommand.timeWindow()),
                deliveryCommand.trackingNumber()
        );
        return toDeliveryDetails(deliveryRepository.save(delivery));
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    public List<DeliveryDetails> getAll() {
        return deliveryRepository.findAll()
                .stream()
                .map(this::toDeliveryDetails)
                .toList();
    }

    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    public DeliveryDetails getById(Long deliveryId) {
        return toDeliveryDetails(getDelivery(deliveryId));
    }

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    public DeliveryDetails update(Long deliveryId, DeliveryCommand deliveryCommand) {
        Delivery delivery = getDelivery(deliveryId);
        delivery.update(
                deliveryCommand.orderId(),
                deliveryCommand.status(),
                toDeliveryAddress(deliveryCommand.deliveryAddress()),
                deliveryCommand.deliveryDate(),
                toTimeWindow(deliveryCommand.timeWindow()),
                deliveryCommand.trackingNumber()
        );
        return toDeliveryDetails(delivery);
    }

    @Transactional
    @CircuitBreaker(name = "deliveryServiceCircuitBreaker")
    public void delete(Long deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) {
            throw new ResourceNotFoundException(DELIVERY_NOT_FOUND_MESSAGE.formatted(deliveryId));
        }
        deliveryRepository.deleteById(deliveryId);
    }

    private Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException(DELIVERY_NOT_FOUND_MESSAGE.formatted(deliveryId)));
    }

    private DeliveryAddress toDeliveryAddress(DeliveryAddressDetails deliveryAddressDetails) {
        if (deliveryAddressDetails == null) {
            throw new IllegalArgumentException("Delivery address must be provided");
        }
        return new DeliveryAddress(
                deliveryAddressDetails.street(),
                deliveryAddressDetails.city(),
                deliveryAddressDetails.postalCode(),
                deliveryAddressDetails.country()
        );
    }

    private TimeWindow toTimeWindow(TimeWindowDetails timeWindowDetails) {
        if (timeWindowDetails == null) {
            throw new IllegalArgumentException("Time window must be provided");
        }
        return new TimeWindow(timeWindowDetails.startTime(), timeWindowDetails.endTime());
    }

    private DeliveryDetails toDeliveryDetails(Delivery delivery) {
        return new DeliveryDetails(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                new DeliveryAddressDetails(
                        delivery.getDeliveryAddress().getStreet(),
                        delivery.getDeliveryAddress().getCity(),
                        delivery.getDeliveryAddress().getPostalCode(),
                        delivery.getDeliveryAddress().getCountry()
                ),
                delivery.getDeliveryDate(),
                new TimeWindowDetails(
                        delivery.getTimeWindow().getStartTime(),
                        delivery.getTimeWindow().getEndTime()
                ),
                delivery.getTrackingNumber()
        );
    }
}
