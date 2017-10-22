# Kubidibot

Kubidibot is a simple bot that displays a configurable welcome message and broadcasts audio to the given YouTube channel (live).

## Why would you want to stream audio to YouTube?

Well, in our context, it allows us to broadcast a vocal message from Discord to all of our connected Minecraft clients.
The sound is received thanks to a Forge mod.

### How to use

1. Connect to a voice channel. 
2. Spawn the bot into your channel using the `#pop` command. 
3. Say what you want.
4. When you are done, use the `#drop` command.

## Commands

* Spawn the bot in the caller's voice channel: `#pop`
* Disconnect the bot from its voice channel: `#drop`
* Change the volume (e.g. 50%): `#volume 50`
* Display the current volume: `#volume`

## Configs

Everything is configurable:

* config/app.json: Contains the commands' prefix and the welcome message
* config/network.json: Contains the YouTube channel's credentials