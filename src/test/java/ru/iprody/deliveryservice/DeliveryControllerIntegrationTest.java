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
    private static final String DELIVERIES_API = "/api/deliveries";
    private static final String DELIVERY_BY_ID_API = "/api/deliveries/{id}";
    private static final String JSON_ID = "$.id";
    private static final String JSON_STATUS = "$.status";
    private static final String JSON_TRACKING_NUMBER = "$.trackingNumber";
    private static final long ORDER_ID = 1L;
    private static final String STREET = "Nevsky 15";
    private static final String CITY = "Saint Petersburg";
    private static final String POSTAL_CODE = "191025";
    private static final String COUNTRY = "RU";
    private static final int YEAR_2026 = 2026;
    private static final int MARCH = 3;
    private static final int DAY_20 = 20;
    private static final int DAY_21 = 21;
    private static final int TEN_OCLOCK = 10;
    private static final int TWELVE_OCLOCK = 12;
    private static final int FOURTEEN_OCLOCK = 14;
    private static final int EIGHTEEN_OCLOCK = 18;
    private static final String TRACKING_NUMBER_CREATED = "TRK-10001";
    private static final String TRACKING_NUMBER_UPDATED = "TRK-10002";

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
        createRequest.setOrderId(ORDER_ID);
        createRequest.setStatus(DeliveryStatus.SCHEDULED);
        createRequest.setDeliveryAddress(new DeliveryAddressRequest(STREET, CITY, POSTAL_CODE, COUNTRY));
        createRequest.setDeliveryDate(LocalDate.of(YEAR_2026, MARCH, DAY_20));
        createRequest.setTimeWindow(new TimeWindowRequest(LocalTime.of(TEN_OCLOCK, 0), LocalTime.of(FOURTEEN_OCLOCK, 0)));
        createRequest.setTrackingNumber(TRACKING_NUMBER_CREATED);

        MvcResult createResult = mockMvc.perform(post(DELIVERIES_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_ID).isNumber())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath(JSON_STATUS).value("SCHEDULED"))
                .andExpect(jsonPath(JSON_TRACKING_NUMBER).value(TRACKING_NUMBER_CREATED))
                .andReturn();

        JsonNode createdDelivery = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long deliveryId = createdDelivery.get("id").asLong();

        mockMvc.perform(get(DELIVERIES_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(deliveryId));

        mockMvc.perform(get(DELIVERY_BY_ID_API, deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_ID).value(deliveryId))
                .andExpect(jsonPath("$.deliveryAddress.city").value(CITY));

        DeliveryRequest updateRequest = new DeliveryRequest();
        updateRequest.setOrderId(ORDER_ID);
        updateRequest.setStatus(DeliveryStatus.IN_TRANSIT);
        updateRequest.setDeliveryAddress(new DeliveryAddressRequest(STREET, CITY, POSTAL_CODE, COUNTRY));
        updateRequest.setDeliveryDate(LocalDate.of(YEAR_2026, MARCH, DAY_21));
        updateRequest.setTimeWindow(new TimeWindowRequest(LocalTime.of(TWELVE_OCLOCK, 0), LocalTime.of(EIGHTEEN_OCLOCK, 0)));
        updateRequest.setTrackingNumber(TRACKING_NUMBER_UPDATED);

        mockMvc.perform(put(DELIVERY_BY_ID_API, deliveryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_ID).value(deliveryId))
                .andExpect(jsonPath(JSON_STATUS).value("IN_TRANSIT"))
                .andExpect(jsonPath(JSON_TRACKING_NUMBER).value(TRACKING_NUMBER_UPDATED))
                .andExpect(jsonPath("$.deliveryDate").value("2026-03-21"));

        mockMvc.perform(delete(DELIVERY_BY_ID_API, deliveryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(DELIVERY_BY_ID_API, deliveryId))
                .andExpect(status().isNotFound());
    }
}
