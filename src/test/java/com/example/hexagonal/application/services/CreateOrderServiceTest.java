package com.example.hexagonal.application.services;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de aplicación CreateOrderService.
 * Estas pruebas utilizan mocks para simular el comportamiento del repositorio.
 */
@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private CreateOrderService createOrderService;
    
    private String customerId;
    private double amount;
    private Order expectedOrder;
    
    @BeforeEach
    void setUp() {
        customerId = "123";
        amount = 100.0;
        expectedOrder = Order.create(customerId, "Cliente Test", amount);
    }
    
    @Test
    void whenCreateOrder_thenOrderShouldBeSaved() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);
        
        // Act
        Order result = createOrderService.createOrder(customerId, amount);
        
        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(amount, result.getAmount());
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void whenCreateOrderWithNegativeAmount_thenShouldThrowException() {
        // Arrange
        double negativeAmount = -100.0;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            createOrderService.createOrder(customerId, negativeAmount)
        );
        
        // Verify that the repository was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void whenCreateOrderWithEmptyCustomerId_thenShouldThrowException() {
        // Arrange
        String emptyCustomerId = "";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            createOrderService.createOrder(emptyCustomerId, amount)
        );
        
        // Verify that the repository was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void whenCreateOrderWithNullCustomerId_thenShouldThrowException() {
        // Arrange
        String nullCustomerId = null;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            createOrderService.createOrder(nullCustomerId, amount)
        );
        
        // Verify that the repository was never called
        verify(orderRepository, never()).save(any(Order.class));
    }
} 