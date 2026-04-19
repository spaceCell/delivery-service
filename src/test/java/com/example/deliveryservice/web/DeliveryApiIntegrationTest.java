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
        String payload = objectMapper.writeValueAsString(validRequest("CREATED"));

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.orderId").value(1001))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.deliveryAddress.city").value("Moscow"))
                .andExpect(jsonPath("$.trackingNumber").value("TRK-1001"));
    }

    @Test
    void getAllDeliveriesReturnsCreatedItems() throws Exception {
        createDeliveryAndReturnId();

        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1001));
    }

    @Test
    void updateDeliveryReturnsUpdatedPayload() throws Exception {
        Long id = createDeliveryAndReturnId();
        Map<String, Object> updateRequest = validRequest("IN_TRANSIT");
        updateRequest.put("trackingNumber", "TRK-1001-UPD");
        updateRequest.put("deliveryDate", "2026-04-22");

        mockMvc.perform(
                        put("/api/deliveries/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.trackingNumber").value("TRK-1001-UPD"))
                .andExpect(jsonPath("$.deliveryDate").value("2026-04-22"));
    }

    @Test
    void deleteDeliveryRemovesEntity() throws Exception {
        Long id = createDeliveryAndReturnId();

        mockMvc.perform(delete("/api/deliveries/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/deliveries/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Delivery with id " + id + " was not found"));
    }

    @Test
    void createDeliveryWithInvalidTimeWindowReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest("CREATED");
        invalidRequest.put("timeWindow", timeWindow("14:00:00", "10:00:00"));

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Time window end must be after start"));
    }

    @Test
    void createDeliveryWithOrderIdZeroReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest("CREATED");
        invalidRequest.put("orderId", 0);

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Order id must be greater than zero"));
    }

    @Test
    void createDeliveryWithBlankTrackingNumberReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest("CREATED");
        invalidRequest.put("trackingNumber", "   ");

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tracking number must be provided"));
    }

    @Test
    void createDeliveryWithoutAddressReturnsBadRequest() throws Exception {
        Map<String, Object> invalidRequest = validRequest("CREATED");
        invalidRequest.remove("deliveryAddress");

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Delivery address must be provided"));
    }

    @Test
    void createDeliveryWithInvalidStatusReturnsBadRequest() throws Exception {
        String invalidStatusPayload = """
                {
                  "orderId": 1001,
                  "status": "BAD_STATUS",
                  "deliveryAddress": {
                    "street": "Lenina 1",
                    "city": "Moscow",
                    "postalCode": "101000",
                    "country": "Russia"
                  },
                  "deliveryDate": "2026-04-21",
                  "timeWindow": {
                    "startTime": "10:00:00",
                    "endTime": "14:00:00"
                  },
                  "trackingNumber": "TRK-1001"
                }
                """;

        mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidStatusPayload)
                )
                .andExpect(status().isBadRequest());
    }

    private Long createDeliveryAndReturnId() throws Exception {
        String payload = objectMapper.writeValueAsString(validRequest("CREATED"));
        MvcResult result = mockMvc.perform(
                        post("/api/deliveries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Map<String, Object> validRequest(String status) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("orderId", 1001);
        request.put("status", status);
        request.put("deliveryAddress", address());
        request.put("deliveryDate", "2026-04-21");
        request.put("timeWindow", timeWindow("10:00:00", "14:00:00"));
        request.put("trackingNumber", "TRK-1001");
        return request;
    }

    private Map<String, Object> address() {
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("street", "Lenina 1");
        address.put("city", "Moscow");
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
