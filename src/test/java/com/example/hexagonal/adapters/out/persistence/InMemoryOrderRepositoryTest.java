package com.example.hexagonal.adapters.out.persistence;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el repositorio InMemoryOrderRepository.
 * Estas pruebas verifican el comportamiento del repositorio en memoria.
 */
class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;
    private Order order1;
    private Order order2;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        
        // Crear pedidos de prueba
        order1 = Order.create("123", "Cliente 1", 100.0);
        order2 = Order.create("456", "Cliente 2", 200.0);
        
        // Guardar pedidos en el repositorio
        repository.save(order1);
        repository.save(order2);
    }
    
    @Test
    void whenSaveOrder_thenOrderShouldBeRetrieved() {
        // Arrange
        Order newOrder = Order.create("789", "Cliente 3", 300.0);
        
        // Act
        Order savedOrder = repository.save(newOrder);
        
        // Assert
        assertNotNull(savedOrder);
        assertEquals(newOrder.getId(), savedOrder.getId());
        
        // Verificar que se puede recuperar
        Optional<Order> foundOrder = repository.findById(newOrder.getId());
        assertTrue(foundOrder.isPresent());
        assertEquals(newOrder.getId(), foundOrder.get().getId());
    }
    
    @Test
    void whenFindById_thenOrderShouldBeReturned() {
        // Act
        Optional<Order> foundOrder = repository.findById(order1.getId());
        
        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order1.getId(), foundOrder.get().getId());
        assertEquals(order1.getCustomerId(), foundOrder.get().getCustomerId());
        assertEquals(order1.getAmount(), foundOrder.get().getAmount());
    }
    
    @Test
    void whenFindByIdWithNonExistingId_thenEmptyShouldBeReturned() {
        // Act
        Optional<Order> foundOrder = repository.findById("non-existing-id");
        
        // Assert
        assertFalse(foundOrder.isPresent());
    }
    
    @Test
    void whenFindAll_thenAllOrdersShouldBeReturned() {
        // Act
        List<Order> allOrders = repository.findAll();
        
        // Assert
        assertEquals(2, allOrders.size());
        assertTrue(allOrders.stream().anyMatch(order -> order.getId().equals(order1.getId())));
        assertTrue(allOrders.stream().anyMatch(order -> order.getId().equals(order2.getId())));
    }
    
    @Test
    void whenFindByCustomerId_thenCustomerOrdersShouldBeReturned() {
        // Arrange
        Order order3 = Order.create("123", "Cliente 1", 150.0);
        repository.save(order3);
        
        // Act
        List<Order> customerOrders = repository.findByCustomerId("123");
        
        // Assert
        assertEquals(2, customerOrders.size());
        assertTrue(customerOrders.stream().anyMatch(order -> order.getId().equals(order1.getId())));
        assertTrue(customerOrders.stream().anyMatch(order -> order.getId().equals(order3.getId())));
    }
    
    @Test
    void whenUpdateOrder_thenOrderShouldBeUpdated() {
        // Arrange
        order1.confirm();
        
        // Act
        Order updatedOrder = repository.save(order1);
        
        // Assert
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
        
        // Verificar que se actualizó en el repositorio
        Optional<Order> foundOrder = repository.findById(order1.getId());
        assertTrue(foundOrder.isPresent());
        assertEquals(OrderStatus.CONFIRMED, foundOrder.get().getStatus());
    }
} 