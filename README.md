# Dulce Encanto — Backend (Microservicios)

Arquitectura de microservicios para la tienda en línea y panel de administración de **Dulce Encanto**, desarrollada con **Java 25**, **Spring Boot**, **MySQL** y **Docker**.

## Arquitectura del Sistema

El proyecto está dividido en tres microservicios independientes orquestados mediante `docker-compose`:

| Servicio | Carpeta | Descripción |
| :--- | :--- | :--- |
| **Microservicio de Usuarios** | `ms-usuarios` | Registro de cuentas, autenticación y gestión de roles (`CLIENTE` / `ADMIN`). |
| **Microservicio de Productos** | `ms-productos` | Gestión del catálogo, categorías, precios promocionales (ofertas) y control de inventario. |
| **Microservicio de Ventas** | `ms-ventas` | Procesamiento de compras, emisión de boletas y descuento de stock. |
| **Base de Datos** | `mysql-dulce-encanto` | Contenedor MySQL con las bases `db_usuarios`, `db_productos` y `db_ventas`. |

---

## Requisitos Previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución.
- [Git](https://git-scm.com/).

---

## Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/MietzeDiva/backend-dulce-encanto.git](https://github.com/MietzeDiva/backend-dulce-encanto.git)
   cd backend-dulce-encanto
