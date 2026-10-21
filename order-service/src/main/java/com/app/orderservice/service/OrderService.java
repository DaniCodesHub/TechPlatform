package com.app.orderservice.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.app.orderservice.dto.ProductDto;
import com.app.orderservice.model.Order;
import com.app.orderservice.model.OrderItem;
import com.app.orderservice.model.OrderStatus;
import com.app.orderservice.repository.OrderRepository;
import com.app.orderservice.event.OrderEventProducer;
import com.app.orderservice.event.OrderCreatedEvent;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final WebClient productWebClient;
	private final OrderEventProducer orderEventProducer;

	public OrderService(OrderRepository orderRepository, WebClient productWebClient, OrderEventProducer orderEventProducer) {
		this.orderRepository = orderRepository;
		this.productWebClient = productWebClient;
		this.orderEventProducer = orderEventProducer;
	}

	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	public Order findById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	public Order create(Order order) {

		BigDecimal total = BigDecimal.ZERO;

		for (OrderItem item : order.getItems()) {

			ProductDto product = productWebClient.get().uri("/api/products/{id}", item.getProductId()).retrieve()
					.bodyToMono(ProductDto.class).block();

			if (product == null) {
				throw new IllegalArgumentException("Producto no encontrado: " + item.getProductId());
			}

			if (product.getStock() < item.getQuantity()) {
				throw new IllegalArgumentException("Stock insuficiente para el producto: " + product.getId());
			}

			productWebClient.patch()
					.uri(uriBuilder -> uriBuilder.path("/api/products/{id}/stock")
							.queryParam("quantity", item.getQuantity()).build(item.getProductId()))
					.retrieve().bodyToMono(ProductDto.class).block();

			item.setUnitPrice(product.getPrice());
			item.setOrder(order);

			BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

			total = total.add(subtotal);
		}

		order.setTotal(total);
		order.setStatus(OrderStatus.PENDING);
		
		Order savedOrder = orderRepository.save(order);
		
		OrderCreatedEvent event = new OrderCreatedEvent(
		        savedOrder.getId(),
		        savedOrder.getTotal(),
		        savedOrder.getStatus(),
		        savedOrder.getDate()
		);
		
		orderEventProducer.sendOrderCreated(event);

		return savedOrder;
	}
	
	public Order updateStatus(Long id, OrderStatus status) {
	    Order order = findById(id);

	    if (order == null) {
	        return null;
	    }

	    order.setStatus(status);

	    return orderRepository.save(order);
	}
	
	public Order cancel(Long id) {
	    Order order = findById(id);

	    if (order == null) {
	        return null;
	    }

	    for (OrderItem item : order.getItems()) {
	        productWebClient.patch()
	            .uri(uriBuilder -> uriBuilder
	                .path("/api/products/{id}/stock/restore")
	                .queryParam("quantity", item.getQuantity())
	                .build(item.getProductId()))
	            .retrieve()
	            .bodyToMono(ProductDto.class)
	            .block();
	    }

	    order.setStatus(OrderStatus.CANCELLED);

	    return orderRepository.save(order);
	}
}
