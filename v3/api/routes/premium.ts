/*
 * File:          premium.ts
 * Path:          v3\api\routes\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-01 22:32:23 +02:00
 *
 * SHA-256: bec70955bade5fd3bdcd6ab9e3a5f205e8795516072046e95ebf921b2e1c4e99
 *
 */


import {Router} from "express";
import {compute_hash, generate_codes} from "../lib/crypto";
import {patreon_raw_schema, patreon_schema} from "../database/schema/patreon";
import {v4 as idv4} from "uuid";
import {transport} from "../mail/transport";
import delay from "../lib/delay";
import message_queue from "../ws/message_queue";
import {get_socket_by_id} from "../ws/websocket";

const router = Router()

router.post("/", async (req, res) => {
    const signature = req.headers["x-patreon-signature"];
    if (!signature)
        return res.status(401).send({status: 401, message: "Unauthorized"});

    const hash = compute_hash(signature as string, (req as any).rawBody);

    if (hash !== signature)
        return res.status(401).send({status: 401, message: "Unauthorized"});

    const is_duplicated = await patreon_raw_schema.findOne({data: req.body});
    if (is_duplicated) {
        console.log("Duplicated request with id " + hash);
        return res.status(200).send({status: 200, message: "OK"});
    }

    await patreon_raw_schema.create({data: req.body});

    const data: PatreonBody = req.body;
    if (data?.data?.type === "member" && data?.data?.attributes?.patron_status === "active_patron" && data?.data?.attributes?.last_charge_status == "Paid") {

        const email = data.data.attributes.email || "unknown";
        const full_name = data.data.attributes.full_name || "unknown";
        const patron_status = data.data.attributes.patron_status || "unknown";
        const will_pay = data.data.attributes.will_pay_amount_cents || 0;
        const discord_id = data.included?.[1].attributes?.social_connections?.discord?.user_id || "unknown";
        const patreon_id = data.data.id || "unknown";

        const tier = {
            name: data.included?.[2]?.attributes?.title || "Unknown",
            amount: data.included?.[2]?.attributes?.amount_cents || 0,
        }

        if (email == "unknown")
            return res.status(400).send({status: 400, message: "Bad Request"});

        const codes: string[] = []
        switch (tier.name) {
            case "KenaBot Premium":
                codes.push(...generate_codes(1));
                break;
            case "KenaBot Premium (x2 servers)":
                codes.push(...generate_codes(2));
                break;
            case "KenaBot Premium (x3 servers)":
                codes.push(...generate_codes(3));
                break;
            default:
                //codes.push(...generate_codes(1));
                return res.status(200).send({status: 200, message: "OK"});
        }

        const reference = idv4().replace(/-/g, "") + idv4().replace(/-/g, "");

        try {
            const mail = await transport.sendMail(
                {
                    from: '"The Kena Team" <noreply.kenabot@gmail.com>',
                    to: email,
                    subject: 'Thanks for your support!',
                    text: `Hello ${full_name},
                    
Thanks for supporting us! You can claim your new donor mode by clicking this link:
- https://claim.kenabot.xyz/?code=${reference}
                     
If that link does not work for you, you can try and claim it manually with the /premium command in your discord server(s) using the following code(s):
${codes.join("\n")}

${discord_id == "unknown" ? `For future donations, we recommend you to link your discord account to your patreon account. You can do this by going to your patreon settings and linking your discord account there.` : `Note: Your discord account with id '${discord_id} is already linked to your patreon account, so you must login with that account to claim your premium mode.`}

Thanks for your support!
The Kena Team`,
                    priority: "high"
                }
            );

            if (mail.rejected.length > 0) return res.status(500).send({
                status: 500,
                message: "The email could not be sent"
            });
            if (mail.accepted.length === 0) return res.status(500).send({
                status: 500,
                message: "The email could not be sent"
            });

            await patreon_schema.create({
                reference,
                email,
                full_name,
                patron_status,
                will_pay,
                discord_id,
                patreon_id,
                tier,
                codes,
            })

        } catch (e) {
            return res.status(400).send({status: 500, message: "The email could not be sent"});
        }
    }

    res.send({status: 200, message: "OK"});
})

router.get("/:reference", async (req, res) => {

    const user: any = req.user
    if (!user) return res.status(401).send({status: 401, message: "Unauthorized"});

    const reference = req.params.reference;

    const patreon = await patreon_schema.findOne({reference});
    if (!patreon) return res.status(404).send({status: 404, message: "Not Found"});
    if(patreon.claimed) return res.status(400).send({status: 400, message: "Bad Request: Already claimed"});

    if (patreon.discord_id == "unknown") {
        patreon.discord_id = user.id;
        patreon.save();
    } else
        if (patreon.discord_id !== user.id)
            return res.status(401).send({status: 401, message: "Unauthorized"});

    res.status(200).send(
        {
            status: 200,
            user: {
                username: user.username,
                avatar: user.avatar,
                full_name: patreon.full_name,
                tier: patreon.tier,
            },
            codes: patreon.codes,
            reference: patreon.reference
        });
})

router.post("/:reference", async (req, res) => {

    const user: any = req.user
    if (!user) return res.status(401).send({status: 401, message: "Unauthorized"});

    const reference = req.params.reference;

    const patreon = await patreon_schema.findOne({reference});
    if (!patreon) return res.status(404).send({status: 404, message: "Not Found"});

    if (patreon.claimed) return res.status(400).send({status: 400, message: "Bad Request: Already claimed"});
    if (patreon.discord_id !== user.id) return res.status(401).send({status: 401, message: "Unauthorized"});

    let discord_ids = req.body.ids;
    if (!discord_ids) return res.status(400).send({status: 400, message: "Bad Request: No ids"});
    if (!Array.isArray(discord_ids)) return res.status(400).send({status: 400, message: "Bad Request: Not an array"});
    if (discord_ids.length === 0) return res.status(400).send({status: 400, message: "Bad Request: Empty array"});
    discord_ids = [...new Set(discord_ids)];
    if (discord_ids.length > patreon.codes.length) return res.status(400).send({status: 400, message: "Bad Request: Too many ids"});
    if (discord_ids.length < patreon.codes.length) return res.status(400).send({status: 400, message: "Bad Request: Too few ids"});
    if (discord_ids.some((id: any) => typeof id !== "string")) return res.status(400).send({status: 400, message: "Bad Request: Not all are strings"});

    const codes = patreon.codes;

    for (let i = 0; i < codes.length; i++) {
        const code = codes[i];
        const id = discord_ids[i];

        for (let j = 1; j < 5; j++) {
            const client = get_socket_by_id(j.toString())
            if (!client)
                return res.status(500).send({
                    status: 500,
                    message: `${i}:${j}:${id}: One of the bot seems to be offline, please try again later`
                })

            const random = idv4();

            client.send(JSON.stringify({
                type: "action",
                action: "/claim",
                method: "POST",
                id: random,
                data: {
                    user: req.user ? {
                        id: (req.user as any).id,
                        username: (req.user as any).username,
                        avatar: (req.user as any).avatar,
                        guilds: (req.user as any).guilds.map((i: any) => {
                            return {
                                id: i.id,
                                name: i.name,
                                icon: i.icon,
                                owner: i.owner,
                                permissions: i.permissions
                            }
                        }) || []
                    } : null,
                    params: [id, code],
                    body: ""
                }
            }))

            for (let k = 0; k < 10; k++) {
                const response = message_queue.get_from_queue_and_delete(random)
                if (response && response.length > 0) {
                    if (response[0].data) {

                        if (response[0].data.status) {
                            if (response[0].data.status === 200) {
                                if (i === codes.length - 1 && j === 4) {
                                    patreon.claimed = true;
                                    patreon.save();
                                }
                            } else
                                return res.status(response[0].data.status).send(response[0].data);
                        } else {
                            return res.status(500).send({
                                status: 500,
                                data: response[0].data
                            })
                        }

                    } else
                        return res.status(500).send({
                            status: 404,
                            message: `${i}:${j}:${id}: Server did not respond, please try again later`
                        })
                } else
                    await delay(500);
            }
        }
    }

    res.status(500).send({  status: 500, message: "Server did not respond, please try again later" });
});

export default router;