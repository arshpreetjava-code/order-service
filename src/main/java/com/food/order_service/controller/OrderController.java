package com.food.order_service.controller;

import com.food.order_service.dto.OrderDto;
import com.food.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order.
     * <p>
     * Accepts an OrderDto in the request body, delegates creation to the
     * OrderService and returns the created order representation (or service response).
     *
     * @param orderDto the order payload sent by the client
     * @return ResponseEntity containing the result of order creation and HTTP 200
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto){
        return ResponseEntity.ok().body(orderService.createOrder(orderDto));
    }
}
