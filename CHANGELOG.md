# Changelog

## [2.1.0+1.19.4](https://github.com/axieum/minecord/compare/v2.0.3+1.19.4...v2.1.0+1.19.4) (2024-05-10)


### Features

* **chat:** add `entries[].discord.purgeLinks` option to prevent links from being sent to Discord ([#135](https://github.com/axieum/minecord/issues/135)) ([b85235a](https://github.com/axieum/minecord/commit/b85235a76755a781940885110d16e9edae639346))

## [2.0.3+1.19.4](https://github.com/axieum/minecord/compare/v2.0.2+1.19.4...v2.0.3+1.19.4) (2024-03-04)


### Bug Fixes

* **chat:** Minecraft events were not being forwarded to Discord forum threads (fixes [#121](https://github.com/axieum/minecord/issues/121)) ([#125](https://github.com/axieum/minecord/issues/125)) ([bb07ad2](https://github.com/axieum/minecord/commit/bb07ad297984f1febde8a547db1316d7dd428c4c))

## [2.0.2+1.19.4](https://github.com/axieum/minecord/compare/v2.0.1+1.19.4...v2.0.2+1.19.4) (2023-10-19)


### Bug Fixes

* **chat:** do not check player count during server start-up on Discord messages (fixes [#84](https://github.com/axieum/minecord/issues/84)) ([#108](https://github.com/axieum/minecord/issues/108)) ([0cfb220](https://github.com/axieum/minecord/commit/0cfb220f05a069de9d12732b3a668173be5cc0d8))
* **cmds:** option choices were incorrectly serializing to disk (fixes [#83](https://github.com/axieum/minecord/issues/83)) ([#110](https://github.com/axieum/minecord/issues/110)) ([9e560cf](https://github.com/axieum/minecord/commit/9e560cfc3952cb4422c60870eabee0b89a24f2d8))

## [2.0.1+1.19.4](https://github.com/axieum/minecord/compare/v2.0.0+1.19.4...v2.0.1+1.19.4) (2023-06-10)


### Bug Fixes

* **api:** do not load already loaded modded translations (fixes [#78](https://github.com/axieum/minecord/issues/78)) ([#79](https://github.com/axieum/minecord/issues/79)) ([2aaf1ba](https://github.com/axieum/minecord/commit/2aaf1bad68c88924b79adf6057b8cdfa8fd6178d))

## [2.0.0+1.19.4](https://github.com/axieum/minecord/compare/v1.1.0-beta.3...v2.0.0+1.19.4) (2023-06-08)


### ⚠ BREAKING CHANGES

* use `Patbox/TextPlaceholderAPI` for templating ([#70](https://github.com/axieum/minecord/issues/70))

### Features

* add `samolego/FabricTailor` support for player avatars (closes [#68](https://github.com/axieum/minecord/issues/68)) ([#75](https://github.com/axieum/minecord/issues/75)) ([6a14aba](https://github.com/axieum/minecord/commit/6a14abafc15c69d2cf650654858d9bf7e57f5877))
* add player avatars to certain messages (closes [#31](https://github.com/axieum/minecord/issues/31)) ([#62](https://github.com/axieum/minecord/issues/62)) ([331e6d8](https://github.com/axieum/minecord/commit/331e6d839dd1d60424d9d13976fd9c525bc32541))
* add support for Bedrock player avatars in embeds (closes [#71](https://github.com/axieum/minecord/issues/71)) ([#72](https://github.com/axieum/minecord/issues/72)) ([af69840](https://github.com/axieum/minecord/commit/af69840f2c2fa7e568459aa6063ee716a719a04f))
* **chat:** send Discord stickers into Minecraft (closes [#74](https://github.com/axieum/minecord/issues/74)) ([#76](https://github.com/axieum/minecord/issues/76)) ([8c8ca3e](https://github.com/axieum/minecord/commit/8c8ca3ef30511260c7b319d71017ed7ad5584546))
* **chat:** separate advancement config into task, goal and challenge ([a570d0a](https://github.com/axieum/minecord/commit/a570d0a2fde10a012224c8cab16b7448b2967a1d))
* **chat:** show player avatar on login, logout, death and teleport messages ([331e6d8](https://github.com/axieum/minecord/commit/331e6d839dd1d60424d9d13976fd9c525bc32541))
* **cmds:** show avatar in custom command feedback where a player is detected ([331e6d8](https://github.com/axieum/minecord/commit/331e6d839dd1d60424d9d13976fd9c525bc32541))
* upgrade Minecraft (1.19 -&gt; 1.19.2) ([#69](https://github.com/axieum/minecord/issues/69)) ([c351b26](https://github.com/axieum/minecord/commit/c351b2682cf67e6c02901643e052960f0a5856bd))
* use `Patbox/TextPlaceholderAPI` for templating ([#70](https://github.com/axieum/minecord/issues/70)) ([a570d0a](https://github.com/axieum/minecord/commit/a570d0a2fde10a012224c8cab16b7448b2967a1d))


### Bug Fixes

* **api:** regex characters in `StringTemplate` variables should be escaped ([#65](https://github.com/axieum/minecord/issues/65)) ([4553977](https://github.com/axieum/minecord/commit/45539770ccdd15164d481a0132c6f01db467823a))
* mixins that may now be remapped ([f17a48d](https://github.com/axieum/minecord/commit/f17a48d816a617ae37fc05159835527f2541f537))


### Miscellaneous Chores

* release as v2.0.0+1.19.4 ([d3d9bfc](https://github.com/axieum/minecord/commit/d3d9bfc1c030ee7da967adc23b02bc5da980c690))

## [1.1.0-beta.3](https://github.com/axieum/minecord/compare/v1.0.0-beta.3...v1.1.0-beta.3) (2022-08-22)


### Features

* **api:** add 'i18n' config for translations ([#48](https://github.com/axieum/minecord/issues/48)) ([64b95c0](https://github.com/axieum/minecord/commit/64b95c018cf041392e96c2cbde111df5e34ae1e0))
* **api:** add `String` formatting to string templates ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))
* **chat:** add `/me`, `/say` and `/tellraw` command messages (closes [#51](https://github.com/axieum/minecord/issues/51)) ([#53](https://github.com/axieum/minecord/issues/53)) ([7eea724](https://github.com/axieum/minecord/commit/7eea724576532bdedc601590cb7a1c1609e0a8b1))
* **chat:** add Discord reply messages (closes [#50](https://github.com/axieum/minecord/issues/50)) ([#57](https://github.com/axieum/minecord/issues/57)) ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))
* **chat:** use new `fabric-message-api` events (closes [#32](https://github.com/axieum/minecord/issues/32)) ([#52](https://github.com/axieum/minecord/issues/52)) ([1859c69](https://github.com/axieum/minecord/commit/1859c695c02640e945fb7786b0a3f774d2de5cb7))


### Bug Fixes

* **api:** do not attempt to remap the language mixin ([791775b](https://github.com/axieum/minecord/commit/791775b14dc1565b636e45bf164905aac36de948))
* **chat:** correct `teleport` message placeholders in config ([7eea724](https://github.com/axieum/minecord/commit/7eea724576532bdedc601590cb7a1c1609e0a8b1))
* **chat:** correct Discord message placeholders in config ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))
* **cmds:** commands that don't have an output should receive a default message (fixes [#54](https://github.com/axieum/minecord/issues/54)) ([162f30f](https://github.com/axieum/minecord/commit/162f30f95c79882a6137089bb1f545a9ad80d786))
* **cmds:** commands with multiple lines of output should edit the original embed ([162f30f](https://github.com/axieum/minecord/commit/162f30f95c79882a6137089bb1f545a9ad80d786))
* modded translation keys should be resolved (fixes [#45](https://github.com/axieum/minecord/issues/45)) ([64b95c0](https://github.com/axieum/minecord/commit/64b95c018cf041392e96c2cbde111df5e34ae1e0))

## [1.0.0-beta.3](https://github.com/axieum/minecord/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2022-07-01)


### Bug Fixes

* `lang3` classes should not be relocated (fixes [#40](https://github.com/axieum/minecord/issues/40)) ([#42](https://github.com/axieum/minecord/issues/42)) ([9c107f3](https://github.com/axieum/minecord/commit/9c107f32d5e5566a96ce2d8c05d2e9e8ff7ea0f5))
* **chat:** unused `java-diff-utils` classes should remain ([9c107f3](https://github.com/axieum/minecord/commit/9c107f32d5e5566a96ce2d8c05d2e9e8ff7ea0f5))


### Miscellaneous Chores

* force release as v1.0.0-beta.3 ([b36b83a](https://github.com/axieum/minecord/commit/b36b83a64e8d5d78b27d58dab932d55f7937e1f8))

## [1.0.0-beta.2](https://github.com/axieum/minecord/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2022-06-30)


### Bug Fixes

* **api:** unused JDA classes should remain (fixes [#37](https://github.com/axieum/minecord/issues/37)) ([#38](https://github.com/axieum/minecord/issues/38)) ([35c6599](https://github.com/axieum/minecord/commit/35c6599ecb299639eae41cad2a0eb62086dc2b22))


### Miscellaneous Chores

* force release as v1.0.0-beta.2 ([01a60f0](https://github.com/axieum/minecord/commit/01a60f027e376acc5baa098f80188426487e9dc4))

## [1.0.0-beta.1](https://github.com/axieum/minecord/compare/v1.0.0-alpha.1...v1.0.0-beta.1) (2022-06-11)


### Features

* initial beta ([#24](https://github.com/axieum/minecord/issues/24)) ([d053f57](https://github.com/axieum/minecord/commit/d053f579fd80b90b2d954f86f1611bc92d63ce7d))


### Miscellaneous Chores

* force release as v1.0.0-beta.1 ([81e23ff](https://github.com/axieum/minecord/commit/81e23ff11d404b1acf4073628320d82200de583c))
