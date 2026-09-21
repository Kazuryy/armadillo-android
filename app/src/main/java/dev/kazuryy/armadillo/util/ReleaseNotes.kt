package dev.kazuryy.armadillo.util

private const val MAX_BULLETS = 4
private const val MAX_BULLET_CHARS = 110

private val LINK = Regex("\\[([^\\]]+)\\]\\([^)]*\\)")

/**
 * Short plain-text summary of a GitHub Release description for the update banner.
 * Only the part before the first "---" line is used (the release workflow puts the install and
 * checksum blocks after it). Headings are dropped, bullets are kept, markdown is stripped.
 * Returns null when there is nothing to show.
 */
internal fun releaseNotesSummary(body: String?): String? {
    if (body.isNullOrBlank()) return null

    val bullets = body.lines()
        .map { it.trim() }
        .takeWhile { it != "---" }
        .filter { it.startsWith("- ") || it.startsWith("* ") }
        .map { cleanMarkdown(it.drop(2)) }
        .filter { it.isNotEmpty() }

    if (bullets.isEmpty()) return null

    val shown = bullets.take(MAX_BULLETS).map { "• " + it.truncate(MAX_BULLET_CHARS) }
    val hidden = bullets.size - shown.size
    return (if (hidden > 0) shown + "…and $hidden more" else shown).joinToString("\n")
}

private fun cleanMarkdown(text: String): String =
    text.replace(LINK, "$1").replace("**", "").replace("`", "").trim()

private fun String.truncate(max: Int): String =
    if (length <= max) this else take(max - 1).trimEnd() + "…"
