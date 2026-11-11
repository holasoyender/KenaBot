/*
 * File:          en-US.ts
 * Path:          v2\langs\en-US.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-09 17:43:12 +02:00
 *
 * SHA-256: 61b008482cccec84ace150e405dc4800c455b73883cac319b52ff325c9818077
 *
 */


import emojis from "../utils/emojis";
export default {
    flag: "🇺🇸",
    name: "Inglés",
    description: "Inglés de Estados Unidos",
    events: {
        guildCreate: {
            embed_author: "Thanks for adding me to %s%!!",
            embed_desc: "If you have any questions about how the bot works, you can access my **[web page](https://kenabot.xyz/)** , where you will find all the information you need.\n" +
                "\n" +
                "If you want to configure me you can do it from my **[web dashboard](https://dashboard.kenabot.xyz/)**, and if you have any doubts you can visit my **[support server](https://support.kenabot.xyz/)**!",
            buttons: {
                web: "My web page",
                support: "My support server",
                dash: "My dashboard"
            }
        },
        dm: {
            embed_title: "Hello :wave: I am %s%",
            embed_description: "I'm the music bot your server needs!\nIf you want a little more **information about me**, you can click the **buttons!**"
        },
        trackStart: {
            embed_author: "|  Now playing",
        },
        interactionCreate: {
            unknown_command: emojis.error + " I could not find the command you typed",
            cant_use_button: emojis.error + " You cannot use this button",
        }
    },

    commands: {
        play: {
            no_vc: emojis.error + "I can't play music unless I am on a voice channel!",
            no_vc_perms: emojis.error + "I do not have permissions to play music on this channel!",
            different_vc: emojis.error + "I'm already playing music on another channel!",
            no_player: emojis.error + "I have no player available!",
            no_song: emojis.error + "You must specify a song!",
            no_song_found: emojis.error + "I have not found any song with that name!",
            youtube_link: emojis.error + "I can't play songs from YouTube!",
            vc_disabled: emojis.error + "An administrator has disabled this voice channel!",
            song: "song",
            songs: "songs",
            duration: "Duration",
            in_queue: "In queue",
            pause_resume: "Pause/Resume",
            stop: "Stop",
        },
        queue: {
            embed_footer: "Page %s% of %d% (%f% total songs)",
            embed_author: "Play list",
            now_playing: "Now playing",
            no_previous: "No previous songs",
            no_next: "No more songs",
        },
        pause: {
            already_paused: emojis.error + "The song is already paused!",
            paused: emojis.pause + "The song has **paused**!",
            resumed: emojis.resume + "The song has **resumed**!",
            already_resumed: emojis.error + "The song is not paused!",
        },
        stop: "The playlist is now **empty**!",
        stop_leave: "The playlist is now **empty** and I have left the voice channel!",
        skip: "The song has been **skipped**!",
        skip_vote: {
            vote: "**Started the vote to skip the song!**\nClick on the button to vote, for the song to be skipped more than 50% of the users must vote.\nCurrent percentage: `%p%%`.",
            label: "Vote to skip",
            announce: "**I have sent a message for all voice channel users to vote to skip the song!**\nRemember to click the button to vote.",
            already_voted: emojis.error + "You have already voted to skip the song!",
            cant_vote: emojis.error + "You can't vote to skip the song!",
            voted: emojis.success + "You have voted to skip the song!",
            canceled: "**Skip vote cancelled**.\nLooks like not enough users have voted to skip the song.",
            completed: "The `%s%%` of users have decided to skip the song.",
            redirect: "**Only the DJ can execute this command!**\nIf you want to start a vote to skip a song you can execute the command `/skip`.\nIf you want to start a vote to skip the song you can execute the `/skip` command."
        },
        volume: {
            info: emojis.volume + "Current volume: **%s%%**",
            invalid_level: emojis.error + "You must specify a volume level between **0** and **100**!",
            set: emojis.volume + "The volume has been changed to **%s%%**!",
        },
        loop: {
            info: emojis.loop + "The loop is **%s%**",
            already: emojis.error + "The loop is already **%s%%**!",
            action: emojis.loop + "The loop has **%s%**!",
            repeat: "activated",
            no_repeat: "deactivated",
        },
        now_playing: {
            remaining: "Remaining ",
        },
        premium: {
            no_code: emojis.error+"You need to enter a code",
            invalid_code: emojis.error+"The code you entered is invalid",
            used_code: emojis.error+"The code you entered has already been used",
            bot_claimed: emojis.error+"The code you entered has already been claimed on this bot",
            already_premium: emojis.error+"You are already in donor mode!",
            enabled: emojis.success+"Donor mode has been enabled for **%t%**!",
        },
        invite: emojis.success + "You can invite me to your server by clicking on the button!",
        support: emojis.success + "You can visit my support server for help and more information by clicking on the button!",
        shuffle: emojis.shuffle + "The playlist has been **mixed**!",
        dashboard: emojis.success + "You can visit my web dashboard to configure me by clicking on the button!",
        remove: {
            invalid: emojis.error + "There is no song in the **%s%%** position!",
            success: emojis.success + "The song `%s%%` has been **deleted**!",
        },
        skip_to: emojis.resume + "Skipped **%s%%** songs forward!",
        clear_queue: emojis.success + "The queue is now **empty**!",
        seek: {
            max_duration: emojis.error + "I can't jump to a position longer than the length of the song!",
            invalid_time: emojis.error + "You must specify a valid time!",
            forward_success: emojis.resume + "The song has been **forwarded** to `%s%%`!",
            backward_success: emojis.resume + "The song has been **delayed** to `%s%%`!",
        },
        back: {
            no_previous: emojis.error + "There is no previous song!",
            success: emojis.resume + "Back to the previous song!",
        },
        filters: {
            no_filters: emojis.error + "You must specify at least one filter!",
            bass_boost: emojis.success + "The `Bass Boost` filter has been **activated**!",
            ocho_d: emojis.success + "The `8D` filter has been **activated**!",
            nightcore: emojis.success + "The `Nightcore` filter has been **activated**!",
            vaporwave: emojis.success + "The `Vaporwave` filter has been **activated**!",
            clear: emojis.success + "All filters have been **disabled**!",
            treble: emojis.success + "The `Treble` filter has been **activated**!",
            karaoke: emojis.success + "The `Karaoke` filter has been **activated**!",
            tremolo: emojis.success + "The `Tremolo` filter has been **activated**!",
            speed: emojis.success + "The `Speed` filter has been **activated**!",
            unknown: emojis.error + "This filter is not recognized!",
            pitch: emojis.success + "The `Pitch` filter has been **activated**!",
        },
        _247: {
            no_channel: emojis.error + "You must specify a **voice** channel in order to activate the **24/7** mode!",
            enabled: emojis.success + "The **24/7** mode has been **activated** for the %s% channel!",
            disabled: emojis.success + "The **24/7** mode has been **disabled**!",
        },
        help: {
            desc: "**Hello :wave:**, I'm **%s%**!\nI'm the music bot your server needs! I can play songs from **Spotify**, **Soundcloud**, **Twitch** and [much more](https://kenabot.xyz)!\n\n" +
                "**Commands**\n" +
                "You can see information about all my commands **[here](https://kenabot.xyz/commands)** or by clicking on the button below!\n\n" +
                "**Premium/Donations**\n" +
                "The **[KenaBot for Donors](https://kenabot.xyz/premium)** version will give you access to new features and enhancements to the bot, such as **24/7**, **audio effects** and a **web player**!!!!\n" +
                "[Click here to learn more](https://kenabot.xyz/premium)\n\n" +
                "**Add me to your server**\n" +
                "You can add me to as many servers as you want by clicking **[here](https://discord.com/api/oauth2/authorize?client_id=%i%&permissions=3534272&scope=bot%20applications.commands)** or the button!\n\n" +
                "**Support server**\n" +
                "On my support server you will find all kinds of help and more information about me, you can join by clicking **[here](https://kenaboy.xyz/support)** or on the button!",
            commands: {
                title: "Commands",
            },
            add: {
                title: "Add me to your server",
                add: "Add me!"
            },
            support: {
                title: "Support server",
                support: "Support"
            }
        },
        commands: "You can see information about all my commands **[here](https://kenabot.xyz/commands)** or by clicking on the button below!",
        lyrics: {
            loading: emojis.search + "I'm looking for the song...\n`If I haven't sent a message with the lyrics, it's possible that I haven't found the song.`",
            error: emojis.error + "No song with that name has been found!"
        },
        config: {
            title: "Server configuration",
            description: "**Hello** :wave: welcome to the bot configuration wizard <@!%s%>.\n\nIf you have any questions about the bot and its configuration, you can go to my **[support server](https://kenabot.xyz/soporte)** or check my **[web page](https://kenabot.xyz)**.",
            help: "If you need help, you can consult my **[web page](https://kenabot.xyz)** or the **[support server](https://kenabot.xyz/soporte)**.",
            buttons: {
                lang: "Language",
                channels: "Blocked channels",
                dj: "DJ Role",
                more: "More",
                commands: "Blocked commands",
                premium_on: "Donor mode activated",
                premium_off: "Donor mode deactivated",
                back: "Back",
                voice_channels: "Blocked voice channels",
                beta_on: "You are in beta",
                beta_off: "You are not in the beta",
                embed: "Embed color",
                database: "Information we store",
                select_all: "Select all",
                remove_all: "Remove all"
            },
            lang: {
                title: "Language Selection",
                description: "Open the menu to choose the language in which you want the bot messages to be displayed.\n\nIf you don't see your language you can join my [support server](https://kenabot.xyz/soporte) and contribute as a translator!",
                select: "Select language"
            },
            channels: {
                title: "Blocked channels",
                description: "Open the menu to choose the channels in which I will not answer messages.",
                select: "Select channels",
            },
            embed: {
                title: "Embed color configuration",
                description: "Open the menu to choose the embed color that will be shown in all bot messages.\n\nThis menu has only default colors, if you want to use a custom color you can go to my [dashboard web](https://kenabot.xyz/dashboard).",
                select: "Embed color",
            },
            premium_on: {
                title: "You already have the donor mode activated",
                description_forever: "**Thank you very much for contributing to the development of KenaBot!!!!**.\nAnd we have good news... Your donor mode is **permanently** activated!",
                description: "**Thank you very much for contributing to the development of KenaBot!!!!**.\nLooks like you have <t:%s%:R> of donor mode left, enjoy all the things that this mode brings!",
            },
            premium_off: {
                title: "It seems that you do not have the donor mode activated",
                description: "If you want to donate to my development, you can do it from my [web page](https://kenabot.xyz/premium)\nYou will receive the donor mode and you will be able to enjoy all the things that this mode brings!",
            },
            database: {
                title: "This is all the information we have stored about your server:",
                raw: "Raw:",
                read: "Human legible:",
                delete: "Delete my server information",
                privacy: "Privacy policy",
                confirm: "Delete",
                cancel: "Cancel"
            },
            commands: {
                title: "Blocked commands",
                description: "Open the menu to choose the commands that I will not respond to.\nIf a user without permissions tries to execute a command that is blocked, I will respond with an error message.",
                select: "Select the commands",
            },
            voice: {
                title: "Blocked voice channels",
                description: "Open the menu to choose the voice channels I will not be able to join.",
                select: "Select channels",
            },
            djrole: {
                title: "DJ Role",
                description: "Open the menu to choose the role of the DJ, who will be able to execute all music commands without limitations.",
                select: "Select role",
            },
            cant_use: "**You cannot use this interaction!**\nYou can run the command on your own to use it."
        },
    },
    manager: {
        no_perms: emojis.error + "You do not have sufficient permissions to perform this action!",
        song_added: "Song **[%s%](%l%)** added to the queue! `%t%`",
        playlist_added: "**%s%** songs added to the queue!",
        nothing_playing: emojis.error + "Nothing is being reproduced!",
        cant_modify_queue: emojis.error + "You cannot modify the playback queue!",
        queue_end: emojis.success + "The playback queue has **finished**!",
        restart: {
            embed_author: "¡KenaBot is restarting!",
            embed_description: "We regret this interruption, but **KenaBot** will be rebooting to implement new features.\n**We'll be back in no time!**",
        },
        cant_do: emojis.error + "I can't do that here!",
        no_premium: emojis.error + "Only **donors** can use this command!\n[Click here to know more](https://kenabot.xyz/premium)",
        no_premium_twitch: emojis.error + "Only **donors** can play **Twitch** streams!\n[Click here to know more](https://kenabot.xyz/premium)",
        no_premium_stage: emojis.error + "Only **donors** can play music in **Stage Channels**!\n[Click here to know more](https://kenabot.xyz/premium)",
        blocked: "**This channel is blocked!**\nAn administrator of this server has blocked this channel to prevent the use of commands here.",
        blocked_command: "**This command is blocked!**\nAn administrator of this server has blocked this command to prevent its use globally.",
        no_db: "**No information!**\nWe don't seem to have anything stored in our databases about your server.",
        delete_confirm: "**Are you sure you want to delete this information?**\nAll server configuration will be lost.",
        delete_cancel: "**Cancelled!**\nThe action of deleting the information has been cancelled.",
        deleted: "**Deleted!**\nThe server information has been deleted.",
        dj_role_required: "**Only the DJ can execute this command!**\nIt seems that in a voice channel there is a DJ, only he can execute this command.",
        dj_role_required_usecmd: "**Only the DJ can execute this command!**\nIt seems that in a voice channel there is a DJ, only he can execute this command.\n\nYou can start a vote to skip the song using the `/skip` command.",
        is247: emojis.error + "You cant do that with the **24/7** mode enabled!",
        premium_forever: emojis.error + "It looks like this server is in our **Partner** program, so you can't delete its settings.",
        blocked_server: emojis.error+"This server is blocked by the developer!\nReason: `%s%`.",
    }
}