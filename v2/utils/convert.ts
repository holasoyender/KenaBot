/*
 * File:          convert.ts
 * Path:          v2\utils\convert.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: 0e0826d66b465ede146726a3eb9701b47b1fcb1b5c4a1cb783e2c7e3228b13ab
 *
 */


export const convertTime = function (duration:number) {

    let seconds: number | string = parseInt(String((duration / 1000) % 60)),
        minutes: number | string = parseInt(String((duration / (1000 * 60)) % 60)),
        hours: number | string = parseInt(String((duration / (1000 * 60 * 60)) % 24));


    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;

    if(duration <= 0) {
        return "00:00:00";
    }

    if (duration < 3600000) {
        return minutes + ":" + seconds;
    } else {
        return hours + ":" + minutes + ":" + seconds;
    }
}