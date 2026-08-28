# Armadillo (Android TV)

An independent Android TV / Google TV client for [Pangolin](https://github.com/fosrl/pangolin), the open-source zero-trust remote access platform.

This repo covers Android TV. Other Armadillo clients live in their own repos, see [armadillo-apple](https://github.com/Kazuryy/armadillo-apple) for tvOS.

> **Armadillo is an independent, community-built project.** It is not affiliated with, endorsed by, or sponsored by Fossorial, Inc., the makers of Pangolin. It exists as a stopgap: once Fossorial ships an official Android TV client, this project will likely be retired in its favor.

## Status

`./gradlew :tunnel:assembleDebug` and `:app:assembleDebug` both build successfully (verified on macOS/Apple Silicon). Not yet run on a device or emulator, and no automated tests exist yet. Android TV was chosen over Android mobile because Fossorial already publishes an [official Android client](https://github.com/fosrl/android) for phones/tablets; Android TV has no official client yet (per [fosrl discussion #3039](https://github.com/orgs/fosrl/discussions/3039), it's planned but not scheduled).

The tunnel core (`:tunnel` module) and business logic (`util/` package) are ported near-verbatim from `fosrl/android`, since that part is platform-agnostic and doesn't need reinventing. The UI is new, built for D-pad navigation with Compose for TV instead of the phone-oriented Activities/fragments the upstream app uses.

## Stack

- Kotlin, Jetpack Compose for TV (`androidx.tv:tv-material`)
- WireGuard tunnel via Pangolin's `olm` core, compiled to a native `.so` and driven through a `VpnService` backend (`:tunnel` module, ported from `fosrl/android`)
- Device-code + QR sign-in flow (ZXing for QR generation), reusing `AuthManager`'s existing device-auth polling logic
- Play Store distribution (planned)

## Building

Requires Android Studio (or the Android SDK/cmdline-tools) with:
- SDK Platform 36, Build-Tools 36.0.0, Platform-Tools
- NDK `29.0.14206865` and CMake `3.22.1` (exact versions pinned in `tunnel/build.gradle.kts`)

The `:tunnel` module's native build (`tunnel/tools/libpangolin-go/Makefile`) downloads its own pinned Go toolchain and expects a Linux-style userland. On macOS you need two GNU-compatible tools ahead of the BSD ones on `PATH`:
- `flock`: not present on macOS by default, `brew install flock`
- `sha256sum`: macOS's `/sbin/sha256sum` doesn't support the `-c` piped-checksum syntax the Makefile uses. Put a shim ahead of it on `PATH`, e.g. `/opt/homebrew/bin/sha256sum` containing `exec shasum -a 256 "$@"`.

```bash
./gradlew :tunnel:assembleDebug
./gradlew :app:assembleDebug
```

## Project origin

The `:tunnel` module and `util/` business logic package are derived from [fosrl/android](https://github.com/fosrl/android) (Pangolin's official Android client), renamespaced from `net.pangolin.Pangolin` to `dev.kazuryy.armadillo`. The UI layer is original, written for Android TV from scratch.

## License

AGPL-3.0, see [LICENSE](LICENSE). Portions of this codebase (the `:tunnel` module and `util/` package) are derived from `fosrl/android` and used under that project's default AGPL-3 license grant, the same reasoning applied in [armadillo-apple](https://github.com/Kazuryy/armadillo-apple).
