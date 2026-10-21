package com.app.orderservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.orderservice.model.OrderEventHistory;

public interface OrderEventHistoryRepository extends MongoRepository<OrderEventHistory, String> {
}
