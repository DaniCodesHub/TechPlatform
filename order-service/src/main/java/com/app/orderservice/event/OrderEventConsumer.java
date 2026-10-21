package com.app.orderservice.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.app.orderservice.model.OrderEventHistory;
import com.app.orderservice.repository.OrderEventHistoryRepository;

@Service
public class OrderEventConsumer {

    private final OrderEventHistoryRepository repository;

    public OrderEventConsumer(OrderEventHistoryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "order-created", groupId = "techplatform-group")
    public void consume(OrderCreatedEvent event) {

        OrderEventHistory history = new OrderEventHistory();

        history.setOrderId(event.getOrderId());
        history.setTotal(event.getTotal());
        history.setStatus(event.getStatus().name());
        history.setDate(event.getDate());

        repository.save(history);
    }
}