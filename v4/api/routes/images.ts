/*
 * File:          images.ts
 * Path:          v4\api\routes\images.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-25 23:27:32 +02:00
 *
 * SHA-256: 4a7af7bd520dd2fc03f7dae41f700aa98a0d9d9d9057d434f9940878ec39376e
 *
 */


import {Router} from "express";
import canvas_api from "canvas";

const router = Router();

router.post("/artists-top-tracks/:artist", async (req, res) => {
    const artist = req.params.artist;
    const token = req.headers.authorization;

    if (!artist || !token)
        return res.status(400).send({status: 400, message: "Bad Request"});

    if (token !== process.env.WS_AUTHORIZATION)
        return res.status(401).send({status: 401, message: "Unauthorized"});

    try {
        const body = req.body;

        if (!body.images || !body.images.length)
            return res.status(400).send({status: 400, message: "Bad Request"});

        const images = body.images;
        const size = images.length;

        const canvas = canvas_api.createCanvas(926, 200);
        const ctx = canvas.getContext('2d');

        for (const image of (images as string[])) {
            const index = (images as string[]).indexOf(image);

            const img = await canvas_api.loadImage(image);

            if (size == 1) {
                ctx.save();
                roundedImage(ctx, 387, 24, 152, 152, 20);
                ctx.clip();
                await ctx.drawImage(img, 387, 24, 152, 152);
                ctx.restore();
            }

            if (size == 2) {

                if (index == 0) {
                    ctx.save();
                    roundedImage(ctx, 287, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 287, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 1) {
                    ctx.save();
                    roundedImage(ctx, 487, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 487, 24, 152, 152);
                    ctx.restore();
                }
            }

            if (size == 3) {

                if (index == 0) {
                    ctx.save();
                    roundedImage(ctx, 187, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 187, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 1) {
                    ctx.save();
                    roundedImage(ctx, 387, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 387, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 2) {
                    ctx.save();
                    roundedImage(ctx, 587, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 587, 24, 152, 152);
                    ctx.restore();
                }
            }

            if (size == 4) {

                if (index == 0) {
                    ctx.save();
                    roundedImage(ctx, 97, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 97, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 1) {
                    ctx.save();
                    roundedImage(ctx, 287, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 287, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 2) {
                    ctx.save();
                    roundedImage(ctx, 487, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 487, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 3) {
                    ctx.save();
                    roundedImage(ctx, 677, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 677, 24, 152, 152);
                    ctx.restore();
                }
            }

            if (size == 5) {

                if (index == 0) {
                    ctx.save();
                    roundedImage(ctx, 27, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 27, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 1) {
                    ctx.save();
                    roundedImage(ctx, 207, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 207, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 2) {
                    ctx.save();
                    roundedImage(ctx, 387, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 387, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 3) {
                    ctx.save();
                    roundedImage(ctx, 567, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 567, 24, 152, 152);
                    ctx.restore();
                }

                if (index == 4) {
                    ctx.save();
                    roundedImage(ctx, 747, 24, 152, 152, 20);
                    ctx.clip();
                    await ctx.drawImage(img, 747, 24, 152, 152);
                    ctx.restore();
                }
            }

        }

        return res.status(200).contentType("image/png").send(canvas.toBuffer());

    } catch (e: any) {
        console.error(e)
        return res.status(400).send({status: 400, message: "Bad Request", error: e?.message || "Unknown Error"});
    }
});

function roundedImage(ctx: canvas_api.CanvasRenderingContext2D, x: number, y: number, width: number, height: number, radius: number) {
    ctx.beginPath();
    ctx.moveTo(x + radius, y);
    ctx.lineTo(x + width - radius, y);
    ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
    ctx.lineTo(x + width, y + height - radius);
    ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
    ctx.lineTo(x + radius, y + height);
    ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
    ctx.lineTo(x, y + radius);
    ctx.quadraticCurveTo(x, y, x + radius, y);
    ctx.closePath();
}


export default router;
