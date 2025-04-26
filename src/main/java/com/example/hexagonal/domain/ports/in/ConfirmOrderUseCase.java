package com.example.hexagonal.domain.ports.in;

import com.example.hexagonal.domain.model.Order;

/**
 * Puerto de entrada que define el caso de uso para confirmar un pedido.
 */
public interface ConfirmOrderUseCase {
    
    /**
     * Confirma un pedido existente.
     * 
     * @param orderId ID del pedido a confirmar
     * @return El pedido confirmado
     * @throws IllegalStateException si el pedido no puede ser confirmado
     */
    Order confirmOrder(String orderId);
} 