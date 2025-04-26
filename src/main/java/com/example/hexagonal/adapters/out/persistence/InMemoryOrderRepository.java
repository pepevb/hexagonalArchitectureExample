package com.example.hexagonal.adapters.out.persistence;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador secundario que implementa el repositorio de pedidos en memoria.
 * Esta implementación es útil para pruebas y desarrollo.
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<String, Order> orders = new HashMap<>();
    
    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
    
    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
    
    @Override
    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
} 