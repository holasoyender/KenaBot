/*
 * File:          eval.ts
 * Path:          v2\commands\developers\eval.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-21 18:48:38 +01:00
 *
 * SHA-256: 60ef5b8b07c2ac5e6f0d1a8c88d3840ed0d9b18631f4fb7aee5628068cfa754b
 *
 */


import Zeppelin from "../../utils/Client";
import {Message} from "discord.js";

module.exports = {
    name: "eval",
    desc: "Evaluates code.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const content = message.content.split(" ").slice(1).join(" ");
        if (!content) return message.channel.send("ErrNo: $noArg")

        const result = new Promise((resolve) => resolve(eval(content)));

        return result.then((output: any) => {
            if (typeof output !== "string")
                // eslint-disable-next-line @typescript-eslint/no-var-requires
                output = require("util").inspect(output, {depth: 0});

            if (output.includes(client.token))
                output = output.replace(client.token, "TOKEN");

            message.reply({
                content: `\`\`\`js\n${output}\`\`\``,
                allowedMentions: { repliedUser: false },
            });
        }).catch((err) => {
            err = err.toString();
            if (err.includes(client.token))
                err = err.replace(client.token, "TOKEN");

            message.reply({
                content: `\`\`\`js\n${err}\`\`\``,
                allowedMentions: { repliedUser: false },
            });
        })
    },
}
