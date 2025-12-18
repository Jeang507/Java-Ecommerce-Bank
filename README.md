# YulianaApp  
Sistema Integrado de E-commerce y Banco  
Aplicación de Escritorio en Java

## Descripción General

**YulianaApp** es un sistema integrado desarrollado en **Java**, compuesto por dos módulos principales que funcionan de manera conjunta:

- **E-commerce**: tienda virtual de ropa para bebés.
- **Bank**: sistema bancario simulado responsable de la autenticación financiera y la gestión del dinero.

El sistema permite a los usuarios registrarse, iniciar sesión, seleccionar productos, gestionar un carrito de compras y realizar pagos donde **el monto es descontado directamente del banco**, simulando una transacción real mediante **comunicación por sockets TCP**.

El proyecto aplica principios de **Programación Orientada a Objetos**, **arquitectura en capas** y **separación clara de responsabilidades**, diferenciando la lógica comercial de la lógica financiera.

Proyecto desarrollado con la colaboración de **Beira Barria**.

---

## Visión General del Sistema

El proyecto está conformado por **dos aplicaciones que trabajan juntas**:

### Módulo E-commerce
- Gestión de usuarios de la tienda
- Catálogo de productos
- Carrito de compras
- Solicitud de pagos al banco
- Generación de factura tras pago aprobado

### Módulo Bank
- Autenticación de usuarios bancarios
- Gestión de cuentas y saldo
- Autorización y ejecución de retiros
- Atención de solicitudes de pago provenientes del e-commerce

> El e-commerce **no administra dinero**.  
> Toda operación financiera es responsabilidad exclusiva del banco.

---

## Flujo de Pago Integrado

1. El usuario confirma la compra en el e-commerce.
2. El e-commerce solicita las credenciales bancarias.
3. Se envía una solicitud de pago al banco mediante sockets TCP.
4. El banco valida las credenciales.
5. El banco verifica el saldo disponible.
6. Si el saldo es suficiente:
   - Se realiza el **retiro bancario**
   - El banco confirma la transacción
   - El e-commerce genera la factura
7. Si el saldo es insuficiente:
   - El banco rechaza la operación
   - El e-commerce notifica al usuario

---

## Arquitectura General

Ambos módulos siguen una **arquitectura en capas**, manteniendo independencia lógica, pero comunicándose mediante una capa de red bien definida.

---

## Módulo E-commerce

### app
- **EcommerceApp**  
  Punto de entrada del sistema de tienda.

### model
- **Producto**  
  Representa los productos del catálogo.
- **UsuarioEcommerce**  
  Representa a los usuarios de la tienda.

### service
- **CarritoService**  
  Gestión del carrito y cálculo del total.
- **UserStoreEcommerce**  
  Registro, autenticación y persistencia de usuarios.
- **PagoService**  
  Coordina el proceso de pago y la comunicación con el banco.

### network
- **ConexionBanco**  
  Cliente TCP encargado de enviar solicitudes al banco y recibir respuestas.

### util
- **PasswordEncryption**  
  Encriptación de contraseñas (SHA-256 + Base64).
- **FacturaTXTGenerator**  
  Generación automática de facturas en formato TXT.

### ui
- LoginCliente  
- RegisterCliente  
- Tienda  
- Carrito  
- Pago  
- CuentaUsuario  

### ui/layout
- **WrapLayout**  
  Layout adaptable para mostrar productos en forma de catálogo.

---

## Módulo Bank

El módulo **Bank** funciona como un **servidor bancario independiente**, encargado de toda la lógica financiera del sistema.  
Este módulo es la **autoridad central del dinero** y valida cada transacción solicitada por el e-commerce.

### Responsabilidades del Banco
- Autenticación de usuarios bancarios
- Gestión de cuentas y saldo
- Autorización y ejecución de retiros
- Registro de transacciones
- Atención de conexiones entrantes del e-commerce

### app / ui
- **ATMInterface**  
  Interfaz principal del sistema bancario.  
  Permite interactuar con las funciones del banco y levantar la aplicación bancaria.

- **BankServerUI**  
  Interfaz encargada de **iniciar y mantener activo el servidor bancario**, mostrando su estado y quedando a la espera de conexiones entrantes.

### service
- Servicios bancarios responsables de:
  - Validación de credenciales
  - Verificación de saldo
  - Ejecución de retiros
  - Control de operaciones financieras

### network
- Componentes encargados de:
  - Escuchar conexiones TCP
  - Procesar solicitudes del e-commerce
  - Responder con aprobación o rechazo de transacciones

### Persistencia
- **users_secure.txt**  
  Almacena usuarios bancarios y sus saldos.
- **transactions.txt**  
  Registro histórico de operaciones financieras.

---

## Comunicación entre Módulos

- Protocolo: **TCP**
- Modelo: **Cliente / Servidor**
- Cliente: E-commerce
- Servidor: Bank
- Comunicación controlada mediante comandos y validaciones

---

## Scripts de Automatización (.sh)

El proyecto utiliza **múltiples scripts Bash**, cada uno con una responsabilidad específica.  
Por este motivo, el sistema está diseñado y probado para **entornos Linux**.

### Scripts Disponibles

- **compile.sh**  
  Compila el proyecto de forma básica, generando los `.class` en la carpeta `bin/`.  
  Útil para pruebas rápidas sin librerías externas.

- **runATM.sh**  
  Inicia la aplicación bancaria **ATMInterface**, permitiendo la interacción con el sistema bancario.

- **runBankServer.sh**  
  Inicia el **servidor bancario (BankServerUI)**, quedando a la espera de conexiones entrantes desde el e-commerce.

- **run.sh**  
  Script principal del **E-commerce**.  
  Compila el proyecto usando librerías externas ubicadas en `lib/` y ejecuta la aplicación `EcommerceApp`.

---

### Orden de Ejecución Recomendado

- bash
./runBankServer.sh
./runATM.sh
./run.sh

---

## Compilación y Ejecución

### Requisitos
- Java JDK 17 o superior
- Sistema operativo compatible con Bash

### Permisos de Ejecución
- bash
chmod +x *.sh

### Estado del Proyecto
Proyecto académico completamente funcional que integra un e-commerce y un sistema bancario realista, destacando la separación entre lógica comercial y financiera, el uso de comunicación en red y buenas prácticas de arquitectura de software.