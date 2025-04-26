package com.example.hexagonal.adapters.in.web;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.ports.in.ConfirmOrderUseCase;
import com.example.hexagonal.domain.ports.in.CreateOrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para el controlador OrderController.
 * Estas pruebas utilizan MockMvc para simular solicitudes HTTP.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CreateOrderUseCase createOrderUseCase;
    
    @MockBean
    private ConfirmOrderUseCase confirmOrderUseCase;
    
    @Test
    void whenCreateOrder_thenReturnCreatedOrder() throws Exception {
        // Arrange
        String customerId = "123";
        double amount = 100.0;
        Order order = Order.create(customerId, "Cliente Test", amount);
        
        when(createOrderUseCase.createOrder(eq(customerId), eq(amount))).thenReturn(order);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateOrderRequest(customerId, amount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.name()));
    }
    
    @Test
    void whenCreateOrderWithInvalidData_thenReturnBadRequest() throws Exception {
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
    void whenConfirmOrder_thenReturnConfirmedOrder() throws Exception {
        // Arrange
        String orderId = "123";
        Order order = Order.create("456", "Cliente Test", 100.0);
        order.confirm();
        
        when(confirmOrderUseCase.confirmOrder(eq(orderId))).thenReturn(order);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value(OrderStatus.CONFIRMED.name()));
    }
    
    @Test
    void whenConfirmNonExistingOrder_thenReturnNotFound() throws Exception {
        // Arrange
        String orderId = "123";
        
        when(confirmOrderUseCase.confirmOrder(eq(orderId)))
                .thenThrow(new IllegalArgumentException("Order not found"));
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenConfirmOrderInInvalidState_thenReturnBadRequest() throws Exception {
        // Arrange
        String orderId = "123";
        
        when(confirmOrderUseCase.confirmOrder(eq(orderId)))
                .thenThrow(new IllegalStateException("Order is already confirmed"));
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isBadRequest());
    }
} 