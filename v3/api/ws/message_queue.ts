/*
 * File:          message_queue.ts
 * Path:          v3\api\ws\message_queue.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 0c7472a8ef46a696fbb6ba97487d4bda73a0ae248d1fe297d678250cf5d0c4a1
 *
 */


let queue: { id: string, data: any }[] = [];

const get_from_queue = (id: string): { id: string, data: any }[] => {
    return queue.filter(item => item.id === id);
}

const get_from_queue_and_delete = (id: string): { id: string, data: any }[] => {
    const data = get_from_queue(id);
    remove_from_queue(id);
    return data;
}

const add_to_queue = (id: string, data: any) => {
    queue.push({id, data});
}

const remove_from_queue = (id: string) => {
    queue = queue.filter(item => item.id !== id);
}

const get_size = (): number => {
    return queue.length;
}

export default {
    get_from_queue,
    get_from_queue_and_delete,
    add_to_queue,
    remove_from_queue,
    get_size
}