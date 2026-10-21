package com.app.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.orderservice.model.Order;
import com.app.orderservice.model.OrderStatus;
import com.app.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public List<Order> findAll() {
		return orderService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Order> findById(@PathVariable Long id) {
		Order order = orderService.findById(id);

		if (order == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(order);
	}

	@PostMapping
	public ResponseEntity<Order> create(@RequestBody Order order) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(order));
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<Order> updateStatus(
	        @PathVariable Long id,
	        @RequestParam OrderStatus status) {

	    Order order = orderService.updateStatus(id, status);

	    if (order == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok(order);
	}
	
	@PatchMapping("/{id}/cancel")
	public ResponseEntity<Order> cancel(@PathVariable Long id) {
	    Order order = orderService.cancel(id);

	    if (order == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok(order);
	}
}
