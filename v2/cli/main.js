/*
 * File:          main.js
 * Path:          v2\cli\main.js
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 22:42:18 +01:00
 *
 * SHA-256: b55fb172f189d8c6a99d0916df4d46c5898d56183ccc0c05e444cc7954e01122
 *
 */


// eslint-disable-next-line @typescript-eslint/no-var-requires,no-undef
const chalk = require('chalk');
// eslint-disable-next-line @typescript-eslint/no-var-requires,no-undef
const inquirer = require('inquirer');
// eslint-disable-next-line @typescript-eslint/no-var-requires,no-undef
const { createSpinner } = require('nanospinner');
// eslint-disable-next-line @typescript-eslint/no-var-requires,no-undef
const fs = require('fs');

const sleep = (ms = 500) => new Promise((r) => setTimeout(r, ms));
let botName = ""
let token = ""
let sentry = ""
let spotify_id = ""
let spotify_secret = ""
let mongodb_url = ""
let genius = ""
let api_port = ""
let pm2_pos = ""
async function welcome() {

    console.log(
            `
██╗░░██╗  ███████╗  ███╗░░██╗  ░█████╗░    ░█████╗░  ██╗░░░░░  ██╗
██║░██╔╝  ██╔════╝  ████╗░██║  ██╔══██╗    ██╔══██╗  ██║░░░░░  ██║
█████═╝░  █████╗░░  ██╔██╗██║  ███████║    ██║░░╚═╝  ██║░░░░░  ██║
██╔═██╗░  ██╔══╝░░  ██║╚████║  ██╔══██║    ██║░░██╗  ██║░░░░░  ██║
██║░╚██╗  ███████╗  ██║░╚███║  ██║░░██║    ╚█████╔╝  ███████╗  ██║
╚═╝░░╚═╝  ╚══════╝  ╚═╝░░╚══╝  ╚═╝░░╚═╝    ░╚════╝░  ╚══════╝  ╚═╝`);

    await sleep();

    console.log(`
    ${chalk.red('COMO FUNCIONA')} 
    Bienvenido al ${chalk.green("manager de configuración de KenaBot")}.
    Este proceso generará un archivo ${chalk.red(".env")} con las variables de configuración necesarias para que KenaBot funcione.
    Si surge alguna duda/error, ${chalk.blue("puedes consultarme en el servidor de Soporte de KenaBot")}.
  `);
}

async function handleAnswer(type, answer) {
    const spinner = createSpinner('Registrando respuesta...').start();
    await sleep();

    switch (type) {
        case 'botName':
            botName = answer;
            spinner.success({ text: `Nombre del bot guardado como ${chalk.yellow(chalk.bold(answer))}` });
            break;
        case 'token':
            token = answer;
            spinner.success({ text: `Token para ${chalk.yellow(chalk.bold(botName))} establecido` });
            break;
        case "sentry":
            sentry = answer;
            spinner.success({ text: `Sentry activado` });
            break;
        case "spotify_id":
            spotify_id = answer;
            spinner.success({ text: `ID de Spotify establecido` });
            break;
        case "spotify_secret":
            spotify_secret = answer;
            spinner.success({ text: `Secret de Spotify establecido` });
            break;
        case "mongo":
            mongodb_url = answer;
            spinner.success({ text: `URL de MongoDB establecida` });
            break;
        case "genius":
            genius = answer;
            spinner.success({ text: `Token de Genius establecido` });
            break;
        case "api_port":
            api_port = answer;
            spinner.success({ text: `Puerto para la API establecido` });
            break;
        case "pm2_pos":
            pm2_pos = answer;
            spinner.success({ text: `Posición para PM2 establecida` });
            break;
        default:
            spinner.error({ text: `error!` });
            // eslint-disable-next-line no-undef
            process.exit(1);
            break;
    }
}

async function askBotName() {
    const answers = await inquirer.prompt({
        name: 'bot_name',
        type: 'list',
        message: 'Cual es el nombre de este bot?',
        choices: [
            'KenaBot',
            'KenaBot 2',
            'KenaBot 3',
            'KenaBot 4',
            'KenaBot Beta',
        ],
    });
    return handleAnswer("botName", answers.bot_name);
}

async function q1() {
    const answers = await inquirer.prompt({
        name: 'bot_token',
        type: 'input',
        message: `Por favor, especifica el token de ${botName}:`,
        default() {
            return "T0K3N";
        }
    });

    return handleAnswer("token", answers.bot_token);
}

async function q2() {
    const answers = await inquirer.prompt({
        name: 'sentry_dns',
        type: 'input',
        message: `Tu DNS de Sentry para registrar errores:`,
        default() {
            return "SentryDNS";
        }
    });
    return handleAnswer("sentry", answers.sentry_dns);
}

async function q3() {
    const answers = await inquirer.prompt({
        name: 'spotify_id',
        type: 'input',
        message: `La ID de tu API de Spotify (para la música):`,
        default() {
            return "SpotifyID";
        }
    });

    return handleAnswer("spotify_id", answers.spotify_id);
}

async function q4() {
    const answers = await inquirer.prompt({
        name: 'spotify_token',
        type: 'input',
        message: `El secret de tu API de Spotify (para la música):`,
        default() {
            return "SpotifySecret";
        }
    });

    return handleAnswer("spotify_secret", answers.spotify_token);
}

async function q5() {
    const answers = await inquirer.prompt({
        name: 'mongodb_url',
        type: 'input',
        message: `La URL de tu base de datos de MongoDB:`,
        default() {
            return "mongodb://localhost:27017/KenaBot";
        }
    });

    return handleAnswer("mongo", answers.mongodb_url);
}

async function q6() {
    const answers = await inquirer.prompt({
        name: 'genius_token',
        type: 'input',
        message: `El token de tu API de Genius (lyrics):`,
        default() {
            return "GeniusToken";
        }
    });

    return handleAnswer("genius", answers.genius_token);
}

async function q7() {
    const answers = await inquirer.prompt({
        name: 'api_port',
        type: 'list',
        message: `El puerto para la API de ${botName} (distinto a los demás):`,
        choices: [
            '4444',
            '4420',
            '4460',
            '4480',
            '3333',
        ],
    });

    return handleAnswer("api_port", answers.api_port);
}
async function q8() {
    const answers = await inquirer.prompt({
        name: 'pm2_pos',
        type: 'list',
        message: `La posición de PM2 en la que ${botName} estará (distinto a los demás):`,
        choices: [
            '0',
            '1',
            '2',
            '3',
            '4',
            '5'
        ],
    });

    return handleAnswer("pm2_pos", answers.pm2_pos);
}

async function end() {
    let env = `
    # Archivo de entorno para el bot ${botName}
    # Generado el ${new Date().toLocaleString()}
        
TOKEN=${token.replace(/ /g, "")}
SENTRY=${sentry.replace(/ /g, "")}
SPOTIFY_ID=${spotify_id.replace(/ /g, "")}
SPOTIFY_SECRET=${spotify_secret.replace(/ /g, "")}
MONGODB_URL=${mongodb_url.replace(/ /g, "")}
GENIUS_API=${genius.replace(/ /g, "")}
API_PORT=${api_port.replace(/ /g, "")}
PM2_POSITION=${pm2_pos.replace(/ /g, "")}`;

    fs.writeFile('.env', env, function (err) {
        if (err) return console.log(err);
        console.log(`${chalk.green("√")} Se ha guardado toda la configuración en el archivo ${chalk.red(".env")}`);
    });

}


(async () => {
    console.clear();
    await welcome();
    await askBotName();

    await q1();
    await q2();
    await q3();
    await q4();
    await q5();
    await q6();
    await q7();
    await q8();
    await end();
})();
