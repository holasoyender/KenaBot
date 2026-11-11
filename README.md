# KenaBot
En este repositorio se encuentra el archivo de los códigos fuente antiguo del bot de Discord **KenaBot**, desde su inicio hasta *julio del 2024*, 
junto a un poco de historia de la evolución que tuvo el bot en esos años.

### Un poco de contexto
**KenaBot** fue un proyecto muy ambicioso de crear un bot de música para el servidor de Discord de la comunidad de **Ibai**, 
empezó como un proyecto de otro desarrollador del equipo original del bot, y tras el recibimiento tan positivo de la comunidad,
se decidió hacer un bot de cara al público, con el objetivo de que cualquier persona pudiera usarlo en su servidor de Discord y
convertirse en el estándar de bots de música en español. Mi trabajo en el proyecto comenzó en ese momento, por eso las versiones
que se listan en este repositorio comienzan desde la versión `v2`, ya que la versión `v1` es propiedad intelectual del desarrollador original.

## `v2` - Primer lanzamiento público (Diciembre 2021 - Agosto 2022)
Esta primera versión pública del bot, al igual que la versión original (`v1`), estaban desarrolladas en **Typescript** y utilizaban la librería **discord.js v13**, además de **Lavalink** para la reproducción de música.
El bot tuvo un recibimiento muy positivo por parte de la comunidad, y fue ganando usuarios rápidamente. Sin embargo, con el tiempo, la versión `v2` comenzó a tener problemas de rendimiento y estabilidad, el
manejo de la memoria en Typescript no era el mejor, y la librería **discord.js v13** estaba comenzando a quedar obsoleta, por lo que se decidió hacer un cambio radical en la arquitectura del bot, con la intención de
hacer un código robusto y estable desde cero, que pudiera soportar la gran cantidad de usuarios que el bot estaba teniendo, y que además fuera más fácil de mantener y actualizar.
En aquel entonces, la manera más común de desarrollar un bot de Discord grande era usando **Java** y **JDA**, por lo que se decidió rehacer el bot en **Java** y **Kotlin**, utilizando **JDA** y **Lavalink** para la reproducción de música.

El código fuente de esta versión se encuentra en la carpeta `v2`, exactamente igual que como se dejó el **29 de Agosto del 2022**, cuando se terminó la reescritura al nuevo lenguaje.

### Arquitectura de la versión `v2`
Esta primera versión heredó el sistema de base de datos de la versión original, que estaba basado en **MongoDB**, y se reestructuró el sistema de comandos y eventos, y se hicieron algunas mejoras en la estructura del código y en la organización de los archivos.
Se añadieron también configuraciones independientes por servidor, y funcionalidades de cara al público. Para el manejo de errores y logs se integró **Sentry**.

El proyecto se dividió en 3 módulos principales: el bot, la API y la página web, en este repositorio solo se encuentra el código del bot y la API, ambos escritos en **Typescript**.

## `v3` - Reescritura completa (Agosto 2022 - Julio 2024)
Esta versión fue una reescritura completa del bot, utilizando **Java** y **Kotlin**, con **JDA**. En esta versión también se introduce un nuevo módulo: **Atenea** fue
un fork del proyecto **Lavalink**, que se utilizaba para la reproducción de música, pero con algunas mejoras y funcionalidades adicionales independientes del bot, esto nos facilitó mucho la integración de nuevas funcionalidades 
y la mejora del rendimiento del bot, ya que Atenea estaba optimizado para nuestro caso de uso, aunque esto significó un aumento de la carga de trabajo al tener que mantener un módulo más. La base de datos se mantuvo sin ninguna reestructuración,
y se siguió utilizando **MongoDB**. Se añadieron nuevas funcionalidades, y se mejoró la estabilidad y el rendimiento del bot, además de añadir nuevas configuraciones por servidor y nuevas funcionalidades de cara al público.

La API se mantuvo en **Typescript**, pero se reestructuró completamente, y se añadieron nuevas funcionalidades acordes con la nueva versión del bot.

Esta versión fue la más estable y robusta de todas, y fue la que más tiempo estuvo en funcionamiento, fue la versión que estaba en producción cuando abandoné el proyecto, y la que más usuarios tuvo, llegando a tener más de 100.000 servidores activos.

**Atenea** no se encuentra en este repositorio, ya que fue un proyecto independiente en colaboración con otro desarrollador y la **Universitat Politècnica de Catalunya**, ya que se llegó
a desarrollar un driver de audio propio similar a **Opus** como proyecto de investigación, y se decidió mantenerlo privado por motivos de seguridad y privacidad, ya que es usado actualmente por
otros proyectos de investigación y desarrollo de la universidad, y no se puede compartir públicamente.

## `v3.5` - Primer intento de modularización (Noviembre 2023)
Originalmente, esta versión iba a ser una nueva versión `v4`, que tenía la intención de modularizar y centralizar la lógica del bot. Históricamente, **KenaBot** han sido 4 bots independientes, cada uno
con su cliente y base de datos propia, pero no dejaba de ser el mismo código multiplicado por 4, por lo que se decidió hacer un cambio radical en la arquitectura del bot,
y centralizar toda la lógica en una sola aplicación, que se conectara a los 4 clientes de Discord, y que tuviera una base de datos centralizada, para poder manejar toda la información de los 4 bots desde un solo lugar. Sin embargo,
a medio camino del desarrollo de esta versión se decidió que, si se iba a hacer otra reescritura completa, lo mejor sería abandonar la ida de usar la **JVM** por los problemas
de rendimiento que se estaban teniendo, y hacer una reescritura completa en **Rust**, ya que es un lenguaje mucho más rápido y eficiente, y además es mucho más seguro que **Java** y **Kotlin**. Por lo que esta versión quedó
en un estado de inicio, sin nada funcional, y se decidió abandonar la idea y pasar a la reescritura completa en **Rust**.

## `v4` - Reescritura completa en Rust (Agosto 2023 - Julio 2024)
Finalmente, se decidió hacer una reescritura completa del bot en **Rust**, utilizando un fork personalizado de la librería **Serenity** llamado **skua** para la integración con Discord,
y una nueva versión de **Atenea** (`v4`) para la reproducción de música. Esta versión iba a ser un gran cambio para el bot, pero las ideas de modularización y centralización de la lógica del bot se abandonaron, y se decidió
mantener la misma arquitectura que en la versión `v3`, pero con un código más rápido, eficiente y seguro. La base de datos se mantuvo sin cambios, y se siguió utilizando **MongoDB**.
La intención era que el usuario no notase el cambio de versión, y que el bot siguiera funcionando igual que antes, pero con un código más rápido y eficiente. 
Sin embargo, esta versión nunca llegó a estar terminada ni en producción, por conflictos internos del equipo de desarrollo y problemas tanto de comunicación como personales, y finalmente
acabé abandonando el proyecto, por lo que esta versión nunca llegó a ver la luz, y se decidió mantener la versión `v3` en producción hasta que el nuevo equipo de desarrollo 
decidiera hacer una nueva reescritura completa del bot.