# Ejemplo de Arquitectura Hexagonal en Java

Este proyecto demuestra la implementación de la arquitectura hexagonal (también conocida como "Ports and Adapters") en Java utilizando Spring Boot.

## ¿Qué es la Arquitectura Hexagonal?

La arquitectura hexagonal es un patrón de diseño que promueve la separación de responsabilidades y la independencia de frameworks externos. En esta arquitectura:

- El **dominio** está en el centro (la lógica de negocio)
- Los **puertos** son interfaces que definen cómo se comunica el dominio con el exterior
- Los **adaptadores** implementan estos puertos y conectan con tecnologías externas

## Explicación Detallada de la Arquitectura Hexagonal

La arquitectura hexagonal, también conocida como "Ports and Adapters", fue propuesta por Alistair Cockburn en 2005. Esta arquitectura se centra en mantener el dominio de la aplicación aislado de las tecnologías externas y frameworks.

### Principios Fundamentales

1. **El dominio está en el centro**: La lógica de negocio debe estar en el núcleo de la aplicación, sin dependencias externas.
2. **Puertos definen la comunicación**: Las interfaces (puertos) definen cómo el dominio se comunica con el exterior.
3. **Adaptadores implementan los puertos**: Los adaptadores conectan el dominio con tecnologías externas.

### Capas de la Arquitectura

#### 1. Dominio (Core)
- Contiene las entidades y la lógica de negocio pura
- No tiene dependencias con frameworks externos
- Define las reglas de negocio y el comportamiento de las entidades

#### 2. Puertos
- **Puertos de entrada**: Interfaces que definen los casos de uso (qué puede hacer la aplicación)
- **Puertos de salida**: Interfaces que definen cómo el dominio necesita interactuar con el exterior (persistencia, servicios externos)

#### 3. Adaptadores
- **Adaptadores primarios**: Implementan los puertos de entrada (controladores, UI)
- **Adaptadores secundarios**: Implementan los puertos de salida (repositorios, servicios externos)

#### 4. Servicios de Aplicación
- Orquestan los casos de uso utilizando el dominio
- Conectan los puertos de entrada con los puertos de salida

### Flujo de la Aplicación

1. Un adaptador primario (por ejemplo, un controlador REST) recibe una solicitud
2. El adaptador utiliza un puerto de entrada (caso de uso) para procesar la solicitud
3. El servicio de aplicación implementa el caso de uso utilizando el dominio
4. El dominio ejecuta la lógica de negocio
5. El servicio de aplicación utiliza un puerto de salida para persistir datos o interactuar con servicios externos
6. Un adaptador secundario implementa el puerto de salida
7. El resultado se devuelve al adaptador primario, que genera la respuesta

## Estructura del Proyecto

```
src/main/java/com/example/hexagonal/
├── domain/                  # Capa de dominio
│   ├── model/               # Entidades del dominio
│   └── ports/               # Puertos (interfaces)
│       ├── in/              # Puertos de entrada (casos de uso)
│       └── out/             # Puertos de salida (repositorios)
├── application/             # Capa de aplicación
│   └── services/            # Servicios que implementan los casos de uso
├── adapters/                # Adaptadores
│   ├── in/                  # Adaptadores primarios (controladores, UI)
│   │   └── web/             # Adaptador web (REST API)
│   └── out/                 # Adaptadores secundarios (persistencia, servicios externos)
│       └── persistence/     # Adaptador de persistencia
└── config/                  # Configuración de la aplicación
```

## Componentes Principales

### Dominio

- **Order**: Entidad que representa un pedido con su lógica de negocio
- **OrderStatus**: Enum que representa los estados posibles de un pedido

### Puertos

- **CreateOrderUseCase**: Puerto de entrada para crear pedidos
- **ConfirmOrderUseCase**: Puerto de entrada para confirmar pedidos
- **OrderRepository**: Puerto de salida para persistir pedidos

### Adaptadores

- **OrderController**: Adaptador primario que expone la API REST
- **InMemoryOrderRepository**: Adaptador secundario que implementa el repositorio en memoria

## Cómo Ejecutar

1. Clona este repositorio
2. Ejecuta `mvn spring-boot:run`
3. La aplicación estará disponible en `http://localhost:8080`

## Endpoints de la API

- `POST /api/orders`: Crear un nuevo pedido
- `POST /api/orders/{orderId}/confirm`: Confirmar un pedido existente

## Ventajas de la Arquitectura Hexagonal

1. **Independencia de frameworks**: El dominio no depende de Spring, JPA u otros frameworks
2. **Testabilidad**: Puedes probar el dominio de forma aislada
3. **Flexibilidad**: Puedes cambiar fácilmente la implementación de los adaptadores
4. **Separación clara de responsabilidades**: Cada componente tiene un propósito específico

## Ejemplos de Pruebas

La arquitectura hexagonal facilita la creación de pruebas unitarias y de integración para cada capa. A continuación, se muestran ejemplos de cómo probar los diferentes componentes:

### 1. Pruebas del Dominio

Las entidades del dominio se pueden probar de forma aislada, ya que no tienen dependencias externas:

```java
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
void whenConfirmAlreadyConfirmedOrder_thenShouldThrowException() {
    // Arrange
    Order order = Order.create("123", "Cliente Test", 100.0);
    order.confirm();
    
    // Act & Assert
    assertThrows(IllegalStateException.class, () -> order.confirm());
}
```

### 2. Pruebas de Servicios de Aplicación

Los servicios de aplicación se pueden probar utilizando mocks para los puertos de salida:

```java
@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private CreateOrderService createOrderService;
    
    @Test
    void whenCreateOrder_thenOrderShouldBeSaved() {
        // Arrange
        String customerId = "123";
        double amount = 100.0;
        Order expectedOrder = Order.create(customerId, "Cliente Test", amount);
        
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);
        
        // Act
        Order result = createOrderService.createOrder(customerId, amount);
        
        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(amount, result.getAmount());
        verify(orderRepository).save(any(Order.class));
    }
}
```

### 3. Pruebas de Adaptadores Primarios (Controladores)

Los controladores se pueden probar utilizando MockMvc para simular solicitudes HTTP:

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
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
        
        when(createOrderUseCase.createOrder(customerId, amount)).thenReturn(order);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"123\",\"amount\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.amount").value(amount));
    }
}
```

### 4. Pruebas de Adaptadores Secundarios (Repositorios)

Los repositorios se pueden probar utilizando una base de datos en memoria para pruebas:

```java
@DataJpaTest
class JpaOrderRepositoryTest {

    @Autowired
    private JpaOrderRepository jpaOrderRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    void whenSaveOrder_thenOrderShouldBePersisted() {
        // Arrange
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId("123");
        orderEntity.setCustomerId("Cliente Test");
        orderEntity.setAmount(100.0);
        orderEntity.setStatus(OrderStatus.PENDING);
        
        // Act
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);
        
        // Assert
        assertNotNull(savedOrder.getId());
        
        // Verificar que se guardó correctamente
        OrderEntity foundOrder = entityManager.find(OrderEntity.class, savedOrder.getId());
        assertNotNull(foundOrder);
        assertEquals("Cliente Test", foundOrder.getCustomerId());
        assertEquals(100.0, foundOrder.getAmount());
        assertEquals(OrderStatus.PENDING, foundOrder.getStatus());
    }
}
```

### 5. Pruebas de Integración

Las pruebas de integración verifican que todas las capas funcionen correctamente juntas:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void whenCreateAndConfirmOrder_thenOrderShouldBeConfirmed() {
        // Arrange
        String customerId = "123";
        double amount = 100.0;
        
        // Act - Crear pedido
        ResponseEntity<Order> createResponse = restTemplate.postForEntity(
            "/api/orders",
            new CreateOrderRequest(customerId, amount),
            Order.class
        );
        
        assertTrue(createResponse.getStatusCode().is2xxSuccessful());
        Order createdOrder = createResponse.getBody();
        assertNotNull(createdOrder);
        
        // Act - Confirmar pedido
        ResponseEntity<Order> confirmResponse = restTemplate.postForEntity(
            "/api/orders/" + createdOrder.getId() + "/confirm",
            null,
            Order.class
        );
        
        assertTrue(confirmResponse.getStatusCode().is2xxSuccessful());
        Order confirmedOrder = confirmResponse.getBody();
        assertNotNull(confirmedOrder);
        assertEquals(OrderStatus.CONFIRMED, confirmedOrder.getStatus());
        
        // Verificar en la base de datos
        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
    }
}
```

## Extensión del Proyecto

Para extender este proyecto, podrías:

1. Implementar un adaptador de persistencia con JPA/Hibernate
2. Añadir más casos de uso (cancelar pedido, listar pedidos, etc.)
3. Implementar validaciones adicionales en el dominio
4. Añadir pruebas unitarias para cada capa 