# ¿Qué es Chancita?

Chancita es una aplicación móvil nativa para **Android** que permite a los usuarios crear **rifas digitales** o participar en ellas, comprando números disponibles y visualizando los resultados de los sorteos.

La app gestiona la autenticación de usuarios y persistencia de datos con **Firebase**, mientras que los pagos se procesan de forma segura mediante **Mercado Pago**.

***❗Nota: El proyecto está en frases tempranas de desarrollo, por lo que muchas de las funcionalidades mencionadas puede que aún no estén implementadas.***<br/>
***❗Nota 2: Actualmente la aplicación posee mejor soporte para modo claro. En modo oscuro pueden llegar a presentarse algunas inconsistencias.***

# Arquitectura del Proyecto

La arquitectura se basa en un frontend móvil en **Android**, con backend serverless en **Firebase** y **Mercado Pago** como pasarela de pagos:

<img width="1280" height="720" alt="Definición de arquitectura" src="https://github.com/user-attachments/assets/3a245d78-73e1-4d93-a13f-92494e288b49" />

- **Android App**: Interfaz de usuario diseñada sobre el **patrón arquitectónico MVVM** donde los organizadores crean rifas y los participantes compran números además de ver resultados.  
- **Firebase Authentication**: Gestiona la creación de cuenta y login seguros.
- **Firestore Database**: Almacena información de rifas, participantes y números vendidos.
- **Cloud Functions**: Lógica para notificaciones, asignación de números ganadores (en modo aleatorio) y validación de compras y conexión con Mercado Pago.
- **Mercado Pago**: Pasarela de pago para adquirir los números de la rifa. Internamente usa el método de **Split Payments** que ofrece Mercado Pago sobre la API **Checkout Pro**.

**❗Tengase en cuenta que Mercado Pago cobra una comisión del 2.2% cuyo costo se cobra al participante de la rifa.**

## Requisitos para ejecución en entorno de desarrollo
- Android Studio con un dispositivo/emulador API 26 o mayor.
- Java 1.8.
- Cuenta de Mercado Pago.
