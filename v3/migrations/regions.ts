/*
 * File:          regions.ts
 * Path:          v3\migrations\regions.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-15 13:50:16 +02:00
 *
 * SHA-256: 6684b12c0cbea46c69009f43338a29244cf27bcf646f3e40e691a249ec94819a
 *
 */


import mongoose from "mongoose"
import fs from "fs"

mongoose.connect("mongodb://localhost:27017/Stats")

const db = mongoose.connection

db.on("error", console.error.bind(console, "connection error:"))
db.once("open", async () => {
    console.log("Connected to MongoDB, running migrations...")

    console.log("Fetching all documents...")
    const allDocuments = await db.collection("Regions").find({}).toArray()
    console.log("OK Fetched all documents. (", allDocuments.length, "documents )")


    console.log("!!!! Saving a backup of the database in case something goes wrong... !!!!")
    const existingFile = fs.existsSync("backup.json")
    if (existingFile) {
        console.log("Existing backup found, creating a new one...")
        fs.renameSync("backup.json", `backup-${Math.round(Math.random() * 10000000)}.json.bak`)
        console.log("OK Created backup.")
    }
    const file = fs.createWriteStream("backup.json")
    file.write(JSON.stringify(allDocuments))
    console.log("OK Saved backup. (", allDocuments.length, "documents )")


    console.log("Converting documents...")
    const regions: [string, number][] = allDocuments.map((doc) => [(doc.region as string).split(".")[0].replace(/[0-9]/g, ""), doc.count])
    console.log("OK Converted documents. (", regions.length, "regions )")

    console.log("Merging duplicated regions...")
    const regionsMap = new Map<string, number>()
    regions.forEach(([region, count]) => {
        if (regionsMap.has(region)) {
            regionsMap.set(region, regionsMap.get(region)! + count)
        } else {
            regionsMap.set(region, count)
        }
    }
    )
    console.log("OK Merged regions. (", regionsMap.size, "unique regions )")

    console.log("Deleting old documents...")
    const deleted = await db.collection("Regions").deleteMany({})
    console.log("OK Deleted old documents. (", deleted.deletedCount, "documents )")

    console.log("Inserting new documents...")
    const inserted = await db.collection("Regions").insertMany(
        Array.from(regionsMap.entries()).map(([region, count]) => ({ region, count }))
    )
    console.log("OK Inserted new documents. (", inserted.insertedCount, "documents )")

    console.log("Done.")
})
