# Changelog

All notable changes to Armadillo (Android TV) are listed here. The section of a version is used as
the description of its GitHub Release, so it must exist before the `vX.Y.Z` tag is pushed.

## [Unreleased]

### Added
- Always-On VPN: Android can restart the tunnel after a reboot or when the app is killed. Nothing is restarted once the active account has been logged out.
- Exit nodes: a screen to route all traffic through an exit node of the organization, or none.
- Sites: a screen listing the sites of the tunnel with their connection type, address and last seen time.
- Settings: override DNS, tunnel DNS, exit node first, and tunnel logs.
- IPv6 addresses are accepted for the upstream DNS servers.
- The update banner shows the release notes of the new version before downloading it.
- Release descriptions are built from this file, with the SHA-256 of the APK and install steps.

### Changed
- Synchronized with the official Android client (fosrl/android 0.8.1): olm 1.10.1, newt 1.18.1, Go 1.26, and its tunnel service and connection logic.
- While Always-On owns the tunnel, switching or adding an account and logging out the active one are refused, and Disconnect explains how to turn Always-On off in the Android VPN settings.
- CI builds the debug and the unsigned release APK (to catch R8 problems) on every push to the development branches and on pull requests. A release tag now runs the unit tests first.

## [0.1.2] - 2026-09-21

### Added
- Accounts screen: switch between signed-in accounts, add another one, or log one out.
- Unit tests, and a CI workflow that runs them and builds the debug APK on every push and pull request.

### Security
- Logging out now erases everything that could be used to act as that user: the session token, the device credentials (OLM id and secret), the account entry, the tunnel logs and the downloaded update. The tunnel is stopped first if it belongs to that account.
- Update downloads are verified against the SHA-256 published with the release, and are only accepted from this repository's GitHub releases over HTTPS.
- Response bodies (which included the device secret when a device was created) and OLM ids are no longer written to the logs.
- Removed unused permissions (draw over other apps, battery optimization exemption, boot completed) and disabled app backup.
- The update file provider only exposes the download folder instead of the whole app storage.

### Changed
- Adding an account stops the tunnel of the previously active account.

### Fixed
- Errors while registering the device or storing its credentials are now shown when connecting, instead of a generic message.
- A failed update download shows an error in the banner instead of crashing the app.

## [0.1.1] - 2026-09-15

### Fixed
- DNS override default, and the disconnect error reason is now preserved.
- Release builds: use the runner's preinstalled Android SDK.

## [0.1.0] - 2026-08-28

### Added
- Android TV and Google TV client for Pangolin, built with Compose for TV and D-pad navigation.
- Sign in with Pangolin Cloud or a self-hosted instance, using a device code and QR code.
- WireGuard tunnel through Pangolin's olm core, with the VPN permission requested before connecting.
- Signed APK releases on GitHub with in-app update checks.
- Brand identity aligned with armadillo-apple.

[Unreleased]: https://github.com/Kazuryy/armadillo-android/compare/v0.1.2...HEAD
[0.1.2]: https://github.com/Kazuryy/armadillo-android/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/Kazuryy/armadillo-android/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/Kazuryy/armadillo-android/releases/tag/v0.1.0
