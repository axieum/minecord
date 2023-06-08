# Changelog

## [2.0.0+1.20](https://github.com/axieum/minecord/compare/api-v2.0.0+1.19.4...api-v2.0.0+1.20) (2023-06-08)


### deps

* upgrade to Minecraft 1.20 ([#73](https://github.com/axieum/minecord/issues/73)) ([3b392eb](https://github.com/axieum/minecord/commit/3b392eb8d7776ab3cc0384b62c1aeb50c90308a9))

## [2.0.0+1.19.4](https://github.com/axieum/minecord/compare/api-v1.1.0-beta.3...api-v2.0.0+1.19.4) (2023-06-08)


### ⚠ BREAKING CHANGES

* use `Patbox/TextPlaceholderAPI` for templating ([#70](https://github.com/axieum/minecord/issues/70))

### Features

* add `samolego/FabricTailor` support for player avatars (closes [#68](https://github.com/axieum/minecord/issues/68)) ([#75](https://github.com/axieum/minecord/issues/75)) ([6a14aba](https://github.com/axieum/minecord/commit/6a14abafc15c69d2cf650654858d9bf7e57f5877))
* add player avatars to certain messages (closes [#31](https://github.com/axieum/minecord/issues/31)) ([#62](https://github.com/axieum/minecord/issues/62)) ([331e6d8](https://github.com/axieum/minecord/commit/331e6d839dd1d60424d9d13976fd9c525bc32541))
* add support for Bedrock player avatars in embeds (closes [#71](https://github.com/axieum/minecord/issues/71)) ([#72](https://github.com/axieum/minecord/issues/72)) ([af69840](https://github.com/axieum/minecord/commit/af69840f2c2fa7e568459aa6063ee716a719a04f))
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

## [1.1.0-beta.3](https://github.com/axieum/minecord/compare/api-v1.0.0-beta.3...api-v1.1.0-beta.3) (2022-08-22)


### Features

* **api:** add 'i18n' config for translations ([#48](https://github.com/axieum/minecord/issues/48)) ([64b95c0](https://github.com/axieum/minecord/commit/64b95c018cf041392e96c2cbde111df5e34ae1e0))
* **api:** add `String` formatting to string templates ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))
* **chat:** add Discord reply messages (closes [#50](https://github.com/axieum/minecord/issues/50)) ([#57](https://github.com/axieum/minecord/issues/57)) ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))


### Bug Fixes

* **api:** do not attempt to remap the language mixin ([791775b](https://github.com/axieum/minecord/commit/791775b14dc1565b636e45bf164905aac36de948))
* **chat:** correct Discord message placeholders in config ([4baa059](https://github.com/axieum/minecord/commit/4baa05986911815d36129f59e9a538ef9c3fed0f))
* modded translation keys should be resolved (fixes [#45](https://github.com/axieum/minecord/issues/45)) ([64b95c0](https://github.com/axieum/minecord/commit/64b95c018cf041392e96c2cbde111df5e34ae1e0))

## [1.0.0-beta.3](https://github.com/axieum/minecord/compare/api-v1.0.0-beta.2...api-v1.0.0-beta.3) (2022-07-01)


### Bug Fixes

* `lang3` classes should not be relocated (fixes [#40](https://github.com/axieum/minecord/issues/40)) ([#42](https://github.com/axieum/minecord/issues/42)) ([9c107f3](https://github.com/axieum/minecord/commit/9c107f32d5e5566a96ce2d8c05d2e9e8ff7ea0f5))
* **chat:** unused `java-diff-utils` classes should remain ([9c107f3](https://github.com/axieum/minecord/commit/9c107f32d5e5566a96ce2d8c05d2e9e8ff7ea0f5))


### Miscellaneous Chores

* force release as v1.0.0-beta.3 ([b36b83a](https://github.com/axieum/minecord/commit/b36b83a64e8d5d78b27d58dab932d55f7937e1f8))

## [1.0.0-beta.2](https://github.com/axieum/minecord/compare/api-v1.0.0-beta.1...api-v1.0.0-beta.2) (2022-06-30)


### Bug Fixes

* **api:** unused JDA classes should remain (fixes [#37](https://github.com/axieum/minecord/issues/37)) ([#38](https://github.com/axieum/minecord/issues/38)) ([35c6599](https://github.com/axieum/minecord/commit/35c6599ecb299639eae41cad2a0eb62086dc2b22))


### Miscellaneous Chores

* force release as v1.0.0-beta.2 ([01a60f0](https://github.com/axieum/minecord/commit/01a60f027e376acc5baa098f80188426487e9dc4))

## [1.0.0-beta.1](https://github.com/axieum/minecord/compare/api-v1.0.0-alpha.1...api-v1.0.0-beta.1) (2022-06-11)


### Features

* initial beta ([#24](https://github.com/axieum/minecord/issues/24)) ([d053f57](https://github.com/axieum/minecord/commit/d053f579fd80b90b2d954f86f1611bc92d63ce7d))


### Miscellaneous Chores

* force release as v1.0.0-beta.1 ([81e23ff](https://github.com/axieum/minecord/commit/81e23ff11d404b1acf4073628320d82200de583c))
