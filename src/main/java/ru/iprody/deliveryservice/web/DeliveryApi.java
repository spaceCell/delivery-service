package ru.iprody.deliveryservice.web;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryResponse;

@Tag(name = "Deliveries", description = "Операции с доставками")
public interface DeliveryApi {

    @Operation(summary = "Создать доставку")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Доставка создана"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @ResponseStatus(HttpStatus.CREATED)
    DeliveryResponse create(@RequestBody DeliveryRequest deliveryRequest);

    @Operation(summary = "Получить список доставок")
    @ApiResponse(responseCode = "200", description = "Список доставок получен")
    List<DeliveryResponse> getAll();

    @Operation(summary = "Получить доставку по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Доставка найдена"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    DeliveryResponse getById(@PathVariable("id") Long deliveryId);

    @Operation(summary = "Обновить доставку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Доставка обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    DeliveryResponse update(@PathVariable("id") Long deliveryId, @RequestBody DeliveryRequest deliveryRequest);

    @Operation(summary = "Удалить доставку")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Доставка удалена"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long deliveryId);
}
