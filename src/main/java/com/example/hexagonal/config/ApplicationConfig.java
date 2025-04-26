package com.example.hexagonal.config;

import com.example.hexagonal.adapters.out.persistence.InMemoryOrderRepository;
import com.example.hexagonal.application.services.ConfirmOrderService;
import com.example.hexagonal.application.services.CreateOrderService;
import com.example.hexagonal.domain.ports.in.ConfirmOrderUseCase;
import com.example.hexagonal.domain.ports.in.CreateOrderUseCase;
import com.example.hexagonal.domain.ports.out.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la aplicación que conecta los puertos con sus adaptadores.
 * Esta clase es responsable de la inyección de dependencias.
 */
@Configuration
public class ApplicationConfig {
    
    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }
    
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository orderRepository) {
        return new CreateOrderService(orderRepository);
    }
    
    @Bean
    public ConfirmOrderUseCase confirmOrderUseCase(OrderRepository orderRepository) {
        return new ConfirmOrderService(orderRepository);
    }
} 