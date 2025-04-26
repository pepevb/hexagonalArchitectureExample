package com.example.hexagonal.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad del dominio que representa un pedido.
 * Esta clase contiene la lógica de negocio relacionada con los pedidos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    private String id;
    private String customerId;
    private double total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    
    /**
     * Método de dominio para confirmar un pedido.
     * Esta es una regla de negocio que pertenece al dominio.
     */
    public void confirm() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled order");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    /**
     * Método de dominio para cancelar un pedido.
     * Esta es una regla de negocio que pertenece al dominio.
     */
    public void cancel() {
        if (this.status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a confirmed order");
        }
        this.status = OrderStatus.CANCELLED;
    }
    
    /**
     * Factory method para crear un nuevo pedido.
     * Encapsula la lógica de creación de un pedido.
     */
    public static Order create(String customerId, double total) {
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .total(total)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
} 