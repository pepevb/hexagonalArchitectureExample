package com.example.hexagonal.domain.ports.out;

import com.example.hexagonal.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida que define cómo el dominio necesita interactuar con la persistencia.
 * Esta interfaz define las operaciones que el dominio necesita para persistir datos.
 */
public interface OrderRepository {
    
    /**
     * Guarda un pedido en el repositorio.
     * 
     * @param order El pedido a guardar
     * @return El pedido guardado
     */
    Order save(Order order);
    
    /**
     * Busca un pedido por su ID.
     * 
     * @param id ID del pedido a buscar
     * @return El pedido encontrado o un Optional vacío si no existe
     */
    Optional<Order> findById(String id);
    
    /**
     * Obtiene todos los pedidos.
     * 
     * @return Lista de todos los pedidos
     */
    List<Order> findAll();
    
    /**
     * Busca pedidos por ID de cliente.
     * 
     * @param customerId ID del cliente
     * @return Lista de pedidos del cliente
     */
    List<Order> findByCustomerId(String customerId);
} 