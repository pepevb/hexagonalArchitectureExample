package com.example.hexagonal.domain.ports.in;

import com.example.hexagonal.domain.model.Order;

/**
 * Puerto de entrada que define el caso de uso para crear un pedido.
 * Esta interfaz define cómo el exterior puede interactuar con el dominio.
 */
public interface CreateOrderUseCase {
    
    /**
     * Crea un nuevo pedido.
     * 
     * @param customerId ID del cliente
     * @param total Total del pedido
     * @return El pedido creado
     */
    Order createOrder(String customerId, double total);
} 