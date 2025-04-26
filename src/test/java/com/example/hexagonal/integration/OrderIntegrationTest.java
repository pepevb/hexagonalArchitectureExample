package com.example.hexagonal.integration;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para verificar que todas las capas funcionen correctamente juntas.
 * Estas pruebas utilizan el contexto completo de Spring Boot.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void whenCreateAndConfirmOrder_thenOrderShouldBeConfirmed() throws Exception {
        // Arrange
        String customerId = "123";
        double amount = 100.0;
        
        // Act - Crear pedido
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest(customerId, amount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.name()))
                .andReturn();
        
        // Extraer el ID del pedido creado
        String responseContent = createResult.getResponse().getContentAsString();
        Order createdOrder = objectMapper.readValue(responseContent, Order.class);
        String orderId = createdOrder.getId();
        
        // Act - Confirmar pedido
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.name()));
        
        // Verificar en la base de datos
        Order savedOrder = orderRepository.findById(orderId).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
    }
    
    @Test
    void whenCreateOrderWithInvalidData_thenShouldReturnBadRequest() throws Exception {
        // Arrange
        String customerId = "";
        double amount = -100.0;
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest(customerId, amount))))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenConfirmNonExistingOrder_thenShouldReturnNotFound() throws Exception {
        // Arrange
        String nonExistingOrderId = "non-existing-id";
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/confirm", nonExistingOrderId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenConfirmOrderTwice_thenSecondAttemptShouldReturnBadRequest() throws Exception {
        // Arrange
        String customerId = "123";
        double amount = 100.0;
        
        // Crear pedido
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest(customerId, amount))))
                .andExpect(status().isOk())
                .andReturn();
        
        // Extraer el ID del pedido creado
        String responseContent = createResult.getResponse().getContentAsString();
        Order createdOrder = objectMapper.readValue(responseContent, Order.class);
        String orderId = createdOrder.getId();
        
        // Confirmar pedido por primera vez
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk());
        
        // Act & Assert - Confirmar pedido por segunda vez
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isBadRequest());
    }
} 