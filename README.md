# Armadillo (Android TV)

An independent Android TV / Google TV client for [Pangolin](https://github.com/fosrl/pangolin), the open-source zero-trust remote access platform.

This repo covers Android TV. Other Armadillo clients live in their own repos, see [armadillo-apple](https://github.com/Kazuryy/armadillo-apple) for tvOS.

> **Armadillo is an independent, community-built project.** It is not affiliated with, endorsed by, or sponsored by Fossorial, Inc., the makers of Pangolin. It exists as a stopgap: once Fossorial ships an official Android TV client, this project will likely be retired in its favor.

## Status

Research/scaffolding stage, no app code yet. Android TV was chosen over Android mobile because Fossorial already publishes an [official Android client](https://github.com/fosrl/android) for phones/tablets; Android TV has no official client yet (per [fosrl discussion #3039](https://github.com/orgs/fosrl/discussions/3039), it's planned but not scheduled).

Technical feasibility is confirmed: Android exposes `VpnService`, a system API that lets third-party apps establish a real VPN tunnel (the same mechanism NordVPN, ExpressVPN, and PIA use for their own Android TV apps), and WireGuard has an official embeddable Android tunnel library. This is unlike webOS, which has no equivalent API and was ruled out for a client of this kind.

## Planned stack

- Kotlin, Jetpack Compose for TV
- WireGuard tunnel via Pangolin's `olm` core (exact integration approach, direct Go bindings vs. the official WireGuard Android tunnel library, still to be decided)
- Play Store distribution

## License

AGPL-3.0 — see [LICENSE](LICENSE). If portions of this codebase end up derived from `fosrl/android`, they'll be used under that project's default AGPL-3 license grant, the same reasoning applied in [armadillo-apple](https://github.com/Kazuryy/armadillo-apple).
