/*
 * File:          es-ES.ts
 * Path:          v2\langs\es-ES.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-11 21:17:53 +02:00
 *
 * SHA-256: c0281697464532fdf89aca93281dedc0e0ed2725fd81d7cb6009f63ee8b66830
 *
 */


import emojis from "../utils/emojis";
export default {
  flag: "🇪🇸",
  name: "Español",
  description: "El idioma por defecto de KenaBot",
  events: {
    guildCreate: {
      embed_author: "Gracias por añadirme al servidor %s%!!",
      embed_desc:
        "Si tienes alguna duda sobre el funcionamiento del bot, puedes acceder a mi **[página web](https://kenabot.xyz/)** , en la que encontrarás toda la información que necesitas\n" +
        "\n" +
        "Si quieres configurarme puedes hacerlo desde mi **[dashboard web](https://dashboard.kenabot.xyz/)**, y si tienes alguna duda puedes visitar mi **[servidor de soporte](https://support.kenabot.xyz/)**!",
      buttons: {
        web: "Mi página web",
        support: "Mi servidor de soporte",
        dash: "Mi dashboard",
      },
    },
    dm: {
      embed_title: "Hola :wave: soy %s%",
      embed_description:
        "Soy el bot de música que tu servidor necesita!\nSi quieres un poco más de **información sobre mi**, puedes hacer click en los **botones!**",
    },
    trackStart: {
      embed_author: "|  Ahora sonando",
    },
    interactionCreate: {
      unknown_command:
        emojis.error + " No he podido encontrar el comando que has escrito",
      cant_use_button: emojis.error + " No puedes usar este botón",
    },
  },

  commands: {
    play: {
      no_vc:
        emojis.error +
        "No puedo reproducir música si no estoy en un canal de voz!",
      no_vc_perms:
        emojis.error +
        "No tengo permisos para reproducir música en este canal!",
      different_vc:
        emojis.error + "Ya estoy reproduciendo música en otro canal!",
      no_player: emojis.error + "No tengo ningún reproductor disponible!",
      no_song: emojis.error + "Debes de especificar una canción!",
      no_song_found:
        emojis.error + "No he encontrado ninguna canción con ese nombre!",
      youtube_link: emojis.error + "No puedo reproducir canciones de YouTube!",
      vc_disabled:
        emojis.error + "Un administrador ha deshabilitado ese canal de voz!",
      song: "canción",
      songs: "canciones",
      duration: "Duración",
      in_queue: "En cola",
      pause_resume: "Pausar/Resumir",
      stop: "Parar",
    },
    queue: {
      embed_footer: "Página %s% de %d% (%f% canciones totales)",
      embed_author: "Lista de reproducción",
      now_playing: "Ahora sonando",
      no_previous: "No hay canciones anteriores",
      no_next: "No hay más canciones",
    },
    pause: {
      already_paused: emojis.error + "La canción ya está pausada!",
      paused: emojis.pause + "La canción se ha **pausado**!",
      resumed: emojis.resume + "La canción se ha **reanudado**!",
      already_resumed: emojis.error + "La canción no está pausada!",
    },
    stop: "La lista de reproducción se ha **vaciado**!",
    stop_leave:
      "La lista de reproducción se ha **vaciado** y he salido del canal de voz!",
    skip: "La canción ha sido **saltada**!",
    skip_vote: {
      vote: "**Empieza la votación para saltar la canción!**\nHaz click en el botón para votar, para que la canción sea saltada debe de votar mas del 50% de los usuarios.\n `Porcentaje actual: %p%%`.",
      label: "Votar para saltar",
      announce:
        "**He enviado un mensaje para que todos los usuarios del canal de voz voten para saltar la canción!**\nRecuerda hacer click al botón para votar.",
      already_voted: emojis.error + "Ya has votado para saltar la canción!",
      cant_vote: emojis.error + "No puedes votar para saltar la canción!",
      voted: emojis.success + "Has votado para saltar la canción!",
      canceled:
        "**Voto para saltar cancelado**\nParece que no han votado suficientes usuarios para saltar la canción.",
      completed:
        "**Saltando canción!**\nEl `%s%%` de los usuarios han decidido saltar la canción.",
      redirect:
        "**Solo el DJ puede ejecutar este comando!**\nSi quieres iniciar una votación para saltar la canción puedes ejecutar el comando `/skip`.",
    },
    volume: {
      info: emojis.volume + "Volumen actual: **%s%%**",
      invalid_level:
        emojis.error +
        "Debes de especificar un nivel de volumen entre **0** y **100**!",
      set: emojis.volume + "El volumen se ha cambiado a **%s%%**!",
    },
    loop: {
      info: emojis.loop + "El bucle está **%s%**",
      already: emojis.error + "El bucle ya está **%s%**!",
      action: emojis.loop + "El bucle se ha **%s%**!",
      repeat: "activado",
      no_repeat: "desactivado",
    },
    now_playing: {
      remaining: "Quedan ",
    },
    premium: {
      no_code: emojis.error + "No has especificado un código de premium!",
      invalid_code:
        emojis.error + "El código que has especificado no es válido!",
      used_code:
        emojis.error + "El código que has especificado ya ha sido usado!",
      bot_claimed: emojis.error + "Ese código ya ha sido usando en este bot!",
      already_premium: emojis.error + "Ya estás en el modo donador!",
      enabled:
        emojis.success + "El modo donador ha sido activado durante **%t%**",
    },
    invite:
      emojis.success +
      "Puedes invitarme a tu servidor haciendo click en el botón!",
    support:
      emojis.success +
      "Puedes visitar mi servidor de soporte para obtener ayuda y más información haciendo click en el botón!",
    shuffle: emojis.shuffle + "La lista de reproducción se ha **mezclado**!",
    dashboard:
      emojis.success +
      "Puedes visitar mi dashboard web para configurarme haciendo click en el botón!",
    remove: {
      invalid:
        emojis.error + "No hay ninguna canción en el puesto número **%s%**!",
      success: emojis.success + "La canción `%s%` se ha **eliminado**!",
    },
    skip_to: emojis.resume + "Se han adelantado **%s%** canciones!",
    clear_queue: emojis.success + "La lista de reproducción se ha **vaciado**!",
    seek: {
      max_duration:
        emojis.error +
        "No puedo saltar a una posición superior a la duración de la canción!",
      invalid_time: emojis.error + "Debes de especificar una tiempo válido!",
      forward_success:
        emojis.resume + "Se ha **adelantado** la canción a `%s%`!",
      backward_success:
        emojis.resume + "Se ha **retrasado** la canción a `%s%`!",
    },
    back: {
      no_previous: emojis.error + "No hay ninguna canción anterior!",
      success: emojis.resume + "Se ha vuelto a la canción anterior!",
    },
    filters: {
      no_filters: emojis.error + "Debes de especificar al menos un filtro!",
      bass_boost:
        emojis.success + "El filtro de `Bass Boost` se ha **activado**!",
      ocho_d: emojis.success + "El filtro de `8D` se ha **activado**!",
      nightcore:
        emojis.success + "El filtro de `Nightcore` se ha **activado**!",
      vaporwave:
        emojis.success + "El filtro de `Vaporwave` se ha **activado**!",
      clear: emojis.success + "Todos los filtros se han **desactivado**!",
      treble: emojis.success + "El filtro de `Treble` se ha **activado**!",
      karaoke: emojis.success + "El filtro de `Karaoke` se ha **activado**!",
      tremolo: emojis.success + "El filtro de `Tremolo` se ha **activado**!",
      speed: emojis.success + "El filtro de `Speed` se ha **activado**!",
      unknown: emojis.error + "No se reconoce ese filtro!",
      pitch: emojis.success + "El filtro de `Pitch` se ha **activado**!",
    },
    _247: {
      no_channel:
        emojis.error +
        "Debes especificar un canal de **voz** para poder activar el modo **24/7**!",
      enabled:
        emojis.success +
        "El modo **24/7** se ha **activado** para el canal %s%!",
      disabled: emojis.success + "El modo **24/7** se ha **desactivado**!",
    },
    help: {
      desc:
        "**Hola :wave:**, soy **%s%**!\nSoy el bot de música que tu servidor necesita! Puedo reproducir canciones desde **Spotify**, **Soundcloud**, **Twitch** y [mucho más](https://kenabot.xyz)!\n\n" +
        "**Comandos**\n" +
        "Puedes ver información sobre todos mis comandos **[aquí](https://kenabot.xyz/commands)** o haciendo click en el botón de abajo!\n\n" +
        "**Premium/Donaciones**\n" +
        "La versión de **[KenaBot para donadores](https://kenabot.xyz/premium)** te dará acceso a nuevas funciones y mejoras en el bot, como **24/7**, **efectos de audio** y un **reproductor web**!!.\n[Haz click aquí para saber más](https://kenabot.xyz/premium)\n\n" +
        "**Añádeme a tu servidor**\n" +
        "Puedes añadirme a todos los servidores que quieras haciendo click **[aquí](https://discord.com/api/oauth2/authorize?client_id=%i%&permissions=3534272&scope=bot%20applications.commands)** o en el botón!\n\n" +
        "**Servidor de soporte**\n" +
        "En mi servidor de soporte encontrarás todo tipo de ayuda y más información sobre mi, puedes unirte haciendo click **[aquí](https://kenaboy.xyz/support)** o en el botón!",
      commands: {
        title: "Comandos",
      },
      add: {
        title: "Añádeme a tu servidor",
        add: "Añádeme!",
      },
      support: {
        title: "Servidor de soporte",
        support: "Soporte",
      },
    },
    commands:
      "Puedes ver información sobre todos mis comandos **[aquí](https://kenabot.xyz/commands)** o haciendo click en el botón de abajo!",
    lyrics: {
      loading:
        emojis.search +
        "Estoy buscando la canción...\n`Si no he mandado un mensaje con la letra, es posible que no haya encontrado la canción.`",
      error:
        emojis.error +
        " <@!%s%>, No se ha encontrado ninguna canción con ese nombre!",
    },
    config: {
      title: "Configuración del servidor",
      description:
        "**Hola** :wave: bienvenido al asistente de configuración del bot <@!%s%>\n\nSi tienes alguna duda a cerca del bot y de su configuración, puedes entrar a mi **[servidor de soporte](https://kenabot.xyz/soporte)** o consultar mi **[página web](https://kenabot.xyz)**",
      help: "Si necesitas ayuda, puedes consultar mi **[página web](https://kenabot.xyz)** o en el **[servidor de soporte](https://kenabot.xyz/soporte)**",
      buttons: {
        lang: "Idioma",
        channels: "Canales bloqueados",
        dj: "Rol de DJ",
        more: "Más",
        commands: "Comandos bloqueados",
        premium_on: "Modo donador activado",
        premium_off: "Modo donador desactivado",
        back: "Atrás",
        voice_channels: "Canales de voz bloqueados",
        beta_on: "Estás en la beta",
        beta_off: "No estás en la beta",
        embed: "Color del embed",
        database: "Información que guardamos",
        select_all: "Seleccionar todos",
        remove_all: "Quitar todos",
      },
      lang: {
        title: "Selección de Idioma",
        description:
          "Abre el menú para elegir el idioma en el que quieres que se muestren los mensajes del bot.\n\nSi no ves tu idioma puedes unirte a mi [servidor de soporte](https://kenabot.xyz/soporte) y contribuir como traductor!",
        select: "Selecciona el idioma",
      },
      channels: {
        title: "Canales bloqueados",
        description:
          "Abre el menú para elegir los canales en los que no responderé mensajes.",
        select: "Selecciona los canales",
      },
      embed: {
        title: "Configuración del color de los embeds",
        description:
          "Abre el menú para elegir el color del embed que se mostrará en todos los mensajes del bot.\n\nEste menú solo trae unos colores predeterminados, si quires usar un color personalizado puedes entrar a mi [dashboard web](https://kenabot.xyz/dashboard).",
        select: "Color del embed",
      },
      premium_on: {
        title: "Ya tienes el modo donador activado",
        description_forever:
          "**Muchas gracias por contribuir al desarrollo de KenaBot!!!!**\nY tenemos buenas noticias... Tu modo donador está activado **permanentemente**!",
        description:
          "**Muchas gracias por contribuir al desarrollo de KenaBot!!!!**\nPare que te quedan <t:%s%:R> de modo donador, disfruta de todas las cosas que trae este modo!",
      },
      premium_off: {
        title: "Parece que no tienes el modo donador activado",
        description:
          "Si quires donar a mi desarrollo, puedes hacerlo desde mi [página web](https://kenabot.xyz/premium)\nRecibirás el modo donador y podrás disfrutar de todas las cosas que trae este modo!",
      },
      database: {
        title:
          "Esta es toda la información que tenemos almacenada a cerca de tu servidor:",
        raw: "Crudo:",
        read: "Legible:",
        delete: "Borrar información del servidor",
        privacy: "Política de privacidad",
        confirm: "Borrar",
        cancel: "Cancelar",
      },
      commands: {
        title: "Comandos bloqueados",
        description:
          "Abre el menú para elegir los comandos que no responderé.\nSi un usuario sin permisos intenta ejecutar un comando que está bloqueado, le responderé con un mensaje de error.",
        select: "Selecciona los comandos",
      },
      voice: {
        title: "Canales de voz bloqueados",
        description:
          "Abre el menú para elegir los canales de voz en los que no podré unirme.",
        select: "Selecciona los canales",
      },
      djrole: {
        title: "Rol de DJ",
        description:
          "Abre el menú para elegir el rol del DJ, el cual podrá ejecutar todos los comandos de música sin limitaciones.",
        select: "Selecciona el rol",
      },
      cant_use:
        "**No puedes usar esta interacción!**\nPuedes ejecutar el comando por tu cuenta para poder usalo.",
    },
  },
  manager: {
    no_perms:
      emojis.error +
      "No tienes los permisos suficientes para ejecutar esa acción!",
    song_added: "Canción **[%s%](%l%)** añadida a la cola! `%t%`",
    playlist_added: "**%s%** canciones añadidas a la cola!",
    nothing_playing: emojis.error + "No se está reproduciendo nada!",
    cant_modify_queue:
      emojis.error + "No puedes modificar la cola de reproducción!",
    queue_end: emojis.success + "La cola de reproducción se ha **acabado**!",
    restart: {
      embed_author: "¡KenaBot se va a reiniciar!",
      embed_description:
	"Lamentamos esta interrupción, pero **KenaBot** se va a reiniciar para implementar nuevas funciones\n**¡Estaremos de vuelta en nada!**",
    },
    cant_do: emojis.error + "No puedo hacer eso aquí!",
    no_premium:
      emojis.error +
      "Solo los **donadores** pueden usar ese comando!\n[Haz click aquí para saber más](https://kenabot.xyz/premium)",
    no_premium_twitch:
      emojis.error +
      "Solo los **donadores** pueden reproducir directos de **Twitch**!\n[Haz click aquí para saber más](https://kenabot.xyz/premium)",
    no_premium_stage:
      emojis.error +
      "Solo los **donadores** pueden reproducir música en **Stage Channels**!\n[Haz click aquí para saber más](https://kenabot.xyz/premium)",
    blocked:
      "**Este canal está bloqueado!**\nUn administrador de este servidor ha bloqueado este canal para evitar el uso de comandos aquí. ",
    blocked_command:
      "**Este comando está bloqueado!**\nUn administrador de este servidor ha bloqueado este comando para evitar su uso globalmente.",
    no_db:
      "**Sin información!**\nParece que no tenemos nada guardado en nuestras bases de datos sobre tu servidor.",
    delete_confirm:
      "**¿Estas seguro de que quieres borrar esta información?**\nToda la configuración del servidor se perderá.",
    delete_cancel:
      "**Cancelado!**\nSe ha cancelado la acción de borrar la información.",
    deleted: "**Borrado!**\nSe ha borrado la información del servidor.",
    dj_role_required:
      "**Solo el DJ puede ejecutar este comando!**\nParece que en un canal de voz hay un DJ, solo el puede ejecutar este comando.",
    dj_role_required_usecmd:
      "**Solo el DJ puede ejecutar este comando!**\nParece que en un canal de voz hay un DJ, solo el puede ejecutar este comando.\n\nPuedes empezar una votación para saltar la canción usando el comando `/skip`.",
    is247: emojis.error + "No puedes usar este comando en el modo **24/7**!",
    premium_forever:
      emojis.error +
      "Parece que este servidor está en nuestro programa **Partner**, por lo que no puedes borrar su configuración.",
    blocked_server:
      emojis.error +
      "Este servidor está bloqueado por el desarrollador!\nRazón: `%s%`.",
  },
};
