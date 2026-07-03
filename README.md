# SearchSport Backend

Backend del proyecto **SearchSport**, desarrollado con **Spring Boot**.

Este backend expone una **API REST** para autenticación, gestión de usuarios, recintos deportivos, canchas, reservas, disponibilidad de horarios, roles administrativos e integración de pagos mediante MercadoPago.

El backend se conecta a una base de datos **MySQL alojada en Aiven** y está desplegado en **Render**.

---

## Enlaces del proyecto

- Backend producción: https://searchsport-backend.onrender.com
- Swagger producción: https://searchsport-backend.onrender.com/swagger-ui.html
- Frontend producción: https://front-taller.vercel.app
- Documentos de entrega: `/docs`
- Documentos entrega 2: `/docse2`

---

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Render
- Aiven MySQL
- Swagger / OpenAPI
- MercadoPago SDK
- JUnit 5
- Mockito
- JaCoCo
- JMeter

---

## Funcionalidades principales

### Autenticación y seguridad

- Registro público de usuarios.
- Login con JWT.
- Retorno de token y rol del usuario autenticado.
- Control de acceso según rol.
- Protección de endpoints administrativos.
- Validación de rutas públicas y privadas.
- CORS configurado para frontend local y frontend desplegado en Vercel.

### Recintos y canchas

- Consulta pública de recintos deportivos.
- Consulta de detalle de recinto.
- Consulta de canchas asociadas a un recinto.
- Gestión administrativa de recintos.
- Aprobación y desaprobación de recintos.
- Asignación de dueño a recinto.
- Gestión administrativa de canchas.
- Gestión de precio base de cancha.
- Gestión de imágenes de recinto.

### Reservas y disponibilidad

- Consulta de bloques horarios disponibles.
- Creación de reservas autenticadas.
- Validación para evitar reservas en horarios pasados.
- Validación para evitar doble reserva de una misma cancha, fecha y horario.
- Visualización de reservas del usuario autenticado.
- Cancelación de reservas.
- Estados de reserva: pendiente, pagada y cancelada.
- Consulta de comprobante de reserva.

### Pagos

- Integración con MercadoPago.
- Creación de preferencia de pago.
- Redirección al checkout de MercadoPago.
- Retorno a frontend según resultado del pago.
- Confirmación de reserva pagada.
- Registro de medio de pago asociado a la reserva.

### Administración

- Gestión de usuarios.
- Cambio de rol de usuarios.
- Desactivación lógica de usuarios.
- Gestión de recintos.
- Gestión de canchas.
- Visualización y control de reservas.
- Endpoints protegidos para rol ADMIN.

---

## Roles utilizados

| Rol | ID | Descripción |
|---|---:|---|
| CLIENTE | 1 | Usuario normal que puede registrarse, iniciar sesión, buscar recintos y reservar canchas |
| DUENO | 2 | Usuario encargado de administrar su recinto y sus canchas |
| ADMIN | 3 | Usuario administrador con acceso al panel administrativo |

---

## Estados de reserva

| Estado | ID | Descripción |
|---|---:|---|
| Pendiente | 1 | Reserva creada, pero aún sin pago confirmado |
| Pagada | 2 | Reserva confirmada después del pago |
| Cancelada | 3 | Reserva anulada y el horario vuelve a estar disponible |

---

## Estructura general del proyecto

```txt
ProyectoSemestral/
├── src/
│   ├── main/
│   │   ├── java/com/example/searchsport/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── util/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/searchsport/
│           ├── controller/
│           ├── service/
│           └── DemoApplicationTests.java
│
├── pom.xml
├── Dockerfile
└── README.md

Arquitectura del backend

El backend sigue una arquitectura por capas:

Controller → Service → Repository → Entity → Base de datos
Controller

Expone los endpoints REST que consume el frontend.

Service

Contiene la lógica de negocio, como disponibilidad, reservas, pagos, usuarios y administración.

Repository

Gestiona el acceso a la base de datos mediante Spring Data JPA.

Entity

Representa las tablas principales de la base de datos.

DTO

Permite enviar y recibir datos sin exponer directamente las entidades internas.

Base de datos

Motor utilizado:

MySQL 8.4

Proveedor cloud:

Aiven MySQL

Tablas principales:

usuario
rol
recinto
cancha
deporte
reserva
estado_reserva
tarifa
horario_especial
direccion
comuna
region
coordenada
imagen
email
telefono
review
Variables de entorno

El backend utiliza variables de entorno para evitar dejar credenciales dentro del código.

Variables principales
PORT=8080

SPRING_DATASOURCE_URL=jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?sslMode=REQUIRED&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=USUARIO_AIVEN
SPRING_DATASOURCE_PASSWORD=PASSWORD_AIVEN

JWT_SECRET=CLAVE_SECRETA_JWT
JWT_EXPIRATION=86400000

MERCADOPAGO_ACCESS_TOKEN=TOKEN_MERCADOPAGO
FRONTEND_URL=http://localhost:3000
MERCADOPAGO_AUTO_RETURN=false
En producción Render
SPRING_DATASOURCE_URL=jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?sslMode=REQUIRED&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=USUARIO_AIVEN
SPRING_DATASOURCE_PASSWORD=PASSWORD_AIVEN

JWT_SECRET=CLAVE_SECRETA_JWT
JWT_EXPIRATION=86400000

MERCADOPAGO_ACCESS_TOKEN=TOKEN_MERCADOPAGO
FRONTEND_URL=https://front-taller.vercel.app
MERCADOPAGO_AUTO_RETURN=true

JAVA_VERSION=17

Opcional para manejar zona horaria local:

JAVA_TOOL_OPTIONS=-Duser.timezone=America/Santiago
Configuración local

Archivo:

src/main/resources/application.properties

Configuración sugerida:

server.port=${PORT:8080}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/searchsport?serverTimezone=UTC}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET:SearchSportSecretKeySoloParaDesarrolloLocalCambiarEnProduccion2026}
jwt.expiration=${JWT_EXPIRATION:86400000}

mercadopago.access-token=${MERCADOPAGO_ACCESS_TOKEN:}
frontend.url=${FRONTEND_URL:http://localhost:3000}
mercadopago.auto-return=${MERCADOPAGO_AUTO_RETURN:false}
Instalación y ejecución local

Instalar dependencias y compilar:

./mvnw clean install

Ejecutar backend:

./mvnw spring-boot:run

En Windows PowerShell:

.\mvnw spring-boot:run

El backend quedará disponible en:

http://localhost:8080

Swagger local:

http://localhost:8080/swagger-ui.html
Ejecución con variables en PowerShell

Ejemplo para correr conectado a Aiven:

$env:SPRING_DATASOURCE_URL="jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?sslMode=REQUIRED&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME="USUARIO_AIVEN"
$env:SPRING_DATASOURCE_PASSWORD="PASSWORD_AIVEN"

$env:JWT_SECRET="CLAVE_SECRETA_JWT"
$env:JWT_EXPIRATION="86400000"

$env:MERCADOPAGO_ACCESS_TOKEN="TOKEN_MERCADOPAGO"
$env:FRONTEND_URL="http://localhost:3000"
$env:MERCADOPAGO_AUTO_RETURN="false"

.\mvnw spring-boot:run
Endpoints principales
Autenticación
POST /api/auth/register
POST /api/auth/login
Usuarios
GET /api/usuarios/me
PATCH /api/usuarios/me
Recintos
GET /api/recintos
GET /api/recintos/{id}
GET /api/recintos/mi-recinto
POST /api/recintos
POST /api/recintos/{id}/imagenes
GET /api/recintos/{id}/imagenes
DELETE /api/recintos/imagenes/{idImagen}
Canchas
GET /api/canchas/recinto/{recintoId}
POST /api/canchas
PATCH /api/canchas/{canchaId}/precio
Disponibilidad
GET /api/disponibilidad
Reservas
POST /api/reservas
GET /api/reservas/mis-reservas
GET /api/reservas/{id}
POST /api/reservas/{id}/cancelar
POST /api/reservas/{id}/pago
MercadoPago
POST /api/mercadopago/crear-preferencia
Administración
GET /api/admin/usuarios
PATCH /api/admin/usuarios/{id}/rol
PATCH /api/admin/usuarios/{id}/estado

GET /api/admin/recintos
PUT /api/admin/recintos/{id}

GET /api/admin/canchas
GET /api/admin/canchas/opciones
PUT /api/admin/canchas/{id}

GET /api/admin/reservas
PATCH /api/admin/reservas/{id}/estado
Flujo principal de reserva
1. El usuario inicia sesión.
2. El frontend consulta recintos disponibles.
3. El usuario selecciona un recinto y una cancha.
4. El backend calcula los bloques horarios disponibles.
5. El usuario crea una reserva.
6. La reserva queda en estado pendiente.
7. El backend crea una preferencia de pago en MercadoPago.
8. El usuario paga en MercadoPago.
9. MercadoPago redirige al frontend.
10. El frontend confirma el pago con el backend.
11. La reserva queda marcada como pagada.
12. El usuario puede ver la reserva y su comprobante.
Regla para evitar doble reserva

La disponibilidad de horarios se calcula en backend.

Un bloque horario no se muestra como disponible cuando ya existe una reserva para la misma:

cancha + fecha + hora_inicio + hora_fin

Los estados que bloquean disponibilidad son:

Pendiente
Pagada

El estado cancelado no bloquea disponibilidad.

Seguridad

El backend utiliza JWT para autenticar usuarios.

El token se envía desde el frontend en el header:

Authorization: Bearer TOKEN

Las rutas administrativas están protegidas para usuarios con rol ADMIN.

Las rutas del dueño están protegidas para usuarios con rol DUENO.

Las rutas públicas, como consulta de recintos o comunas, pueden ser accedidas sin autenticación.

Pruebas

El proyecto incluye pruebas y documentación de criterios de aceptación.

Tipos de pruebas consideradas:

Tests unitarios.
Tests con Mockito.
Tests de integración.
Tests funcionales manuales.
Tests de carga.
Tests de estrés.
Reporte de cobertura con JaCoCo.

Ejecutar tests:

./mvnw test

En Windows:

.\mvnw test

Generar reporte de cobertura JaCoCo, si está configurado:

./mvnw clean test jacoco:report

Reporte Surefire:

target/surefire-reports

Reporte JaCoCo:

target/site/jacoco/index.html
Despliegue en Render

El backend se despliega en Render usando el repositorio de GitHub.

Configuración general:

Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/*.jar

Variables necesarias en Render:

SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
JWT_EXPIRATION
MERCADOPAGO_ACCESS_TOKEN
FRONTEND_URL
MERCADOPAGO_AUTO_RETURN
JAVA_VERSION

Después de modificar variables en Render, se debe realizar un nuevo deploy.

CORS

El backend debe permitir solicitudes desde:

http://localhost:3000
https://front-taller.vercel.app

Esto permite que el frontend funcione tanto en local como en producción.

Recomendaciones para la demo

Antes de presentar:

Verificar que Render esté activo.
Verificar Swagger.
Probar login.
Probar una reserva.
Probar retorno de MercadoPago.
Revisar que la reserva aparezca como pagada.
Revisar panel administrador.
No mostrar variables de entorno ni credenciales.
No mostrar tokens, claves de Aiven ni claves de MercadoPago.
Estado actual

El backend se encuentra funcional para demostrar el flujo principal de SearchSport:

Autenticación → búsqueda de recintos → disponibilidad → reserva → pago → comprobante → administración

El sistema está preparado para defender una arquitectura desacoplada con frontend en Vercel, backend en Render y base de datos MySQL en Aiven.

Mejoras futuras
Mayor cobertura de tests de integración.
Notificaciones por correo.
Expiración automática de reservas pendientes.
Dashboard con métricas para dueños de recintos.
Filtros avanzados por deporte, comuna, precio y horario.
Mejora de reportes administrativos.
Optimización de consultas e índices.
Historial de pagos más detallado.
Autores

Proyecto académico desarrollado para Taller Aplicado de Programación - Duoc UC.

SearchSport.