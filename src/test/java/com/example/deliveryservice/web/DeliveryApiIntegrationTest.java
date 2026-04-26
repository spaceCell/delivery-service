package com.example.deliveryservice.web;

import com.example.deliveryservice.domain.repository.DeliveryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryApiIntegrationTest {

    private static final String DELIVERY_API = "/api/deliveries";
    private static final String DELIVERY_BY_ID_API = "/api/deliveries/{id}";
    private static final String STATUS_CREATED = "CREATED";
    private static final String STATUS_IN_TRANSIT = "IN_TRANSIT";
    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_STATUS = "$.status";
    private static final String JSON_PATH_MESSAGE = "$.message";
    private static final String JSON_PATH_TRACKING_NUMBER = "$.trackingNumber";
    private static final String JSON_FIELD_ID = "id";
    private static final String FIELD_ORDER_ID = "orderId";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_TRACKING_NUMBER = "trackingNumber";
    private static final String FIELD_DELIVERY_DATE = "deliveryDate";
    private static final String FIELD_DELIVERY_ADDRESS = "deliveryAddress";
    private static final String FIELD_TIME_WINDOW = "timeWindow";
    private static final long ORDER_ID = 1001L;
    private static final long ZERO_ORDER_ID = 0L;
    private static final String CITY_MOSCOW = "Moscow";
    private static final String TRACKING_NUMBER = "TRK-1001";
    private static final String TRACKING_NUMBER_UPDATED = "TRK-1001-UPD";
    private static final String TRACKING_NUMBER_NO_STATUS = "TRK-1001-NO-STATUS";
    private static final String DELIVERY_DATE = "2026-04-21";
    private static final String UPDATED_DELIVERY_DATE = "2026-04-22";
    private static final String TIME_10 = "10:00:00";
    private static final String TIME_14 = "14:00:00";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
    }

    @Test
    void createDeliveryReturnsCreated() throws Exception {
        String payload = objectMapper.writeValueAsString(validRequest(STATUS_CREATED));

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_ID).isNumber())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(STATUS_CREATED))
                .andExpect(jsonPath("$.deliveryAddress.city").value(CITY_MOSCOW))
                .andExpect(jsonPath(JSON_PATH_TRACKING_NUMBER).value(TRACKING_NUMBER));
    }

    @Test
    void getAllDeliveriesReturnsCreatedItems() throws Exception {
        createDeliveryAndReturnId();

        mockMvc.perform(get(DELIVERY_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(ORDER_ID));
    }

    @Test
    void updateDeliveryReturnsUpdatedPayload() throws Exception {
        Long id = createDeliveryAndReturnId();
        Map<String, Object> updateRequest = validRequest(STATUS_IN_TRANSIT);
        updateRequest.put(FIELD_TRACKING_NUMBER, TRACKING_NUMBER_UPDATED);
        updateRequest.put(FIELD_DELIVERY_DATE, UPDATED_DELIVERY_DATE);

        mockMvc.perform(
                        put(DELIVERY_BY_ID_API, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(id))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(STATUS_IN_TRANSIT))
                .andExpect(jsonPath(JSON_PATH_TRACKING_NUMBER).value(TRACKING_NUMBER_UPDATED))
                .andExpect(jsonPath("$.deliveryDate").value(UPDATED_DELIVERY_DATE));
    }

    @Test
    void updateDeliveryWithoutStatusKeepsCurrentStatus() throws Exception {
        Map<String, Object> createRequest = validRequest(STATUS_IN_TRANSIT);
        String createPayload = objectMapper.writeValueAsString(createRequest);
        MvcResult createResult = mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createPayload)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_PATH_STATUS).value(STATUS_IN_TRANSIT))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get(JSON_FIELD_ID).asLong();

        Map<String, Object> updateRequest = validRequest(STATUS_CREATED);
        updateRequest.remove(FIELD_STATUS);
        updateRequest.put(FIELD_TRACKING_NUMBER, TRACKING_NUMBER_NO_STATUS);

        mockMvc.perform(
                        put(DELIVERY_BY_ID_API, id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ID).value(id))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(STATUS_IN_TRANSIT))
                .andExpect(jsonPath(JSON_PATH_TRACKING_NUMBER).value(TRACKING_NUMBER_NO_STATUS));
    }

    @Test
    void deleteDeliveryRemovesEntity() throws Exception {
        Long id = createDeliveryAndReturnId();

        mockMvc.perform(delete(DELIVERY_BY_ID_API, id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(DELIVERY_BY_ID_API, id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Delivery with id " + id + " was not found"));
    }

    @Test
    void createDeliveryWithInvalidTimeWindowReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.put(FIELD_TIME_WINDOW, timeWindow(TIME_14, TIME_10));

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Time window end must be after start"));
    }

    @Test
    void createDeliveryWithOrderIdZeroReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.put(FIELD_ORDER_ID, ZERO_ORDER_ID);

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Order id must be greater than zero"));
    }

    @Test
    void createDeliveryWithBlankTrackingNumberReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.put(FIELD_TRACKING_NUMBER, "   ");

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Tracking number must be provided"));
    }

    @Test
    void createDeliveryWithoutAddressReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.remove(FIELD_DELIVERY_ADDRESS);

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Delivery address must be provided"));
    }

    @Test
    void createDeliveryWithInvalidStatusReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.put(FIELD_STATUS, "BAD_STATUS");
        String invalidStatusPayload = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidStatusPayload)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void businessErrorsDoNotOpenCircuitBreaker() throws Exception {
        long missingId = Long.MAX_VALUE;

        mockMvc.perform(get(DELIVERY_BY_ID_API, missingId))
                .andExpect(status().isNotFound());

        Map<String, Object> invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.put(FIELD_ORDER_ID, ZERO_ORDER_ID);

        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(DELIVERY_BY_ID_API, missingId - 1))
                .andExpect(status().isNotFound());

        invalidRequest = validRequest(STATUS_CREATED);
        invalidRequest.remove(FIELD_DELIVERY_ADDRESS);
        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest());

        // If business exceptions are ignored by resilience4j, the breaker stays closed
        // and a valid request is still processed instead of returning 503.
        mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest(STATUS_CREATED)))
                )
                .andExpect(status().isCreated());
    }

    private Long createDeliveryAndReturnId() throws Exception {
        String payload = objectMapper.writeValueAsString(validRequest(STATUS_CREATED));
        MvcResult result = mockMvc.perform(
                        post(DELIVERY_API)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get(JSON_FIELD_ID).asLong();
    }

    private Map<String, Object> validRequest(String status) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put(FIELD_ORDER_ID, ORDER_ID);
        request.put(FIELD_STATUS, status);
        request.put(FIELD_DELIVERY_ADDRESS, address());
        request.put(FIELD_DELIVERY_DATE, DELIVERY_DATE);
        request.put(FIELD_TIME_WINDOW, timeWindow(TIME_10, TIME_14));
        request.put(FIELD_TRACKING_NUMBER, TRACKING_NUMBER);
        return request;
    }

    private Map<String, Object> address() {
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("street", "Lenina 1");
        address.put("city", CITY_MOSCOW);
        address.put("postalCode", "101000");
        address.put("country", "Russia");
        return address;
    }

    private Map<String, Object> timeWindow(String startTime, String endTime) {
        Map<String, Object> window = new LinkedHashMap<>();
        window.put("startTime", startTime);
        window.put("endTime", endTime);
        return window;
    }
}
