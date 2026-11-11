/*
 * File:          together.ts
 * Path:          v2\utils\together.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-15 22:39:49 +02:00
 *
 * SHA-256: 8c2b94b9349593f8a17cfd9ff3166cc2964edec794ad4bb5eba835b231804b7f
 *
 */


import { VoiceChannel } from "discord.js";

export const apps = {
  youtube: { name: "YouTube Watch Together", id: "880218394199220334" },
  poker: { name: "Poker Night", id: "755827207812677713" },
  betrayal: { name: "Betrayal.io", id: "773336526917861400" },
  fishing: { name: "Fishing.io", id: "814288819477020702" },
  chess: { name: "Chess in the Park", id: "832012774040141894" },
  lettertile: { name: "LetterTile", id: "879863686565621790" },
  wordsnack: { name: "WordSnack", id: "879863976006127627" },
  doodlecrew: { name: "DoodleCrew", id: "878067389634314250" },
  awkword: { name: "AwkWord", id: "879863881349087252" },
  spellcast: { name: "SpellCast", id: "852509694341283871" },
  checkers: { name: "Checkers", id: "832013003968348200" },
  puttparty: { name: "PuttParty", id: "763133495793942528" },
  sketchheads: { name: "SketchHeads", id: "902271654783242291" },
  ocho: { name: "Blazing 8s", id: "832025144389533716" },
} as { [key: string]: { name: string; id: string } };

export default async (appID: string, channel: VoiceChannel) => {
  if (!appID) appID = "youtube";
  if (!apps[appID]) appID = "youtube";

  const app = apps[appID];
  let url = "unknown";
  try {
    const res = await fetch(
      `https://discord.com/api/v9/channels/${channel.id}/invites`,
      {
        method: "POST",
        body: JSON.stringify({
          max_age: 86400,
          max_uses: 0,
          target_application_id: app.id,
          target_type: 2,
          temporary: false,
          validate: null,
        }),
        headers: {
          Authorization: `Bot ${channel.client.token}`,
          "Content-Type": "application/json",
        },
      }
    );
    const data = await res.json();
    if (data.error || !data.code) url = "error";
    if (data.code == "50013" || data.code == 50013) url = "error";
    url = `https://discord.com/invite/${data.code}`;
  } catch (err) {
    url = "error";
  }

  return {
    name: app.name,
    id: app.id,
    url,
  };
};
