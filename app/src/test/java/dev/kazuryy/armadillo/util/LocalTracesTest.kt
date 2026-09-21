package dev.kazuryy.armadillo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LocalTracesTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun setup(): Pair<File, File> {
        val files = tmp.newFolder("files")
        val cache = tmp.newFolder("cache")
        listOf("armadillo.log", "armadillo.log.1", "armadillo.log.2", "pangolin.log", "armadillo.sock")
            .forEach { File(files, it).writeText("x") }
        File(files, "unrelated.txt").writeText("keep")
        File(cache, "updates").mkdirs()
        File(cache, "updates/armadillo-update.apk").writeText("apk")
        File(cache, "other.tmp").writeText("keep")
        return files to cache
    }

    @Test
    fun removesLogsRotatedLogsSocketAndDownloadedUpdate() {
        val (files, cache) = setup()

        clearLocalTraces(files, cache, tunnelRunning = false)

        assertEquals(listOf("unrelated.txt"), files.list()!!.toList())
        assertFalse(File(cache, "updates").exists())
        assertTrue(File(cache, "other.tmp").exists())
    }

    @Test
    fun keepsLogsAndSocketWhileTheTunnelStillWritesToThem() {
        val (files, cache) = setup()

        clearLocalTraces(files, cache, tunnelRunning = true)

        assertTrue(File(files, "armadillo.log").exists())
        assertTrue(File(files, "armadillo.sock").exists())
        // the downloaded update is not needed by the tunnel, so it goes either way
        assertFalse(File(cache, "updates").exists())
    }

    @Test
    fun neverTouchesTheAccountStoreDirectory() {
        val (files, cache) = setup()
        File(files, "Armadillo").mkdirs()
        File(files, "Armadillo/accounts.json").writeText("{}")

        clearLocalTraces(files, cache, tunnelRunning = false)

        assertTrue(File(files, "Armadillo/accounts.json").exists())
    }

    @Test
    fun missingDirectoriesAreFine() {
        val (files, cache) = setup()
        File(cache, "updates").deleteRecursively()

        assertEquals(5, clearLocalTraces(files, cache, tunnelRunning = false))
    }
}
