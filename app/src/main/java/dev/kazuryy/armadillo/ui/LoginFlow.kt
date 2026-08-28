package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.AuthManager
import kotlinx.coroutines.launch

private enum class HostingOption { CLOUD, SELF_HOSTED }

@Composable
fun LoginFlow(authManager: AuthManager) {
    var hostingOption by remember { mutableStateOf<HostingOption?>(null) }
    var selfHostedURL by remember { mutableStateOf("") }
    var isLoggingIn by remember { mutableStateOf(false) }
    val deviceAuthCode by authManager.deviceAuthCode.collectAsState()
    val scope = rememberCoroutineScope()

    fun performLogin(hostname: String) {
        isLoggingIn = true
        scope.launch {
            authManager.loginWithDeviceAuth(hostname)
            isLoggingIn = false
        }
    }

    ArmadilloBackground(isActive = deviceAuthCode != null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Armadillo", fontSize = 44.sp, fontWeight = FontWeight.Bold)
            if (hostingOption == null && deviceAuthCode == null && !isLoggingIn) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "For Pangolin, the zero trust remote access platform.",
                    fontSize = 16.sp,
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            when {
                deviceAuthCode != null -> DeviceAuthScreen(
                    authManager = authManager,
                    onCancel = {
                        authManager.cancelDeviceAuth()
                        hostingOption = null
                        isLoggingIn = false
                    }
                )

                isLoggingIn -> Text(text = "Generating login code...", fontSize = 18.sp, color = SecondaryText)

                hostingOption == HostingOption.SELF_HOSTED -> SelfHostedEntryScreen(
                    url = selfHostedURL,
                    onUrlChange = { selfHostedURL = it },
                    onContinue = { performLogin(normalizeHostname(selfHostedURL)) },
                    onBack = { hostingOption = null }
                )

                else -> HostingSelectionScreen(
                    onCloudSelected = {
                        hostingOption = HostingOption.CLOUD
                        performLogin("https://app.pangolin.net")
                    },
                    onSelfHostedSelected = { hostingOption = HostingOption.SELF_HOSTED }
                )
            }
        }
    }
}

private fun normalizeHostname(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ""
    var normalized = trimmed
    if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
        normalized = "https://$normalized"
    }
    return normalized.trimEnd('/')
}
