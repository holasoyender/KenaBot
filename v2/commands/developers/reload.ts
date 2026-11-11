/*
 * File:          reload.ts
 * Path:          v2\commands\developers\reload.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-14 15:29:21 +01:00
 *
 * SHA-256: 279ace6b871369e1b22ea1483cfebb0a2e453d1d33b3435f47d209c9c3eba946
 *
 */


import Zeppelin from "../../utils/Client";
import {Message} from "discord.js";

module.exports = {
    name: "reload",
    desc: "Recargar un evento/comando/modulo del bot",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const content = message.content.split(" ").slice(1);
        let thing;
        if (!content[0]) return message.channel.send("Debes de especificar un tipo: $noArg")

        switch (content[0]) {

            case "cmd":
            case "command":
            case "comando":
            case "comandos":

                if (!content[1]) return message.channel.send("Debes de especificar un comando")
                thing = client.Commands.find(c => c.name === content[1])
                if (!thing) return message.channel.send("No se ha encontrado el comando")
                delete require.cache[require.resolve(`../${thing.name}`)];

                try {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const newCommand = require(`../${thing.name}`);
                    client.Commands.set(newCommand.name, newCommand);
                    message.channel.send(`Comando \`${thing.name}\` recargado`);
                } catch (error:any) {
                    console.error(error);
                    message.channel.send(`Ha ocurrido un error al recargar el comando \`${thing.name}\`:\n\`${error.message}\``);
                }

                break;

            case "evento":
            case "eventos":
            case "event":
            case "events":
            case "evnt":
            case "ev":

                if (!content[1]) return message.channel.send("Debes de especificar un evento")
                thing = client.Events.find(e => e.name === content[1])
                if (!thing) return message.channel.send("No se ha encontrado el evento")
                delete require.cache[require.resolve(`../../events/${content[1]}`)];

                try {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const eventFunction = require(`../../events/${content[1]}`);
                    client.Events.set(content[1], {name: content[1], run: eventFunction.run});

                    message.channel.send(`Evento \`${thing.name}\` recargado`);
                } catch (error:any) {
                    console.error(error);
                    message.channel.send(`Ha ocurrido un error al recargar el evento \`${thing.name}\`:\n\`${error.message}\``);
                }

                break;


            case "lang":
            case "idioma":
            case "lenguaje":
            case "lenguajes":
            case "langs":

                if (!content[1]) return message.channel.send("Debes de especificar un lenguaje")
                thing = client.langs[content[1]]
                if (!thing) return message.channel.send("No se ha encontrado el lenguaje")
                delete require.cache[require.resolve(`../../langs/${content[1]}`)];

                try {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const newLang = require(`../../langs/${content[1]}`);
                    client.langs[content[1]] = newLang.default;
                    message.channel.send(`Lenguaje \`${thing.name}\` recargado`);
                } catch (error:any) {
                    console.error(error);
                    message.channel.send(`Ha ocurrido un error al recargar el lenguaje \`${thing.name}\`:\n\`${error.message}\``);
                }

                break;

        }

    },
}
