/*
 * File:          premium.ts
 * Path:          v4\api\routes\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-07 16:59:49 +02:00
 *
 * SHA-256: 0703cc74d5cecb2988cefa336665da66e186bc13cc6966fc43e3cbb062ba5c26
 *
 */


import {Router} from "express";
import {compute_hash, generate_codes} from "../lib/crypto";
import {patreon_raw_schema, patreon_schema} from "../database/schema/patreon";

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

    if (data?.data?.type === "member" && data?.data?.attributes?.patron_status === "active_patron") {

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

        const codes: string[] = []
        switch (tier.name) {
            case "1 Servidor":
                codes.push(...generate_codes(1));
                break;
            case "2 Servidores":
                codes.push(...generate_codes(2));
                break;
            case "3 Servidores":
                codes.push(...generate_codes(3));
                break;
            case "5 Servidores":
                codes.push(...generate_codes(5));
                break;
            default:
                codes.push("Ha ocurrido un error, por favor contacta a un administrador.");
                break;
        }

        await patreon_schema.create({
            email,
            full_name,
            patron_status,
            will_pay,
            discord_id,
            patreon_id,
            claimed: false,
            tier,
            codes,
            claimed_at: null
        })
    }

    res.send({status: 200, message: "OK"});
})

router.get("/", async (req, res) => {
    const user: any = req.user
    if (!user) return res.status(401).send({status: 401, message: "Unauthorized"});

    const patreon = await patreon_schema.findOne({discord_id: user.id, claimed: false});
    if (!patreon) return res.status(200).send({status: 200, codes: []});

    res.status(200).send(
        {
            status: 200,
            user: {
                username: user.username,
                avatar: user.avatar,
                full_name: patreon.full_name,
                payment_cents: patreon.will_pay,
                tier: patreon.tier,
            },
            codes: patreon.codes
        });

    patreon.claimed = true;
    patreon.claimed_at = new Date();
    patreon.save();
})

export default router;