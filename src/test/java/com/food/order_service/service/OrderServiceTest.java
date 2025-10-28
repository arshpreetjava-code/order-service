package com.food.order_service.service;

import com.food.order_service.dto.FoodDto;
import com.food.order_service.dto.OrderDto;
import com.food.order_service.kafka.events.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static com.food.order_service.kafka.topics.KafkaTopics.ORDER_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderDto sampleOrderDto;

    @BeforeEach
    void setUp() {
        sampleOrderDto = OrderDto.builder()
                .orderId("order-123")
                .userId("user-1")
                .address("221B Baker Street")
                .food(FoodDto.builder()
                        .type("pizza")
                        .name("Margherita")
                        .toppings(new String[]{"basil"})
                        .quantity(2)
                        .price(350)
                        .build())
                .build();
    }

    @Test
    void createOrder_sendsToKafka_andReturnsEvent() {
        OrderEvent result = orderService.createOrder(sampleOrderDto);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(ORDER_CREATED);
        assertThat(keyCaptor.getValue()).isEqualTo(sampleOrderDto.getOrderId());

        OrderEvent sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getOrderId()).isEqualTo(sampleOrderDto.getOrderId());
        assertThat(sentEvent.getUserId()).isEqualTo(sampleOrderDto.getUserId());
        assertThat(sentEvent.getAddress()).isEqualTo(sampleOrderDto.getAddress());
        assertThat(sentEvent.getFood()).isEqualTo(sampleOrderDto.getFood());
        assertThat(sentEvent.getTotalAmount()).isEqualTo(2L * 350L);
        assertThat(sentEvent.getCreateTime()).isNotNull();

        // Service returns the same event that was created
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("createTime")
                .isEqualTo(sentEvent);
    }

    @Test
    void createOrder_whenKafkaThrows_wrapsAndPropagates() {
        doThrow(new RuntimeException("broker down")).when(kafkaTemplate)
                .send(anyString(), anyString(), any(OrderEvent.class));

        assertThatThrownBy(() -> orderService.createOrder(sampleOrderDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send message to Kafka");
    }
}


