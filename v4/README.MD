# KenaBot v4

Reescritura de KenaBot en Rust, desarrollada entre agosto de 2023 y julio de 2024. Buscaba conservar el comportamiento familiar de la versión en producción con una implementación más rápida y eficiente. Nunca se terminó ni llegó a producción; el desarrollo se cerró tras conflictos internos y problemas de comunicación y personales, y v3 continuó activa.

## Cómo funciona internamente

El núcleo está escrito en Rust y usa Tokio para concurrencia. La integración con Discord se basa en `skua`, un fork personalizado de Serenity. El código organiza comandos, eventos, internacionalización, métricas y persistencia; mantiene MongoDB y Redis para los datos y caché.

La reproducción de música se comunica con una nueva generación de Atenea. Como en v3, Atenea era un módulo separado y no forma parte de este repositorio. En esta etapa se descartó la centralización propuesta por v3.5: la arquitectura pretendía seguir el modelo de cuatro bots independientes, aunque se realizaron cambios importantes en la implementación.

El árbol también conserva módulos de API, proxy y servicios auxiliares. Encontrarlos en el código no implica que la reescritura estuviera completa o que el conjunto pudiera desplegarse como sistema de producción.

## Estado del proyecto

Código de desarrollo incompleto preservado como registro histórico. No es una versión lista para ejecutar ni una continuación mantenida. Las dependencias externas y la configuración original reflejan infraestructura de aquella época. Consulta el README raíz para la historia completa.
