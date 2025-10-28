package com.food.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.order_service.dto.FoodDto;
import com.food.order_service.dto.OrderDto;
import com.food.order_service.kafka.events.OrderEvent;
import com.food.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returnsOk_withServiceResponse() throws Exception {
        OrderDto dto = OrderDto.builder()
                .orderId("o-1")
                .userId("u-1")
                .address("Main St")
                .food(FoodDto.builder().name("Burger").price(100).quantity(3).build())
                .build();

        OrderEvent response = OrderEvent.builder()
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .address(dto.getAddress())
                .food(dto.getFood())
                .totalAmount(300)
                .build();

        Mockito.when(orderService.createOrder(any(OrderDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}

