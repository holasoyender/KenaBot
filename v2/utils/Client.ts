/*
 * File:          Client.ts
 * Path:          v2\utils\Client.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:02:10 +02:00
 *
 * SHA-256: cff9692db6334fe4a47254a62a6412e35ca049b0b9e14451bb1a0b70d915cc79
 *
 */


import { Client, ClientOptions, Collection, WebhookClient } from "discord.js";
import { Logger } from "./Logger";
import * as fs from "fs";
import Spotify from "../plugins/spotify/manager";
import Deezer from "../plugins/deezer";
import Apple from "../plugins/apple"
import Nodes from "./Nodes";
import mongo from "../database/mongo";
import os from "os";
import { init } from "../socket/api";
import { exec } from "child_process";
import Cluster from "discord-hybrid-sharding"
import esES from "../langs/es-ES";
import { Manager as newManager } from "../functions/manager";

export default class Zeppelin extends Client {

    public Logger: typeof Logger;
    public Commands: Collection<string, any>;
    public Events: Collection<string, any>;
    public Queue: Map<unknown, unknown>;
    public manager: newManager | null;
    public env: "dev" | "prod";
    public develop: { owners: string[], Commands: Collection<string, any>, loadApi: boolean };
    public webhook: WebhookClient;
    public error_webhook: WebhookClient;
    public crypto: { key: string };
    public langs: { [key: string]: typeof esES };
    public cluster: Cluster.Client | null;

    //public cluster: Cluster.Client;

    constructor(props: ClientOptions) {
        super(props);

        this.Logger = Logger;
        this.Commands = new Collection()
        this.Events = new Collection()
        this.Queue = new Map();
        this.manager = null;
        this.env = os.platform() === "win32" ? "dev" : "prod";
        this.langs = {}
        this.develop = {
            owners: [
                "396683727868264449",
                "581527728034152449",
                "397453373479190538"
            ],
            Commands: new Collection(),
            loadApi: true
        };
        this.webhook = new WebhookClient({ url: `https://discord.com/api/webhooks/938830325113557053/YgItdvv6G1NDrKjUf_snwhwCJtqeJaeFw1M9QpvipMICTcq0IiZTw5F95XGBb_yyzT2S` });
        this.error_webhook = new WebhookClient({ url: `https://canary.discord.com/api/webhooks/930523355776426004/Tq45LCD3N0YR50SyhdkED8mgPNm8BIf_PaLLcUaCVb5ZisjJgBNrnZ81udgNmYM371h3` });
        this.crypto = {
            key: "holasoyender",
        };
        this.cluster = null;
    }

    public async start() {

        if (!process.env.SPOTIFY_SECRET || !process.env.SPOTIFY_ID) return console.log("No se ha encontrado la información de Spotify requerida.")

        if (Cluster.Client.getInfo())
            this.cluster = new Cluster.Client(this)

        this.manager = new newManager({
            nodes: Nodes,
            send: (id, payload) => {
                const guild = this.guilds.cache.get(id);
                if (guild) guild.shard.send(payload);
            },
            autoPlay: true,
            plugins: [new Spotify(),
            new Deezer({}),
            new Apple()
            ],
        });
        this.manager.options.clientName = this.user?.username ?? "KenaBot" + Math.floor(Math.random() * 100);

        let str = "./";
        if (this.env == "prod") {
            str = "./dist/";
        }

        const events = await fs.promises.readdir(str + "events");
        events.forEach(file => {
            if (file.endsWith(".js") || file.endsWith(".ts")) {
                // eslint-disable-next-line @typescript-eslint/no-var-requires
                const eventFunction = require(`../events/${file}`);
                const eventName = file.split(".")[0]
                this.Events.set(eventName, { name: eventName, run: eventFunction.run });
            }
        });

        this.Events.forEach(event => this.on(event.name, (...args) => event.run(this, ...args)))

        await fs.readdir(str + "events/lava", (err, files) => {
            if (err) return this.Logger.error("Error al encontrar la carpeta de eventos de Lava: " + err);
            files.forEach(file => {
                if (file.endsWith(".js") || file.endsWith(".ts")) {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const event = require(`../events/lava/${file}`);
                    const eventName = file.split(".")[0]
                    // @ts-ignore
                    this.manager?.on(eventName, (...args: unknown) => event.run(this, ...args));
                }
            });
        });

        await fs.readdir(str + "commands", (err, files) => {
            if (err) return this.Logger.error("Error al encontrar la carpeta de comandos: " + err);
            files.forEach(file => {
                if (file.endsWith(".js") || file.endsWith(".ts")) {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const command = require(`../commands/${file}`);
                    this.Commands.set(command.name.toLowerCase(), command);
                }
            });
        });

        await fs.readdir(str + "commands/developers", (err, files) => {
            if (err) return this.Logger.error("Error al encontrar la carpeta de comandos de desarrolladores: " + err);
            files.forEach(file => {
                if (file.endsWith(".js") || file.endsWith(".ts")) {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    const command = require(`../commands/developers/${file}`);
                    this.develop.Commands.set(command.name.toLowerCase(), command);
                }
            });
        });

        await fs.readdir(str + "langs", (err, files) => {
            if (err) return this.Logger.error("Error al encontrar la carpeta de lenguajes: " + err);
            files.forEach(file => {
                if (file.endsWith(".js") || file.endsWith(".ts")) {
                    // eslint-disable-next-line @typescript-eslint/no-var-requires
                    this.langs[file.split(".")[0]] = require(`../langs/${file}`).default;
                }
            });
            this.Logger.start("Se han importado " + Object.keys(this.langs).length + " lenguajes");
        });

        try {
            await this.login(process.env.TOKEN)
            if (this.develop.loadApi)
                init(this)
            await mongo(this);
        } catch (e) {
            Logger.error("No se ha podido iniciar el cliente, comprueba que el token es correcto y que el bot tiene los intents requeridos");
            console.log(e)
        }

    }

    public restart(pos: number) {
        exec('pm2 restart ' + pos.toString());
    }
}
