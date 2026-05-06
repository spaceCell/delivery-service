package ru.iprody.deliveryservice.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.iprody.deliveryservice.application.DeliveryApplicationService;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryResponse;
import ru.iprody.deliveryservice.web.mapper.DeliveryWebMapper;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryApplicationService deliveryApplicationService;
    private final DeliveryWebMapper deliveryWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse create(@RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.create(deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    @GetMapping
    public List<DeliveryResponse> getAll() {
        return deliveryApplicationService.getAll()
                .stream()
                .map(deliveryWebMapper::toDeliveryResponse)
                .toList();
    }

    @Override
    @GetMapping("/{id}")
    public DeliveryResponse getById(@PathVariable("id") Long deliveryId) {
        return deliveryWebMapper.toDeliveryResponse(deliveryApplicationService.getById(deliveryId));
    }

    @Override
    @PutMapping("/{id}")
    public DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest) {
        return deliveryWebMapper.toDeliveryResponse(
                deliveryApplicationService.update(deliveryId, deliveryWebMapper.toDeliveryCommand(deliveryRequest))
        );
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long deliveryId) {
        deliveryApplicationService.delete(deliveryId);
    }
}
