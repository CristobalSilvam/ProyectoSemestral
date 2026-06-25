# SearchSport Backend

Backend del proyecto **SearchSport**, desarrollado con **Spring Boot**.  
Expone una API REST para autenticación, gestión de usuarios, recintos deportivos, canchas, reservas y roles.

El backend se conecta a una base de datos **MySQL alojada en Aiven** y está desplegado en **Render**.

---

## Enlaces del proyecto

- Swagger producción: https://searchsport-backend.onrender.com/swagger-ui.html
- Backend producción: https://searchsport-backend.onrender.com
- Documentos de entrega 2: `/docse2`

---

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Render
- Aiven MySQL
- Swagger / OpenAPI
- JUnit 5
- Mockito

---

## Funcionalidades principales

- Registro público de usuarios.
- Login con JWT.
- Retorno de rol de usuario en el login.
- Control de acceso según rol.
- Consulta pública de recintos.
- Consulta pública de canchas.
- Consulta pública de comunas.
- Gestión de reservas autenticadas.
- Visualización de reservas del usuario autenticado.
- Cancelación de reservas.
- Gestión administrativa de usuarios.
- Gestión administrativa de recintos.
- Aprobación y desaprobación de recintos.
- Asignación de dueño a recinto.
- Manejo de errores en formato JSON.
- CORS configurado para frontend local y frontend desplegado en Vercel.

> Nota: la integración con pasarela de pago queda considerada como mejora futura.

---

## Roles utilizados

| Rol | ID | Descripción |
|---|---:|---|
| CLIENTE | 1 | Usuario normal que puede registrarse, iniciar sesión y reservar canchas |
| DUENO | 2 | Usuario encargado de la gestión de recintos |
| ADMIN | 3 | Usuario administrador con acceso al panel administrativo |

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
│           └── DemoApplicationTests.java
├── pom.xml
└── Dockerfile