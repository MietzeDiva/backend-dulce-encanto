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

    git clone https://github.com/MietzeDiva/backend-dulce-encanto.git
    cd backend-dulce-encanto

2. **Configurar variables de entorno:**
   Crea tu archivo `.env` a partir de la plantilla incluida:

    cp .env.example .env

3. **Levantar los contenedores:**

    docker-compose up -d --build

4. **Verificar que los servicios estén activos:**

    docker ps

   Los microservicios quedarán disponibles en los puertos `8081` al `8083` para conectarse con el frontend (`http://localhost:3000`).

---

## Configuración de Usuario Administrador

**Nota sobre el catálogo:** Al levantar el proyecto, el sistema ejecuta un `DataLoader` que precarga automáticamente 4 categorías y 12 productos (incluyendo stock, imágenes y precios de oferta) para que la tienda sea funcional desde el primer momento. Sin embargo, la tabla de **usuarios** sí inicia vacía. Para habilitar el acceso al panel `/admin`:

1. Crea una cuenta desde el formulario de registro en el frontend (`http://localhost:3000/registro`).
2. Ejecuta el siguiente comando en la terminal para otorgarle el rol `ADMIN` (ajusta el `id` si no es el primer usuario creado):

    docker exec -it mysql-dulce-encanto mysql -uroot -proot_dulce_encanto db_usuarios -e "UPDATE usuarios SET rol='ADMIN' WHERE id=1;"

3. Cierra sesión en la web y vuelve a iniciarla para cargar los permisos de administrador.

---

## Comandos Útiles de Docker

- **Ver logs en tiempo real:** `docker-compose logs -f`
- **Detener los servicios:** `docker-compose down`
- **Reiniciar contenedores:** `docker-compose restart`
