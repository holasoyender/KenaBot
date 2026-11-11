/*
 * File:          blog.ts
 * Path:          v4\api\routes\blog.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-01 19:34:27 +02:00
 *
 * SHA-256: efbeef488d3c668c421b968e651bf581ab52fccf72ff562eb3e8f044014fb75c
 *
 */


import {Router} from "express";
import users from "../lib/users";
import {blog_schema, generateId} from "../database/schema/blog";

const router = Router();

router.get("/page/:number", async (req, res) => {

    const page_string = req.params.number;
    let page: number = 0
    try {
        page = parseInt(page_string);
    } catch (e) {
        res.status(400).send({
            status: 400,
            message: "Bad Request"
        })
        return
    }

    if (page < 0) {
        res.status(400).send({
            status: 400,
            message: "Bad Request"
        })
        return
    }

    const entries = await blog_schema.find().sort({created_at: -1}).skip(page * 10).limit(11);

    res.send({
        status: 200,
        message: "OK",
        page: page,
        size: entries.length == 11 ? 10 : entries.length,
        has_next_page: entries.length == 11,
        blogs: entries.slice(0, 10).map((entry) => {
            return {
                id: entry.id,
                title: entry.title,
                icon: entry.icon,
                banner: entry.banner,
                category: entry.category,
                tags: entry.tags,
                created_at: entry.created_at
            }
        })
    })
})

router.get("/id/:id", async (req, res) => {

    const id_string = req.params.id;
    let id: number = 0
    try {
        id = parseInt(id_string);
    } catch (e) {
        res.status(400).send({
            status: 400,
            message: "Bad Request"
        })
        return
    }

    if (id < 0) {
        res.status(400).send({
            status: 400,
            message: "Bad Request"
        })
        return
    }

    // @ts-ignore
    if(isNaN(id) || id == "NaN") {
        res.status(400).send({
            status: 400,
            message: "Bad Request"
        })
        return
    }

    const blog = await blog_schema.findOne({id: id});
    if (!blog) {
        res.status(404).send({
            status: 404,
            message: "Not Found"
        })
        return
    }

    res.send({
        status: 200,
        message: "OK",
        blog
    })
})

router.post("/create", async (req, res) => {

    const body = req.body;

    if (
        !body.title ||
        !body.icon ||
        !body.banner ||
        !body.category ||
        !body.tags ||
        !body.content ||
        !body.password
    ) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, missing fields"
        })
        return
    }

    if (body.title.length > 100 || body.title.length < 5) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, title length must be between 5 and 100 characters"
        })
        return
    }

    if (body.icon.length > 1000 || body.icon.length < 5) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, icon url must be between 5 and 1000 characters"
        })
        return
    }

    if (body.banner.length > 1000 || body.banner.length < 5) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, banner url must be between 5 and 1000 characters"
        })
        return
    }

    if (body.category.length > 100 || body.category.length < 2) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, category must be between 5 and 100 characters"
        })
        return
    }

    if (typeof body.tags !== "object") {
        res.status(400).send({
            status: 400,
            message: "Bad Request, tags must be an array"
        })
        return
    }

    if (body.tags.length > 10 || body.tags.length < 1) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, tags must be between 1 and 10"
        })
        return
    }

    if (body.content.length > 50000 || body.content.length < 10) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, content must be between 10 and 50000 characters"
        })
        return
    }

    const user = users.find(user => user.password === body.password)
    if (!user) {
        res.status(400).send({
            status: 400,
            message: "Bad Request, invalid password"
        })
        return
    }

    let id = await generateId();
    if (isNaN(id))
        id = Math.floor(Math.random() * 1000000000);

    const blog_raw = {
        id,
        title: body.title,
        icon: body.icon,
        banner: body.banner,
        category: body.category,
        tags: body.tags,
        created_at: Date.now(),
        content: body.content,
        author: {
            name: user.name,
            description: user.description,
            avatar: user.avatar
        }
    }

    const blog = new blog_schema(blog_raw);
    await blog.save();

    return res.send({
        status: 200,
        message: "OK",
        blog: blog_raw
    });

});
export default router;