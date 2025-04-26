package com.example.hexagonal.application.services;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.ports.in.ConfirmOrderUseCase;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Servicio de aplicación que implementa el caso de uso para confirmar pedidos.
 */
@Service
@RequiredArgsConstructor
public class ConfirmOrderService implements ConfirmOrderUseCase {
    
    private final OrderRepository orderRepository;
    
    @Override
    public Order confirmOrder(String orderId) {
        // Buscamos el pedido en el repositorio
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        // Utilizamos el método de dominio para confirmar el pedido
        order.confirm();
        
        // Persistimos el pedido actualizado
        return orderRepository.save(order);
    }
} 