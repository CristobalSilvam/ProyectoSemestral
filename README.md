# SearchSport Backend

Backend del proyecto **SearchSport**, desarrollado con **Spring Boot**.  
Expone una API REST para autenticación de usuarios, gestión de recintos deportivos, canchas, reservas y roles de usuario.

El backend se conecta a una base de datos **MySQL alojada en Aiven** y está desplegado en **Render**.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Docker
- Render
- Aiven MySQL
- Swagger / OpenAPI

---

## Funcionalidades principales

- Registro público de usuarios.
- Login con JWT.
- Retorno de rol de usuario en el login.
- Control de navegación según rol:
    - Cliente / usuario normal.
    - Dueño de recinto.
    - Administrador.
- Consulta pública de recintos.
- Consulta pública de canchas.
- Consulta pública de comunas.
- Gestión de reservas autenticadas.
- Visualización de reservas del usuario autenticado.
- Cancelación y pago de reservas.
- Manejo de errores en formato JSON.
- CORS configurado para frontend local y frontend desplegado en Vercel.

---

## Roles utilizados

| Rol | ID | Descripción |
|---|---:|---|
| USER / Cliente | 1 | Usuario normal que puede registrarse, iniciar sesión y reservar canchas |
| DUENO / Dueño | 2 | Usuario encargado de gestión de recintos |
| ADMIN | 3 | Usuario administrador con acceso al panel admin |

---

## Estructura general del proyecto

```txt
demo/
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
├── pom.xml
└── DockerFile