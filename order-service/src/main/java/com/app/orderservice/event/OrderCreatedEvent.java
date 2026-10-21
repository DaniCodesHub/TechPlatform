package com.app.orderservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.orderservice.model.OrderStatus;

public class OrderCreatedEvent {
	
	private Long orderId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime date;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId,
                             BigDecimal total,
                             OrderStatus status,
                             LocalDateTime date) {
        this.orderId = orderId;
        this.total = total;
        this.status = status;
        this.date = date;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
