package com.food.order_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.order_service.dto.FoodDto;
import com.food.order_service.dto.OrderDto;
import com.food.order_service.kafka.events.OrderEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static com.food.order_service.kafka.topics.KafkaTopics.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Test
    void postOrder_publishesKafka_andReturnsEvent() throws Exception {
        OrderDto dto = OrderDto.builder()
                .orderId("o-99")
                .userId("u-99")
                .address("Infinity Ave")
                .food(FoodDto.builder().name("Pasta").price(200).quantity(2).build())
                .build();

        String responseJson = mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderEvent responseBody = objectMapper.readValue(responseJson, OrderEvent.class);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(ORDER_CREATED);
        assertThat(keyCaptor.getValue()).isEqualTo("o-99");
        assertThat(eventCaptor.getValue().getTotalAmount()).isEqualTo(400);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getOrderId()).isEqualTo("o-99");
        assertThat(responseBody.getTotalAmount()).isEqualTo(400);
    }
}


