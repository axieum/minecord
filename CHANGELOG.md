# Changelog

## [2.0.0-alpha.1](https://github.com/axieum/minecord/compare/v1.0.0-alpha.1...v2.0.0-alpha.1) (2022-06-09)


### ⚠ BREAKING CHANGES

* upgrade Minecraft (1.19-pre1 -> 1.19) (#20)
* upgrade JDA (4.4.0 -> 5.0.0-alpha.12) (#16)
* upgrade Minecraft (1.18.2 -> 1.19-pre1) (#15)
* use `googleapis/release-please` for releases
* upgrade Minecraft (1.18 -> 1.18.2) (#14)

### Features

* **api:** add custom entrypoint for use in addons ([4ae2c88](https://github.com/axieum/minecord/commit/4ae2c88b39106a18146376caffbccd281ecb4b15))
* **api:** add string template utility class ([865ed77](https://github.com/axieum/minecord/commit/865ed77fd4894eea294e5c5c7c3c89565242b80a))
* **api:** add string utility helpers ([17bf6a1](https://github.com/axieum/minecord/commit/17bf6a18ac6a4d2400c879e4238e5b0458819cb1))
* **api:** add support for lazy variables in string templates ([#18](https://github.com/axieum/minecord/issues/18)) ([1723d0d](https://github.com/axieum/minecord/commit/1723d0d4003e4f58fa6c44f84303405f53c504f8))
* **api:** expose the Minecraft server instance ([5be4a87](https://github.com/axieum/minecord/commit/5be4a879cae9019710c392b4022823904a3e82b0))
* **api:** init `minecord-api` module ([ab52ace](https://github.com/axieum/minecord/commit/ab52aceaf931ee0de0e4258d3eca8457b43e5020))
* **chat:** init `minecord-chat` module ([#5](https://github.com/axieum/minecord/issues/5)) ([f1a5703](https://github.com/axieum/minecord/commit/f1a5703910c5c70c5eb0f5d7d656620f791b55d0))
* **cmds:** add configurable command cooldowns ([#17](https://github.com/axieum/minecord/issues/17)) ([572ffbe](https://github.com/axieum/minecord/commit/572ffbe2a14cee560f5eedd5801bf32cde1f52e8))
* **cmds:** init `minecord-cmds` module ([#7](https://github.com/axieum/minecord/issues/7)) ([695bb1b](https://github.com/axieum/minecord/commit/695bb1b2e4f92e4c4b80be242156df57e7f7f736))
* **cmds:** remove command permissions from config ([572ffbe](https://github.com/axieum/minecord/commit/572ffbe2a14cee560f5eedd5801bf32cde1f52e8))
* **presence:** init `minecord-presence` module ([#8](https://github.com/axieum/minecord/issues/8)) ([fe62d75](https://github.com/axieum/minecord/commit/fe62d758ad465ac399b9ac33863955c4b5ba8370))
* upgrade JDA (4.4.0 -> 5.0.0-alpha.12) ([#16](https://github.com/axieum/minecord/issues/16)) ([ae2a8d4](https://github.com/axieum/minecord/commit/ae2a8d4daa5ef04a0dd40c4871cd4cd7b5d83f84))
* upgrade Minecraft (1.18 -> 1.18.2) ([#14](https://github.com/axieum/minecord/issues/14)) ([b38154c](https://github.com/axieum/minecord/commit/b38154cb04f7a6b814e4752bae0bda3cfceedb90))
* upgrade Minecraft (1.18.2 -> 1.19-pre1) ([#15](https://github.com/axieum/minecord/issues/15)) ([ac3acac](https://github.com/axieum/minecord/commit/ac3acac70d9800f3b83e2fd7d5449f8ff407eafb))
* upgrade Minecraft (1.19-pre1 -> 1.19) ([#20](https://github.com/axieum/minecord/issues/20)) ([c1778d9](https://github.com/axieum/minecord/commit/c1778d9af6fe2ea35137758fc8ecc70abd8c019b))
* upgrade to Minecraft 1.18 ([#13](https://github.com/axieum/minecord/issues/13)) ([fd016e2](https://github.com/axieum/minecord/commit/fd016e2dfccb40361b100ba517c34ee4b48e59b6))


### Bug Fixes

* **api:** JDA shutting down before addons handle server shutdown ([5be4a87](https://github.com/axieum/minecord/commit/5be4a879cae9019710c392b4022823904a3e82b0))
* **cmds:** custom commands should not fail when called from a private channel ([572ffbe](https://github.com/axieum/minecord/commit/572ffbe2a14cee560f5eedd5801bf32cde1f52e8))


### Performance Improvements

* **cmds:** reusable string template for the uptime command ([#19](https://github.com/axieum/minecord/issues/19)) ([4fb49d9](https://github.com/axieum/minecord/commit/4fb49d9970f934b6a5fcd29b3894d2ecc85dab02))


### Continuous Integration

* use `googleapis/release-please` for releases ([b38154c](https://github.com/axieum/minecord/commit/b38154cb04f7a6b814e4752bae0bda3cfceedb90))
