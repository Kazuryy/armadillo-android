package dev.kazuryy.armadillo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesTest {

    private val body = """
        ### Added
        - Accounts screen to switch, add and log out.

        ### Security
        - Updates are verified with a **SHA-256** checksum.
        - See [the docs](https://example.com) for `details`.

        ---

        ### Install

        - Download the APK.
    """.trimIndent()

    @Test
    fun keepsBulletsAndStripsMarkdown() {
        assertEquals(
            "• Accounts screen to switch, add and log out.\n" +
                "• Updates are verified with a SHA-256 checksum.\n" +
                "• See the docs for details.",
            releaseNotesSummary(body)
        )
    }

    @Test
    fun ignoresEverythingAfterTheSeparator() {
        assertTrue("Download the APK" !in releaseNotesSummary(body)!!)
    }

    @Test
    fun limitsTheNumberOfBullets() {
        val many = (1..7).joinToString("\n") { "- Change $it" }
        val lines = releaseNotesSummary(many)!!.lines()
        assertEquals(5, lines.size)
        assertEquals("…and 3 more", lines.last())
    }

    @Test
    fun truncatesLongBullets() {
        val summary = releaseNotesSummary("- " + "word ".repeat(60))!!
        assertTrue(summary.length <= 2 + 110)
        assertTrue(summary.endsWith("…"))
    }

    @Test
    fun nothingToShowGivesNull() {
        assertNull(releaseNotesSummary(null))
        assertNull(releaseNotesSummary("  "))
        assertNull(releaseNotesSummary("**Full Changelog**: https://example.com/compare/a...b"))
    }
}
