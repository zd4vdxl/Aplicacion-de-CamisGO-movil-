# Tienda de Camisetas — App Android (Kotlin + Jetpack Compose)

App móvil nativa que consume la API REST de .NET Core (misma API que usa la
aplicación web) para navegar el catálogo, agregar productos al carrito,
personalizar camisetas y confirmar la compra.

## Stack utilizado
- **Kotlin** + **Jetpack Compose** (Material Design 3), siguiendo el enfoque
  declarativo visto en la guía G3.
- **Retrofit + Gson + OkHttp** para consumir la API REST, siguiendo el patrón
  de la guía G2 (RetrofitService, data classes generadas desde JSON, etc.).
- **Coroutines + ViewModel + StateFlow** para manejar los estados de carga,
  éxito y error de cada pantalla.
- **Navigation Compose** para moverse entre pantallas.
- **Coil** para cargar las imágenes de las camisetas (`ImagenUrl`).

## ⚠️ Antes de correrla: configura la URL de tu API

Abre:
```
app/src/main/java/sv/org/arrupe/tiendacamisetas/data/remote/RetrofitClient.kt
```
y cambia la constante `BASE_URL`:

- Si corres la API con `dotnet run` en tu misma PC y pruebas en el
  **emulador de Android Studio**, usa `http://10.0.2.2:PUERTO/`
  (10.0.2.2 apunta al localhost de tu computadora desde el emulador).
- Si pruebas en un **celular físico**, usa la IP de tu red local, ej.
  `http://192.168.1.50:PUERTO/`.
- Si tu API corre en HTTPS con certificado válido (producción/Azure), usa
  esa URL directamente y puedes quitar `usesCleartextTraffic` del
  `AndroidManifest.xml` y el archivo `network_security_config.xml`.

## Ajusta los DTOs a la forma real de tu JSON

Los `data class` en `data/model/` (Cliente, LoginResponse, CarritoItem,
Pedido, etc.) están escritos siguiendo la lógica de tu base de datos y tus
endpoints, en **camelCase** (el formato por defecto de `System.Text.Json`
en .NET Core). Si tu API devuelve los campos en PascalCase u otros nombres,
solo necesitas:
1. Abrir el `data class` correspondiente.
2. Renombrar el campo o agregarle `@SerializedName("NombreReal")` de Gson.

## Flujo de la app
1. **Login / Registro** → `POST /api/Auth/login` y `POST /api/Auth/registro`.
   El token JWT se guarda con `SessionManager` (SharedPreferences) y se
   agrega automáticamente a cada petición protegida vía `AuthInterceptor`.
2. **Catálogo** → `GET /api/Camisetas`, mostrado en una grilla con imagen,
   equipo, temporada y precio.
3. **Detalle de camiseta** → el usuario elige talla (según stock real de
   `GET /api/Bodega`) y una personalización opcional (`GET /api/Servicios`),
   luego agrega al carrito con `POST /api/Carritos/item`.
4. **Carrito** → `GET /api/Carritos/cliente/{clienteId}`, permite editar
   cantidades (`PUT /api/Carritos/item/{id}`) o eliminar ítems
   (`DELETE /api/Carritos/item/{id}`).
5. **Pago (checkout)** → pantalla simulada de tarjeta; al confirmar llama a
   `POST /api/Ventas/confirmar-compra`, que en tu backend ejecuta
   `sp_ConfirmarCompra` (crea el pedido, registra el pago, descuenta stock
   y vacía el carrito, todo en una transacción).
6. **Confirmación** → pantalla de éxito con el número de pedido.
7. **Historial de pedidos** → `GET /api/Ventas/cliente/{clienteId}`.

## Cómo abrir el proyecto
1. Descomprime el `.zip`.
2. Abre Android Studio → **Open** → selecciona la carpeta `TiendaCamisetasApp`.
3. Deja que Gradle sincronice (puede tardar la primera vez).
4. Corre tu API de .NET Core en paralelo (`dotnet run`).
5. Ejecuta la app en un emulador o dispositivo.

## Notas
- No se incluyen íconos de lanzador personalizados; Android Studio generará
  uno por defecto o puedes agregar los tuyos desde
  `File > New > Image Asset`.
- Este proyecto no incluye las pantallas de administración (CRUD de
  camisetas/bodega), ya que el enfoque pedido es el flujo del cliente:
  ver catálogo → carrito → pagar → confirmar. Si necesitas un panel admin,
  se puede agregar reutilizando el mismo `ApiService`.
