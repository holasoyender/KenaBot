/*
 * File:          index.js
 * Path:          v4\api\lib\delay\index.js
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: b0b346cc005d51b69bb6f7e57d726d767c32e1b4c0b82de02a69e327576e035c
 *
 */


'use strict';

// From https://github.com/sindresorhus/random-int/blob/c37741b56f76b9160b0b63dae4e9c64875128146/index.js#L13-L15
const randomInteger = (minimum, maximum) => Math.floor((Math.random() * (maximum - minimum + 1)) + minimum);

const createAbortError = () => {
    const error = new Error('Delay aborted');
    error.name = 'AbortError';
    return error;
};

const createDelay = ({clearTimeout: defaultClear, setTimeout: set, willResolve}) => (ms, {value, signal} = {}) => {
    if (signal && signal.aborted) {
        return Promise.reject(createAbortError());
    }

    let timeoutId;
    let settle;
    let rejectFn;
    const clear = defaultClear || clearTimeout;

    const signalListener = () => {
        clear(timeoutId);
        rejectFn(createAbortError());
    };

    const cleanup = () => {
        if (signal) {
            signal.removeEventListener('abort', signalListener);
        }
    };

    const delayPromise = new Promise((resolve, reject) => {
        settle = () => {
            cleanup();
            if (willResolve) {
                resolve(value);
            } else {
                reject(value);
            }
        };

        rejectFn = reject;
        timeoutId = (set || setTimeout)(settle, ms);
    });

    if (signal) {
        signal.addEventListener('abort', signalListener, {once: true});
    }

    delayPromise.clear = () => {
        clear(timeoutId);
        timeoutId = null;
        settle();
    };

    return delayPromise;
};

const createWithTimers = clearAndSet => {
    const delay = createDelay({...clearAndSet, willResolve: true});
    delay.reject = createDelay({...clearAndSet, willResolve: false});
    delay.range = (minimum, maximum, options) => delay(randomInteger(minimum, maximum), options);
    return delay;
};

const delay = createWithTimers();
delay.createWithTimers = createWithTimers;

module.exports = delay;
// TODO: Remove this for the next major release
module.exports.default = delay;
