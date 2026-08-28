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

class UpdateInstaller(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun downloadApk(context: Context, url: String): File = withContext(Dispatchers.IO) {
        val outFile = File(context.cacheDir, "armadillo-update.apk")
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "Download failed: HTTP ${response.code}" }
            val body = checkNotNull(response.body) { "Empty download response" }
            outFile.outputStream().use { output -> body.byteStream().copyTo(output) }
        }
        outFile
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
}
