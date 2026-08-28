# Armadillo (Android TV)

An independent Android TV / Google TV client for [Pangolin](https://github.com/fosrl/pangolin), the open-source zero-trust remote access platform.

This repo covers Android TV. Other Armadillo clients live in their own repos, see [armadillo-apple](https://github.com/Kazuryy/armadillo-apple) for tvOS.

> **Armadillo is an independent, community-built project.** It is not affiliated with, endorsed by, or sponsored by Fossorial, Inc., the makers of Pangolin. It exists as a stopgap: once Fossorial ships an official Android TV client, this project will likely be retired in its favor.

## Status

Initial scaffold in place, not yet built or run on a device/emulator. Android TV was chosen over Android mobile because Fossorial already publishes an [official Android client](https://github.com/fosrl/android) for phones/tablets; Android TV has no official client yet (per [fosrl discussion #3039](https://github.com/orgs/fosrl/discussions/3039), it's planned but not scheduled).

The tunnel core (`:tunnel` module) and business logic (`util/` package) are ported near-verbatim from `fosrl/android`, since that part is platform-agnostic and doesn't need reinventing. The UI is new, built for D-pad navigation with Compose for TV instead of the phone-oriented Activities/fragments the upstream app uses.

## Stack

- Kotlin, Jetpack Compose for TV (`androidx.tv:tv-material`)
- WireGuard tunnel via Pangolin's `olm` core, compiled to a native `.so` and driven through a `VpnService` backend (`:tunnel` module, ported from `fosrl/android`)
- Device-code + QR sign-in flow (ZXing for QR generation), reusing `AuthManager`'s existing device-auth polling logic
- Play Store distribution (planned)

## Building

Requires Android Studio or the Android SDK/NDK (`ndkVersion = 29.0.14206865` in `tunnel/build.gradle.kts`) plus a Go toolchain for the `:tunnel` module's native build. Not yet verified end-to-end; the project has not been built on a machine with the Android SDK installed.

```bash
./gradlew :tunnel:assembleDebug
./gradlew :app:assembleDebug
```

## Project origin

The `:tunnel` module and `util/` business logic package are derived from [fosrl/android](https://github.com/fosrl/android) (Pangolin's official Android client), renamespaced from `net.pangolin.Pangolin` to `dev.kazuryy.armadillo`. The UI layer is original, written for Android TV from scratch.

## License

AGPL-3.0, see [LICENSE](LICENSE). Portions of this codebase (the `:tunnel` module and `util/` package) are derived from `fosrl/android` and used under that project's default AGPL-3 license grant, the same reasoning applied in [armadillo-apple](https://github.com/Kazuryy/armadillo-apple).
