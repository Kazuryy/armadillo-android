package dev.kazuryy.armadillo.util

/** Turns user input such as "pangolin.example.com/" into "https://pangolin.example.com". */
internal fun normalizeHostname(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ""
    var normalized = trimmed
    if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
        normalized = "https://$normalized"
    }
    return normalized.trimEnd('/')
}
