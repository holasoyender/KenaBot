/*
 * File:          api.ts
 * Path:          v2\socket\api.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:02:10 +02:00
 *
 * SHA-256: 8e943f30a242026ca280d98798553895ec6c5d896959a788cc1f7960b052417e
 *
 */


import Zeppelin from "../utils/Client";
import { WebSocket } from "ws";
import EventEmitter from "events";
import { Logger } from "../utils/Logger";
import Guild from "../database/schema/Guild";
import {genPermissions} from "../utils/permissions";

let client: Zeppelin | null = null
class websocket extends EventEmitter {

    private client: Zeppelin | null;

    constructor(options: { client: Zeppelin | null }) {
        super();
        this.client = options.client;

        this.emit("ready");
    }
}
/*

server.use("/players", player);

*/

let wss: websocket | any = null;
let enumError = 0
export const init = (_client: Zeppelin) => {

    client = _client;
    let ws
    wss = new websocket({ client: _client });
    const headers = {
        Authorization: "1234",
        "Client-Name": _client.user?.username.replace(/ /g, ""),
    }

    try {
        ws = new WebSocket('ws://localhost:4000', { headers });
    } catch (e) {
        return;
    }

    ws.on("error", () => {
        if (enumError >= 1) return
        enumError += 1

        Logger.ws("Se ha desconectado del WebSocket, intentando reconectar...")
    })

    ws.addEventListener('open', () => {
        enumError = 0
        Logger.ws('Conectado con el WebSocket de la API central')
    });

    ws.on("close", (reason, message) => {
        if (reason === 1003) {
            console.log("Error al conectar con el WS: " + message.toString())
            return;
        }
        //Loop cada 5 segundos
        let ok = false
        setInterval(() => {
            try {
                if (!ok) {
                    init(_client)
                    ok = true;
                }
                return;
            } catch (e) {
                ok = false
                return;
            }
        }, 1000);
    })

    ws.addEventListener('message', async (event: any) => {
        const options = JSON.parse(event.data);
        if (options.type == "action") {
            wss?.emit(options.action, event.target, options.id, options.data, options.method);
        }
    });

    wss?.on("/commands", (ws: WebSocket, id: number, data: any, method: string) => {
        if (method !== "GET") return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "Method not allowed"
            }, id
        }));

        const Commands: Array<any> = [];
        client?.Commands.forEach(cmd => {
            Commands.push({
                name: cmd.name,
                desc: cmd.desc,
                category: cmd.category,
                permission: cmd.permissions,
                premium: cmd.premium ?? false,
                usage: cmd.usage
            })
        })
        ws.send(JSON.stringify({ data: Commands, id }));
    });
    wss.on("/stats", (ws: WebSocket, id: number, data: Array<string>, method: string) => {
        if (method !== "GET") return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "Method not allowed"
            }, id
        }));

        const userArray: Array<any> = []
        client?.guilds.cache.map(g => userArray.push(g.memberCount))
        let suma = 0;
        userArray.forEach(function (numero) {
            suma += numero;
        });

        ws.send(JSON.stringify({
            data: {
                guilds: client?.guilds.cache.size ?? 0,
                users: suma ?? 0,
                channels: client?.channels.cache.size ?? 0,
                shards: client?.options.shardCount ?? 0,
                // @ts-ignore
                uptime: Math.round(client?.uptime),
            },
            id
        }));
    })

    wss.on("/langs", (ws: WebSocket, id: number, data: any, method: string) => {
        if (method !== "GET") return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "Method not allowed"
            }, id
        }));

        const lang = Object.values(client?.langs ?? {}).map(lang => {
            return {
                flag: lang.flag,
                name: lang.name,
                description: lang.description
            }
        })

        ws.send(JSON.stringify({ data: lang, id }))
    })

    wss.on("/servers", async (ws: WebSocket, id: number, data: any, method: string) => {
        if (method !== "GET") return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "Method not allowed"
            }, id
        }));

        if(!data || !data.user) return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "User not found"
            }, id
        }));

        const guilds = client?.guilds.cache;
        const mutual_guilds = data.user.guilds?.filter((i: any) => guilds?.map((s: any) => s.id).includes(i.id));

        const allowed_guilds = mutual_guilds.filter((i:any) => {
            const permission = genPermissions(i.permissions)
            return permission.includes("MANAGE_GUILD")
        }).map((i:any) => i.id)

        if (data.params["0"]) {
            const guild = client?.guilds.cache.get(data.params["0"]);
            const db:any = await Guild.findOne({ guildID: data.params["0"] })

            if(!allowed_guilds.includes(data.params["0"]))
                return ws.send(JSON.stringify({
                    data: {
                        status: 401,
                        message: "You don't have permission to see this server"
                    }, id
                }));

            if (guild) {
                const config = {
                    premium: {
                        enabled: db?.premium.enabled ?? false,
                        expires: db?.premium.expires ?? 0,
                    },
                    _247: {
                        enabled: db?._247.enabled ?? false,
                        channel:db?._247.channelID ?? ""
                    },
                    disabled: {
                        channels: db?.disabled.channels ?? [],
                        commands: db?.disabled.commands ?? [],
                        voice: db?.disabled.voice ?? [],
                    },
                    guildID: db?.guildID ?? data.params["0"],
                    lang: db?.lang ?? "es-ES",
                    color: db?.color ?? "#2f3136",
                    beta: db?.beta ?? false,
                    dj_role:db?.dj_role ?? ""
                }

                const langs = Object.values(client?.langs ?? {}).map(lang => {
                    return {
                        flag: lang.flag,
                        name: lang.name,
                        description: lang.description,
                        value: Object.keys(client?.langs ?? {}).filter(key => client?.langs[key].name === lang.name)[0]
                    }
                })

                const guild_data = {
                    roles: guild.roles.cache.map(role => {
                        return {
                            id: role.id,
                            name: role.name,
                            color: role.color,
                        }
                    }),
                    channels: guild.channels.cache.map(channel => {
                        if(channel.type == "GUILD_CATEGORY") return;
                        return {
                            id: channel.id,
                            name: channel.name,
                            type: channel.type
                        }
                    }).filter(i => i),
                    langs
                }

                ws.send(JSON.stringify({
                    data: {
                        name: guild.name,
                        id: guild.id,
                        icon: guild.iconURL(),
                        config,
                        guild_data
                    }, id
                }));
            } else {
                ws.send(JSON.stringify({
                    data: {
                        premium: db ? db.premium : false,
                    }, id
                }));
            }
        } else {
            return ws.send(JSON.stringify({
                data: {
                    status: 401,
                    message: "Server is unavailable"
                }, id
            }));
        }
    })

    wss.on("/mutualguilds", async (ws: WebSocket, id: number, data: any, method: string) => {
        if (method !== "GET") return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "Method not allowed"
            }, id
        }));
        if(!data || !data.user) return ws.send(JSON.stringify({
            data: {
                status: 400,
                message: "User not found"
            }, id
        }));

        const botGuilds = client?.guilds.cache;
        const userGuilds = data.user.guilds;
        const mutualGuilds: Array<any> = [];
        if(botGuilds && userGuilds) {
            mutualGuilds.push(userGuilds.filter((g:any) => botGuilds.has(g.id)));
        }

        ws.send(JSON.stringify({
            data: {
                status: 200,
                data: mutualGuilds
            }, id
        }));
    })
};