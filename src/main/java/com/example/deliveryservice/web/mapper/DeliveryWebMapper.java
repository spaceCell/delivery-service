package com.example.deliveryservice.web.mapper;

import com.example.deliveryservice.application.command.DeliveryCommand;
import com.example.deliveryservice.application.dto.DeliveryAddressDetails;
import com.example.deliveryservice.application.dto.DeliveryDetails;
import com.example.deliveryservice.application.dto.TimeWindowDetails;
import com.example.deliveryservice.web.dto.DeliveryAddressRequest;
import com.example.deliveryservice.web.dto.DeliveryAddressResponse;
import com.example.deliveryservice.web.dto.DeliveryRequest;
import com.example.deliveryservice.web.dto.DeliveryResponse;
import com.example.deliveryservice.web.dto.TimeWindowRequest;
import com.example.deliveryservice.web.dto.TimeWindowResponse;
import org.springframework.stereotype.Component;

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
