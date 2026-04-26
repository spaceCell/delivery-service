package ru.iprody.deliveryservice.application;

import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iprody.deliveryservice.application.command.DeliveryCommand;
import ru.iprody.deliveryservice.application.dto.DeliveryAddressDetails;
import ru.iprody.deliveryservice.application.dto.DeliveryDetails;
import ru.iprody.deliveryservice.application.dto.TimeWindowDetails;
import ru.iprody.deliveryservice.common.ResourceNotFoundException;
import ru.iprody.deliveryservice.domain.model.Delivery;
import ru.iprody.deliveryservice.domain.model.DeliveryAddress;
import ru.iprody.deliveryservice.domain.model.TimeWindow;
import ru.iprody.deliveryservice.domain.repository.DeliveryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryApplicationService {

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
            throw new ResourceNotFoundException("Delivery with id " + deliveryId + " was not found");
        }
        deliveryRepository.deleteById(deliveryId);
    }

    private Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + deliveryId + " was not found"));
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
