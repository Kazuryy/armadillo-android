package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.BuildConfig
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.UpdateChecker
import dev.kazuryy.armadillo.util.UpdateInfo
import dev.kazuryy.armadillo.util.UpdateInstaller
import dev.kazuryy.armadillo.util.releaseNotesSummary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Checks for an update and shows the banner if one is available. Independent of any account:
 * it only talks to GitHub's public releases API, so it works whether or not anyone is signed in.
 */
@Composable
fun UpdateSection() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isInstallingUpdate by remember { mutableStateOf(false) }
    var updateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        updateInfo = UpdateChecker().checkForUpdate(BuildConfig.VERSION_NAME)
    }

    updateInfo?.let { info ->
        UpdateBanner(
            updateInfo = info,
            isInstalling = isInstallingUpdate,
            errorMessage = updateError,
            onInstallClick = {
                isInstallingUpdate = true
                updateError = null
                scope.launch {
                    val installer = UpdateInstaller()
                    val unknownSourcesIntent = installer.unknownSourcesIntentIfNeeded(context)
                    if (unknownSourcesIntent != null) {
                        context.startActivity(unknownSourcesIntent)
                        isInstallingUpdate = false
                        return@launch
                    }
                    try {
                        val apkFile = installer.downloadApk(context, info)
                        context.startActivity(installer.installIntent(context, apkFile))
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        updateError = e.message ?: "Update failed"
                    } finally {
                        isInstallingUpdate = false
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun UpdateBanner(
    updateInfo: UpdateInfo,
    isInstalling: Boolean,
    errorMessage: String? = null,
    onInstallClick: () -> Unit
) {
    val notes = remember(updateInfo.changelog) { releaseNotesSummary(updateInfo.changelog) }

    Column(
        modifier = Modifier
            .background(BrandOrange.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Version ${updateInfo.versionName} available", fontSize = 14.sp, color = Color.White)
            errorMessage?.let { Text(text = it, fontSize = 14.sp, color = SecondaryText) }
            Button(
                onClick = onInstallClick,
                enabled = !isInstalling,
                colors = ButtonDefaults.colors(containerColor = BrandOrange, contentColor = Color.Black)
            ) {
                Text(if (isInstalling) "Downloading..." else "Update")
            }
        }
        notes?.let { Text(text = it, fontSize = 13.sp, color = SecondaryText) }
    }
}
