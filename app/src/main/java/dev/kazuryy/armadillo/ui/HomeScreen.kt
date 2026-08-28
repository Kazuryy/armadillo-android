package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.BuildConfig
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.CardBackground
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.AuthManager
import dev.kazuryy.armadillo.util.TunnelManager
import dev.kazuryy.armadillo.util.UpdateChecker
import dev.kazuryy.armadillo.util.UpdateInfo
import dev.kazuryy.armadillo.util.UpdateInstaller
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(authManager: AuthManager, tunnelManager: TunnelManager, onConnectRequested: () -> Unit) {
    val tunnelState by tunnelManager.tunnelState.collectAsState()
    val currentUser by authManager.currentUser.collectAsState()
    val currentOrg by authManager.currentOrg.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val connectButtonFocusRequester = remember { FocusRequester() }

    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isInstallingUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        connectButtonFocusRequester.requestFocus()
        updateInfo = UpdateChecker().checkForUpdate(BuildConfig.VERSION_NAME)
    }

    ArmadilloBackground(isActive = tunnelState.isFullyConnected) {
        Column(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            updateInfo?.let { info ->
                UpdateBanner(
                    updateInfo = info,
                    isInstalling = isInstallingUpdate,
                    onInstallClick = {
                        isInstallingUpdate = true
                        scope.launch {
                            val installer = UpdateInstaller()
                            val unknownSourcesIntent = installer.unknownSourcesIntentIfNeeded(context)
                            if (unknownSourcesIntent != null) {
                                context.startActivity(unknownSourcesIntent)
                                isInstallingUpdate = false
                                return@launch
                            }
                            try {
                                val apkFile = installer.downloadApk(context, info.downloadUrl)
                                context.startActivity(installer.installIntent(context, apkFile))
                            } finally {
                                isInstallingUpdate = false
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(text = "Armadillo", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                currentUser?.let { Text(text = it.email, fontSize = 15.sp, color = SecondaryText) }
                currentOrg?.let {
                    Text(text = "  ·  ${it.name}", fontSize = 15.sp, color = SecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(CardBackground, RoundedCornerShape(20.dp))
                    .padding(horizontal = 48.dp, vertical = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotColor = if (tunnelState.isFullyConnected) BrandOrange else SecondaryText
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.size(10.dp).background(dotColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = tunnelState.statusMessage, fontSize = 20.sp)
                }
                tunnelState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, fontSize = 14.sp, color = SecondaryText)
                }

                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    enabled = tunnelState.canEnable || tunnelState.canDisable,
                    modifier = Modifier.focusRequester(connectButtonFocusRequester),
                    onClick = {
                        if (tunnelState.isFullyConnected || tunnelState.canDisable) {
                            scope.launch { tunnelManager.disconnect() }
                        } else {
                            onConnectRequested()
                        }
                    },
                    colors = ButtonDefaults.colors(
                        containerColor = BrandOrange,
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (tunnelState.isFullyConnected) "Disconnect" else "Connect")
                }
            }
        }
    }
}
