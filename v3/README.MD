# KenaBot v3

Reescritura completa desarrollada entre agosto de 2022 y julio de 2024. Sustituyó la implementación TypeScript de v2 por Java y Kotlin con JDA. Fue la versión más estable y duradera, llegó a superar los 100.000 servidores activos y seguía en producción cuando dejé el proyecto.

## Cómo funciona

El bot se conecta a Discord mediante JDA, procesa comandos y eventos en la aplicación JVM y mantiene los datos en MongoDB. La reproducción se distribuye entre nodos de audio compatibles con Atenea. Atenea nació como un fork de Lavalink con cambios orientados a las necesidades del bot; su código no está incluido en este repositorio porque era un proyecto independiente de colaboración con otro desarrollador y la Universitat Politècnica de Catalunya.

La API, conservada en TypeScript, se reorganizó para acompañar la nueva aplicación y exponer servicios de configuración, autenticación, estadísticas y funciones web. La arquitectura mantuvo bots y bases de datos independientes, con configuraciones por servidor y nuevas funciones para usuarios.

## Evolución y límites del archivo

La reescritura buscaba responder al crecimiento y a los problemas de rendimiento y estabilidad de v2. En esta etapa se consolidaron la separación entre bot, API y nodos de audio y una operación que dio servicio a una comunidad amplia.

Este directorio es una instantánea histórica, no una guía de despliegue vigente. Atenea no se incluye; las dependencias de servicios externos y la configuración de producción requieren sustitución antes de ejecutar cualquier componente. Consulta el README raíz para la historia global.
