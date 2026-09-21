package dev.kazuryy.armadillo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

/** OkHttp client for update traffic: never follows HTTPS to HTTP redirects. */
object UpdateHttpClient {
    fun create(): OkHttpClient = OkHttpClient.Builder()
        .followSslRedirects(false)
        .build()
}

class UpdateVerificationException(message: String) : Exception(message)

class UpdateInstaller(private val client: OkHttpClient = UpdateHttpClient.create()) {

    /**
     * Downloads the APK of [info] and returns it only if its SHA-256 matches the checksum
     * published alongside the release. A mismatching file is deleted.
     */
    suspend fun downloadApk(context: Context, info: UpdateInfo): File = withContext(Dispatchers.IO) {
        if (!isTrustedDownloadUrl(info.downloadUrl) || !isTrustedDownloadUrl(info.checksumUrl)) {
            throw UpdateVerificationException("Untrusted update URL")
        }

        val expected = fetchExpectedChecksum(info.checksumUrl)

        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        val outFile = File(updatesDir, "armadillo-update.apk")
        val digest = MessageDigest.getInstance("SHA-256")

        try {
            client.newCall(Request.Builder().url(info.downloadUrl).build()).execute().use { response ->
                check(response.isSuccessful) { "Download failed: HTTP ${response.code}" }
                val body = checkNotNull(response.body) { "Empty download response" }
                outFile.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    val input = body.byteStream()
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        digest.update(buffer, 0, read)
                        output.write(buffer, 0, read)
                    }
                }
            }
            val actual = digest.digest().joinToString("") { "%02x".format(it) }
            if (actual != expected) {
                throw UpdateVerificationException("Checksum mismatch, update rejected")
            }
        } catch (e: Exception) {
            outFile.delete()
            throw e
        }
        outFile
    }

    private fun fetchExpectedChecksum(url: String): String {
        client.newCall(Request.Builder().url(url).build()).execute().use { response ->
            check(response.isSuccessful) { "Checksum download failed: HTTP ${response.code}" }
            val text = checkNotNull(response.body) { "Empty checksum response" }.source().let {
                it.request(MAX_CHECKSUM_BYTES)
                it.buffer.readUtf8(minOf(it.buffer.size, MAX_CHECKSUM_BYTES))
            }
            return parseSha256(text) ?: throw UpdateVerificationException("Malformed checksum file")
        }
    }

    /** Returns an Intent to request the "install unknown apps" permission, or null if already granted. */
    fun unknownSourcesIntentIfNeeded(context: Context): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null
        if (context.packageManager.canRequestPackageInstalls()) return null
        return Intent(
            android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
        )
    }

    fun installIntent(context: Context, apkFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private companion object {
        const val MAX_CHECKSUM_BYTES = 1024L
    }
}
