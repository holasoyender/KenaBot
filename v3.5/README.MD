# KenaBot v3.5

Prototipo iniciado en noviembre de 2023 como el primer intento de modularización de KenaBot. La idea original era convertirlo en v4: centralizar en una sola aplicación la lógica que hasta entonces se repetía entre cuatro bots independientes, conectados a clientes de Discord y bases de datos propios.

## Arquitectura prevista

El trabajo reorganizó el proyecto JVM en módulos —entre ellos `core`, `api`, `proxy`, `shared` y `atenea`— para separar responsabilidades y compartir componentes. La API seguía en TypeScript. La meta era operar los cuatro clientes desde una lógica común y una base de datos centralizada, haciendo más sencillo mantener el producto.

## Por qué se abandonó

A mitad del desarrollo se decidió que, si se iba a reescribir el bot de nuevo, convenía salir de la JVM por los problemas de rendimiento percibidos y probar Rust. v3.5 quedó en una fase inicial, sin llegar a ser una versión funcional de producción; su trabajo dio paso a la reescritura completa v4.

Este directorio documenta una dirección de diseño descartada y no una versión instalable del bot. La cronología y el contexto general están en el README raíz.
