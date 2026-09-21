package dev.kazuryy.armadillo.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

private const val LATEST_RELEASE_URL = "https://api.github.com/repos/Kazuryy/armadillo-android/releases/latest"
private const val APK_ASSET_NAME = "armadillo-android.apk"
private const val CHECKSUM_ASSET_NAME = "armadillo-android.apk.sha256"

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val name: String? = null,
    val body: String? = null,
    val assets: List<GitHubReleaseAsset> = emptyList()
)

@Serializable
data class GitHubReleaseAsset(
    val name: String,
    val browser_download_url: String
)

data class UpdateInfo(
    val versionName: String,
    val changelog: String?,
    val downloadUrl: String,
    val checksumUrl: String
)

class UpdateChecker(private val client: OkHttpClient = UpdateHttpClient.create()) {
    private val tag = "UpdateChecker"

    suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? {
        val release = fetchLatestRelease() ?: return null
        val latestVersion = release.tag_name.removePrefix("v")

        if (!isNewerVersion(latestVersion, currentVersionName)) {
            return null
        }

        val apkAsset = release.assets.firstOrNull { it.name == APK_ASSET_NAME } ?: run {
            Log.w(tag, "Release $latestVersion has no $APK_ASSET_NAME asset")
            return null
        }
        // Releases without a published checksum are not offered: the installer refuses
        // to install an APK it cannot verify.
        val checksumAsset = release.assets.firstOrNull { it.name == CHECKSUM_ASSET_NAME } ?: run {
            Log.w(tag, "Release $latestVersion has no $CHECKSUM_ASSET_NAME asset")
            return null
        }
        if (!isTrustedDownloadUrl(apkAsset.browser_download_url) ||
            !isTrustedDownloadUrl(checksumAsset.browser_download_url)
        ) {
            Log.w(tag, "Release $latestVersion has untrusted download URLs, ignoring")
            return null
        }

        return UpdateInfo(
            versionName = latestVersion,
            changelog = release.body,
            downloadUrl = apkAsset.browser_download_url,
            checksumUrl = checksumAsset.browser_download_url
        )
    }

    private suspend fun fetchLatestRelease(): GitHubRelease? = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(LATEST_RELEASE_URL).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(tag, "Update check failed: HTTP ${response.code}")
                    return@withContext null
                }
                val bodyString = response.body?.string() ?: return@withContext null
                json.decodeFromString<GitHubRelease>(bodyString)
            }
        } catch (e: IOException) {
            Log.w(tag, "Update check network error: ${e.message}")
            null
        } catch (e: Exception) {
            Log.w(tag, "Update check parse error: ${e.message}")
            null
        }
    }
}
