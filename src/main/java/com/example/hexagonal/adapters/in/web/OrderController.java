package com.example.hexagonal.adapters.in.web;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.ports.in.ConfirmOrderUseCase;
import com.example.hexagonal.domain.ports.in.CreateOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador primario que expone los casos de uso a través de una API REST.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderUseCase.createOrder(
                request.getCustomerId(),
                request.getTotal()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable String orderId) {
        try {
            Order order = confirmOrderUseCase.confirmOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Clase interna para la solicitud de creación de pedido
    @lombok.Data
    static class CreateOrderRequest {
        private String customerId;
        private double total;
    }
} 