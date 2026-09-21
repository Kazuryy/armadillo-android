package dev.kazuryy.armadillo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateSecurityTest {

    private val base = "https://github.com/Kazuryy/armadillo-android/releases/download/v0.2.0"

    @Test
    fun trustedUrl_acceptsReleaseAssetsOfThisRepo() {
        assertTrue(isTrustedDownloadUrl("$base/armadillo-android.apk"))
        assertTrue(isTrustedDownloadUrl("$base/armadillo-android.apk.sha256"))
    }

    @Test
    fun trustedUrl_rejectsOtherSchemesHostsAndRepos() {
        assertFalse(isTrustedDownloadUrl("http://github.com/Kazuryy/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("https://evil.example/Kazuryy/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("https://github.com/Other/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("https://github.com/Kazuryy/armadillo-android/archive/main.zip"))
        assertFalse(isTrustedDownloadUrl("https://github.com.evil.example/Kazuryy/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("https://user@github.com/Kazuryy/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("https://github.com:8443/Kazuryy/armadillo-android/releases/download/v1/a.apk"))
        assertFalse(isTrustedDownloadUrl("not a url"))
        assertFalse(isTrustedDownloadUrl(""))
    }

    @Test
    fun parseSha256_readsSha256sumOutputAndBareDigest() {
        val digest = "a".repeat(64)
        assertEquals(digest, parseSha256("$digest  armadillo-android.apk\n"))
        assertEquals(digest, parseSha256("  $digest\n"))
        assertEquals(digest, parseSha256("A".repeat(64)))
    }

    @Test
    fun parseSha256_rejectsMalformedInput() {
        assertNull(parseSha256(""))
        assertNull(parseSha256("abc"))
        assertNull(parseSha256("g".repeat(64)))
        assertNull(parseSha256("a".repeat(63)))
        assertNull(parseSha256("a".repeat(65)))
    }

    @Test
    fun isNewerVersion_comparesNumerically() {
        assertTrue(isNewerVersion("0.1.2", "0.1.1"))
        assertTrue(isNewerVersion("1.10.0", "1.9.2"))
        assertTrue(isNewerVersion("1.0.0.1", "1.0.0"))
        assertFalse(isNewerVersion("0.1.1", "0.1.1"))
        assertFalse(isNewerVersion("0.1.0", "0.1.1"))
        assertFalse(isNewerVersion("1.0", "1.0.0"))
    }
}
