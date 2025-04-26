package com.example.hexagonal.domain.model;

/**
 * Enum que representa los posibles estados de un pedido.
 * Esta es una parte del modelo de dominio.
 */
public enum OrderStatus {
    PENDING,    // Pedido creado pero no confirmado
    CONFIRMED,  // Pedido confirmado
    CANCELLED   // Pedido cancelado
} 