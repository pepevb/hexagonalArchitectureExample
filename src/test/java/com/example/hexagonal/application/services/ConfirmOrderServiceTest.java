package com.example.hexagonal.application.services;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de aplicación ConfirmOrderService.
 * Estas pruebas utilizan mocks para simular el comportamiento del repositorio.
 */
@ExtendWith(MockitoExtension.class)
class ConfirmOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private ConfirmOrderService confirmOrderService;
    
    private String orderId;
    private Order order;
    
    @BeforeEach
    void setUp() {
        orderId = "123";
        order = Order.create("456", "Cliente Test", 100.0);
    }
    
    @Test
    void whenConfirmExistingOrder_thenOrderShouldBeConfirmed() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        // Act
        Order result = confirmOrderService.confirmOrder(orderId);
        
        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void whenConfirmNonExistingOrder_thenShouldThrowException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            confirmOrderService.confirmOrder(orderId)
        );
        
        // Verify that save was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void whenConfirmAlreadyConfirmedOrder_thenShouldThrowException() {
        // Arrange
        order.confirm();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            confirmOrderService.confirmOrder(orderId)
        );
        
        // Verify that save was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void whenConfirmCancelledOrder_thenShouldThrowException() {
        // Arrange
        order.cancel();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            confirmOrderService.confirmOrder(orderId)
        );
        
        // Verify that save was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
} 