/*
 * File:          progessbar.ts
 * Path:          v2\functions\progessbar.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-02 20:22:17 +01:00
 *
 * SHA-256: a9b96b49688dd5f8b4d15f941d54c383d19f03ce55e7da25432e716709fa56a2
 *
 */


export default (total: number, current: number, size = 40, line = '▬', slider = '🔘') => {
    if (current > total) {
        const bar = line.repeat(size + 2);
        const percentage = (current / total) * 100;
        return [bar, percentage];
    } else {
        const percentage = current / total;
        const progress = Math.round((size * percentage));
        const emptyProgress = size - progress;
        const progressText = line.repeat(progress).replace(/.$/, slider);
        const emptyProgressText = line.repeat(emptyProgress);
        const bar = progressText + emptyProgressText;
        const calculated = percentage * 100;
        return [bar, calculated];
    }
};