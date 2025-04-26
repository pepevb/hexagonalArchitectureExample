package com.example.hexagonal.application.services;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.ports.in.CreateOrderUseCase;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa el caso de uso para crear pedidos.
 * Esta clase orquesta la lógica de aplicación utilizando el dominio.
 */
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    
    private final OrderRepository orderRepository;
    
    @Override
    public Order createOrder(String customerId, double total) {
        // Utilizamos el factory method del dominio para crear el pedido
        Order order = Order.create(customerId, total);
        
        // Persistimos el pedido utilizando el repositorio
        return orderRepository.save(order);
    }
} 