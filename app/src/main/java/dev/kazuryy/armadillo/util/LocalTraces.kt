package dev.kazuryy.armadillo.util

import java.io.File

private val LOG_FILE_PREFIXES = listOf("armadillo.log", "pangolin.log")
private const val SOCKET_FILE_NAME = "armadillo.sock"

/**
 * Deletes local files that may name an account or its device (tunnel logs and their rotated
 * copies, crash log, control socket) and the downloaded update. The logs and socket are kept while
 * the tunnel is running, since the tunnel still writes to them. Returns the number of files deleted.
 */
internal fun clearLocalTraces(filesDir: File, cacheDir: File, tunnelRunning: Boolean): Int {
    var deleted = 0

    if (!tunnelRunning) {
        filesDir.listFiles()?.forEach { file ->
            val isTrace = file.isFile &&
                (file.name == SOCKET_FILE_NAME || LOG_FILE_PREFIXES.any { file.name.startsWith(it) })
            if (isTrace && file.delete()) deleted++
        }
    }

    val updates = File(cacheDir, "updates")
    if (updates.isDirectory) {
        updates.walkBottomUp().forEach { if (it.delete()) deleted++ }
    }

    return deleted
}
