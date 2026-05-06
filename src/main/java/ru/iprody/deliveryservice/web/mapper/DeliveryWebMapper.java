package ru.iprody.deliveryservice.web.mapper;

import org.springframework.stereotype.Component;
import ru.iprody.deliveryservice.application.command.DeliveryCommand;
import ru.iprody.deliveryservice.application.dto.DeliveryAddressDetails;
import ru.iprody.deliveryservice.application.dto.DeliveryDetails;
import ru.iprody.deliveryservice.application.dto.TimeWindowDetails;
import ru.iprody.deliveryservice.web.dto.DeliveryAddressRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryAddressResponse;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryResponse;
import ru.iprody.deliveryservice.web.dto.TimeWindowRequest;
import ru.iprody.deliveryservice.web.dto.TimeWindowResponse;

@Component
public class DeliveryWebMapper {

    public DeliveryCommand toDeliveryCommand(DeliveryRequest deliveryRequest) {
        return new DeliveryCommand(
                deliveryRequest.getOrderId(),
                deliveryRequest.getStatus(),
                toDeliveryAddressDetails(deliveryRequest.getDeliveryAddress()),
                deliveryRequest.getDeliveryDate(),
                toTimeWindowDetails(deliveryRequest.getTimeWindow()),
                deliveryRequest.getTrackingNumber()
        );
    }

    public DeliveryResponse toDeliveryResponse(DeliveryDetails deliveryDetails) {
        return new DeliveryResponse(
                deliveryDetails.id(),
                deliveryDetails.orderId(),
                deliveryDetails.status(),
                toDeliveryAddressResponse(deliveryDetails.deliveryAddress()),
                deliveryDetails.deliveryDate(),
                toTimeWindowResponse(deliveryDetails.timeWindow()),
                deliveryDetails.trackingNumber()
        );
    }

    private DeliveryAddressDetails toDeliveryAddressDetails(DeliveryAddressRequest deliveryAddressRequest) {
        if (deliveryAddressRequest == null) {
            return null;
        }
        return new DeliveryAddressDetails(
                deliveryAddressRequest.getStreet(),
                deliveryAddressRequest.getCity(),
                deliveryAddressRequest.getPostalCode(),
                deliveryAddressRequest.getCountry()
        );
    }

    private TimeWindowDetails toTimeWindowDetails(TimeWindowRequest timeWindowRequest) {
        if (timeWindowRequest == null) {
            return null;
        }
        return new TimeWindowDetails(timeWindowRequest.getStartTime(), timeWindowRequest.getEndTime());
    }

    private DeliveryAddressResponse toDeliveryAddressResponse(DeliveryAddressDetails deliveryAddressDetails) {
        return new DeliveryAddressResponse(
                deliveryAddressDetails.street(),
                deliveryAddressDetails.city(),
                deliveryAddressDetails.postalCode(),
                deliveryAddressDetails.country()
        );
    }

    private TimeWindowResponse toTimeWindowResponse(TimeWindowDetails timeWindowDetails) {
        return new TimeWindowResponse(timeWindowDetails.startTime(), timeWindowDetails.endTime());
    }
}
