# Kubidibot

Kubidibot is a simple bot that displays a configurable welcome message and has an audio bridges.

## Audio Bridge

Kubidibot can connects into voice channels, the audio bridge is a server that sends the audio that the bot receives, to all clients connected to it.

## Commands

* Spawn the bot in the callers voice channel : #pop
* Disconnect the bot from its voice channel : #drop
* Change the volume (exemple, 50%) : #volume 50
* Displays the current volume : #volume

## Configs

Everything is configurable :

* config/app.json : Contains the command prefix and the welcome message
* config/network.json : Contains the audio bridge server port 