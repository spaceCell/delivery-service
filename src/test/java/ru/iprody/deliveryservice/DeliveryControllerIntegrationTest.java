package ru.iprody.deliveryservice;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;
import ru.iprody.deliveryservice.domain.repository.DeliveryRepository;
import ru.iprody.deliveryservice.web.dto.DeliveryAddressRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.TimeWindowRequest;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerIntegrationTest {

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
    void shouldPerformCrudForDelivery() throws Exception {
        DeliveryRequest createRequest = new DeliveryRequest();
        createRequest.setOrderId(1L);
        createRequest.setStatus(DeliveryStatus.SCHEDULED);
        createRequest.setDeliveryAddress(new DeliveryAddressRequest("Nevsky 15", "Saint Petersburg", "191025", "RU"));
        createRequest.setDeliveryDate(LocalDate.of(2026, 3, 20));
        createRequest.setTimeWindow(new TimeWindowRequest(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        createRequest.setTrackingNumber("TRK-10001");

        MvcResult createResult = mockMvc.perform(post("/api/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.trackingNumber").value("TRK-10001"))
                .andReturn();

        JsonNode createdDelivery = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long deliveryId = createdDelivery.get("id").asLong();

        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(deliveryId));

        mockMvc.perform(get("/api/deliveries/{id}", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryId))
                .andExpect(jsonPath("$.deliveryAddress.city").value("Saint Petersburg"));

        DeliveryRequest updateRequest = new DeliveryRequest();
        updateRequest.setOrderId(1L);
        updateRequest.setStatus(DeliveryStatus.IN_TRANSIT);
        updateRequest.setDeliveryAddress(new DeliveryAddressRequest("Nevsky 15", "Saint Petersburg", "191025", "RU"));
        updateRequest.setDeliveryDate(LocalDate.of(2026, 3, 21));
        updateRequest.setTimeWindow(new TimeWindowRequest(LocalTime.of(12, 0), LocalTime.of(18, 0)));
        updateRequest.setTrackingNumber("TRK-10002");

        mockMvc.perform(put("/api/deliveries/{id}", deliveryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryId))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.trackingNumber").value("TRK-10002"))
                .andExpect(jsonPath("$.deliveryDate").value("2026-03-21"));

        mockMvc.perform(delete("/api/deliveries/{id}", deliveryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/deliveries/{id}", deliveryId))
                .andExpect(status().isNotFound());
    }
}
