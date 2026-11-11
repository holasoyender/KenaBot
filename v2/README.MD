# KenaBot v2

Primera versión pública de KenaBot, activa entre diciembre de 2021 y agosto de 2022. El proyecto nació como bot musical para la comunidad de Ibai y, tras la buena acogida, se abrió para que otros servidores pudieran usarlo. Esta carpeta conserva el estado del código al 29 de agosto de 2022, cuando terminó la reescritura que dio paso a v3.

## Cómo funciona

v2 está escrita en TypeScript sobre Node.js y discord.js v13. El bot recibe eventos y comandos de Discord, administra colas y sesiones de reproducción, y delega el audio a nodos Lavalink. La persistencia usa MongoDB. El repositorio incluye el bot y una API central para datos y servicios web; la página web no forma parte de este código.

El sistema podía ejecutar varios procesos/shards y gestionar configuraciones por servidor. También conectaba servicios externos como Spotify, Genius y Sentry. Para su operación histórica se usaron PM2 y nodos de audio desplegados por separado.

## Contexto de la transición

El crecimiento de usuarios hizo visibles problemas de rendimiento y estabilidad. Discord.js v13 empezaba a quedar atrás y el manejo de memoria de la aplicación se volvió difícil de sostener. El equipo optó por rehacer el bot desde cero en Java y Kotlin con JDA, manteniendo MongoDB y Lavalink; esa evolución es v3.

## Estado

Archivo histórico, no preparado como instalación autónoma actual. Las instrucciones de despliegue originales dependen de servicios, credenciales y nodos que ya no deben considerarse disponibles. Consulta el README raíz para la cronología completa.
