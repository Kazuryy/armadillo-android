package dev.kazuryy.armadillo.util

import java.net.URI

private const val TRUSTED_HOST = "github.com"
private const val TRUSTED_PATH_PREFIX = "/Kazuryy/armadillo-android/releases/download/"

private val SHA256_REGEX = Regex("^[0-9a-fA-F]{64}$")

/** Only release assets of this repository, served over HTTPS, may be downloaded as updates. */
internal fun isTrustedDownloadUrl(url: String): Boolean {
    val uri = try {
        URI(url)
    } catch (_: Exception) {
        return false
    }
    return uri.scheme == "https" &&
        uri.host == TRUSTED_HOST &&
        uri.port == -1 &&
        uri.rawUserInfo == null &&
        uri.path?.startsWith(TRUSTED_PATH_PREFIX) == true
}

/** Extracts the digest from `sha256sum` output ("<hex>  <filename>") or a bare digest. */
internal fun parseSha256(text: String): String? {
    val token = text.trim().split(Regex("\\s+")).firstOrNull() ?: return null
    return if (SHA256_REGEX.matches(token)) token.lowercase() else null
}

/** Compares two dot-separated version strings, e.g. "1.10.0" > "1.9.2". */
internal fun isNewerVersion(candidate: String, current: String): Boolean {
    val candidateParts = candidate.split(".").mapNotNull { it.toIntOrNull() }
    val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
    val length = maxOf(candidateParts.size, currentParts.size)
    for (i in 0 until length) {
        val c = candidateParts.getOrElse(i) { 0 }
        val cur = currentParts.getOrElse(i) { 0 }
        if (c != cur) return c > cur
    }
    return false
}
