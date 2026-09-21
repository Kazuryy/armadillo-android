package dev.kazuryy.armadillo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretKeysTest {

    private val keys = listOf(
        "session-token-userA",
        "olm-id-userA",
        "olm-secret-userA",
        "session-token-userB",
        "olm-id-userB",
        "olm-secret-userB",
        "some-other-key"
    )

    @Test
    fun selectsEverythingStoredForOneUser() {
        assertEquals(
            listOf("session-token-userA", "olm-id-userA", "olm-secret-userA"),
            userSecretKeys(keys, "userA")
        )
    }

    @Test
    fun leavesOtherAccountsUntouched() {
        val selected = userSecretKeys(keys, "userA")
        assertTrue(selected.none { it.endsWith("userB") })
        assertTrue("some-other-key" !in selected)
    }

    @Test
    fun coversSecretsAddedLater() {
        val withNewKey = keys + "refresh-token-userA"
        assertTrue("refresh-token-userA" in userSecretKeys(withNewKey, "userA"))
    }

    @Test
    fun blankUserIdSelectsNothing() {
        assertTrue(userSecretKeys(keys, "").isEmpty())
        assertTrue(userSecretKeys(keys, "  ").isEmpty())
    }

    @Test
    fun unknownUserSelectsNothing() {
        assertTrue(userSecretKeys(keys, "userC").isEmpty())
    }
}
