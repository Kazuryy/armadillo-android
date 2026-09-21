package dev.kazuryy.armadillo.util

import org.junit.Assert.assertEquals
import org.junit.Test

class HostnameTest {

    @Test
    fun addsHttpsWhenSchemeIsMissing() {
        assertEquals("https://pangolin.example.com", normalizeHostname("pangolin.example.com"))
    }

    @Test
    fun keepsExplicitScheme() {
        assertEquals("http://192.168.1.10:3000", normalizeHostname("http://192.168.1.10:3000"))
        assertEquals("https://pangolin.example.com", normalizeHostname("https://pangolin.example.com"))
    }

    @Test
    fun trimsWhitespaceAndTrailingSlashes() {
        assertEquals("https://pangolin.example.com", normalizeHostname("  pangolin.example.com//  "))
    }

    @Test
    fun blankInputStaysEmpty() {
        assertEquals("", normalizeHostname("   "))
    }
}
