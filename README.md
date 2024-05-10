<div align="center">

<img alt="Minecord Icon" src="src/main/resources/assets/minecord/icon.svg" width="128">

# Minecord

Bring your Minecraft world into your Discord guild

[![Build](https://img.shields.io/github/actions/workflow/status/axieum/minecord/release.yml?branch=main&style=for-the-badge)][ci:release]
[![Release](https://img.shields.io/github/v/release/axieum/minecord?style=for-the-badge&include_prereleases&sort=semver)][releases]
[![Available For](https://cf.way2muchnoise.eu/versions/Available%20For_502254_latest.svg?badge_style=for_the_badge)][curseforge]
[![Downloads](https://cf.way2muchnoise.eu/full_502254_downloads.svg?badge_style=for_the_badge)][curseforge:files]

</div>

Minecord is a **Minecraft mod** that brings your Minecraft world into your
Discord guild.

It is a **server-side** only mod, uses [JDA][jda] to interface with Discord, is 
built on the [Fabric][fabric] mod loader, and is available for modern versions
of [Minecraft][minecraft] Java Edition.

> [!TIP]
> You only need to add the one mod jar to your server, e.g. `minecord-2.0.3+1.20.6.jar`.

#### Integrations

  * [TextPlaceholderAPI][text-placeholder-api] by Patbox &mdash; for text
    placeholders
  * [TydiumCraft Skin API][tydiumcraft-api] by Tydium &mdash; for embed avatars

## Features

### 💬 Chat

[Chat](minecord-chat/README.md) is responsible for bridging chat-related events
between Minecraft and Discord.

* Multi-channel support
* Filter events to Minecraft dimensions
* Translate unicode emojis, e.g. 😃 -> `:smiley:`
* Show player avatars in embeds where appropriate
* Discord events relayed to Minecraft
  * A user sent a message
  * A user sent a message in reply to another
  * A user edited their recently sent message
  * A user reacted to a recent message
  * A user removed their reaction from a recent message
  * A user sent a message that contained stickers 
  * A user sent a message that contained attachments
* Minecraft events relayed to Discord
  * A player sent an in-game chat message
  * A player sent an in-game message via the `/me` command
  * An admin broadcast an in-game message via the `/say` command
  * An admin broadcast an in-game message to all players via the `/tellraw @a`
    command
  * A player had died
  * A named animal/monster (with name tag) had died
  * A player unlocked an advancement task
  * A player reached an advancement goal
  * A player completed an advancement challenge
  * A player teleported to another dimension
  * A player joined the game
  * A player left the game
  * The server began to start
  * The server started and is accepting connections
  * The server began to stop
  * The server stopped and is offline
  * The server stopped unexpectedly and is inaccessible
    * Optionally attach the crash report if available

### 🪄 Commands

[Commands](minecord-cmds/README.md) is responsible for providing various commands to interact with
your Minecraft server from Discord.

* Built-in commands ready to use
  * `/uptime` &mdash; shows for how long the server has been online
  * `/tps` &mdash; displays the Minecraft server's current ticks-per-second
* Configure your own slash commands to run Minecraft commands
  * For example, you could manage your server's whitelist via `/whitelist`
* Ability to apply cooldowns to commands
  * This can be configured on a per user, channel or guild basis
* Detects players in commands and places their avatar in the response
* Mod developers can register their own slash commands

### 😇 Presence

[Presence](minecord-presence/README.md) is responsible for updating the bot presence in Discord to show
more detailed statuses.

* Built-in presence categories ready to use
  * `starting` &mdash; used when the Minecraft server is starting
  * `running` &mdash; used when the Minecraft server is running
  * `stopping` &mdash; used when the Minecraft server is stopping
* Configure your own presences
  * Type of activity, i.e. competing, listening, playing, streaming, or watching
  * Name of activity (supports placeholder values)
* Mod developers can register and trigger their own presence categories

### 📦 API

[API](minecord-api/README.md) is a gateway into the Minecord mod. It equips developers with the
tools necessary to integrate their mods with Discord.

## Sponsors

We would like to extend our thanks to the following sponsors for supporting
Minecord development.

[<img alt="BisectHosting" src="https://www.bisecthosting.com/partners/custom-banners/b9fe4fbe-8cc4-42cc-a545-dfd1b46d20e6.webp" height="80">][bisecthosting]

## Contributing

Thank you for considering contributing to Minecord! Please see the
[Contribution Guidelines][contributing].

## Security Vulnerabilities

Please review the [Security Policy][security] on how to report security
vulnerabilities.

## Licence

Minecord is open-sourced software licenced under the [MIT licence][licence].

[bisecthosting]: https://bisecthosting.com/axieum
[ci:release]: https://github.com/axieum/minecord/actions/workflows/release.yml
[contributing]: .github/CONTRIBUTING.md
[curseforge]: https://curseforge.com/minecraft/mc-mods/minecord-for-discord
[curseforge:files]: https://curseforge.com/minecraft/mc-mods/minecord-for-discord/files
[fabric]: https://fabricmc.net/
[jda]: https://github.com/DV8FromTheWorld/JDA
[licence]: https://opensource.org/licenses/MIT
[minecraft]: https://minecraft.net/
[releases]: https://github.com/axieum/minecord/releases
[security]: .github/SECURITY.md
[text-placeholder-api]: https://github.com/Patbox/TextPlaceholderAPI
[tydiumcraft-api]: https://www.tydiumcraft.net/docs/skinapi
