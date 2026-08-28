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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.util.AuthManager
import dev.kazuryy.armadillo.util.TunnelManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(authManager: AuthManager, tunnelManager: TunnelManager) {
    val tunnelState by tunnelManager.tunnelState.collectAsState()
    val currentUser by authManager.currentUser.collectAsState()
    val currentOrg by authManager.currentOrg.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Armadillo", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        currentUser?.let { Text(text = it.email, fontSize = 16.sp) }
        currentOrg?.let { Text(text = it.name, fontSize = 16.sp) }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = tunnelState.statusMessage, fontSize = 22.sp)
        tunnelState.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            enabled = tunnelState.canEnable || tunnelState.canDisable,
            onClick = {
                scope.launch {
                    if (tunnelState.isFullyConnected || tunnelState.canDisable) {
                        tunnelManager.disconnect()
                    } else {
                        tunnelManager.connect()
                    }
                }
            }
        ) {
            Text(if (tunnelState.isFullyConnected) "Disconnect" else "Connect")
        }
    }
}
