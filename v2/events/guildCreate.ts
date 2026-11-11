/*
 * File:          guildCreate.ts
 * Path:          v2\events\guildCreate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-04-28 16:21:48 +02:00
 *
 * SHA-256: af1e766d260f69161416dc3f7eac7f22f703a459b595d4fe67c203c62438802a
 *
 */


import Zeppelin from "../utils/Client";
import { Guild, MessageActionRow, MessageButton, MessageEmbed } from "discord.js";
import esES from "../langs/es-ES";
import Default from "../utils/config/default";

exports.run = async (client: Zeppelin, guild: Guild) => {

    const userArray: Array<any> = []
    client.guilds.cache.map(g => userArray.push(g.memberCount))
    let suma = 0;
    userArray.forEach(function (numero) {
        suma += numero;
    });

    const logEmbed = new MessageEmbed()
        .setColor(0x47EB72)
        .setAuthor({ name: `Me han añadido a un servidor`, iconURL: `${guild.iconURL() ?? "https://cdn.discordapp.com/attachments/755000173922615336/953028314593919006/emoji.png"}` })
        .addField(`Nombre del servidor`, `${guild.name} (\`${guild.id}\`)`, true)
        .addField(`Miembros`, `${guild.memberCount}`, true)
        .addField("Propietario", `\`${guild.ownerId}\``, true)
        .addField("Servidores totales", `${client.guilds.cache.size} (\`${suma}\` usuarios totales)`, true)
        .addField("Bot", `${client.user?.tag} (\`${client.user?.id}\`)`, true)
    await client.webhook.send({ embeds: [logEmbed] })

    const embed = new MessageEmbed()
        // @ts-ignore
        .setColor(Default.color)
        .setAuthor({
            name: esES.events.guildCreate.embed_author.replace("%s%", guild.name),
            iconURL: client.user?.avatarURL() || ""
        })
        .setThumbnail(guild.iconURL() || "https://cdn.discordapp.com/attachments/755000173922615336/929506763856834600/emoji.png")
        .setDescription(esES.events.guildCreate.embed_desc);

    const web = new MessageButton().setLabel(esES.events.guildCreate.buttons.web).setEmoji("🖥️").setStyle("LINK").setURL("https://kenabot.xyz/");
    const support = new MessageButton().setLabel(esES.events.guildCreate.buttons.support).setEmoji("⚙️").setStyle("LINK").setURL("https://kenabot.xyz/support");
    const config = new MessageButton().setLabel(esES.events.guildCreate.buttons.dash).setEmoji("🔧").setStyle("LINK").setURL("https://kenabot.xyz/dashboard");
    const row = new MessageActionRow().addComponents([web, config, support]);

    if (guild.me?.permissions.has("VIEW_AUDIT_LOG"))
        guild.fetchAuditLogs({ type: "BOT_ADD", limit: 1 }).then(log => {
            if (!log) return;
            log.entries.first()?.executor?.send({ embeds: [embed], components: [row] }).catch();
        });
}
