package com.example.hexagonal.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Order del dominio.
 * Estas pruebas verifican el comportamiento de la entidad de forma aislada.
 */
class OrderTest {

    @Test
    void whenCreateOrder_thenOrderShouldBeCreatedWithPendingStatus() {
        // Arrange & Act
        String customerId = "123";
        double amount = 100.0;
        Order order = Order.create(customerId, "Cliente Test", amount);
        
        // Assert
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(amount, order.getAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void whenConfirmOrder_thenStatusShouldBeConfirmed() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        
        // Act
        order.confirm();
        
        // Assert
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void whenCancelOrder_thenStatusShouldBeCancelled() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        
        // Act
        order.cancel();
        
        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void whenConfirmAlreadyConfirmedOrder_thenShouldThrowException() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        order.confirm();
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.confirm());
    }

    @Test
    void whenCancelAlreadyCancelledOrder_thenShouldThrowException() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        order.cancel();
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.cancel());
    }

    @Test
    void whenConfirmCancelledOrder_thenShouldThrowException() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        order.cancel();
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.confirm());
    }

    @Test
    void whenCancelConfirmedOrder_thenShouldThrowException() {
        // Arrange
        Order order = Order.create("123", "Cliente Test", 100.0);
        order.confirm();
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.cancel());
    }
} 