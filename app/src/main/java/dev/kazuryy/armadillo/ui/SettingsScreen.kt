package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.Config
import dev.kazuryy.armadillo.util.ConfigManager

@Composable
fun SettingsScreen(configManager: ConfigManager, onBack: () -> Unit) {
    val config by configManager.config.collectAsState()
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstFocus.requestFocus() } }

    // The values shown are the ones the tunnel uses when they were never set
    val overrideDns = config.dnsOverrideEnabled ?: true
    val tunnelDns = config.dnsTunnelEnabled ?: false
    val exitFirst = config.exitNodeTakesPrecedence ?: false
    val collectLogs = config.logCollectionEnabled ?: false

    TvScreen(
        title = "Settings",
        subtitle = "Changes apply the next time you connect."
    ) {
        SettingRow(
            title = "Override DNS",
            description = "Resolve names with Pangolin's DNS so private resources work.",
            enabled = overrideDns,
            modifier = Modifier.focusRequester(firstFocus),
            onToggle = { configManager.updateConfig { c: Config -> c.copy(dnsOverrideEnabled = !overrideDns) } }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingRow(
            title = "Tunnel DNS",
            description = "Send DNS queries for your resources through the tunnel.",
            enabled = tunnelDns,
            onToggle = { configManager.updateConfig { c: Config -> c.copy(dnsTunnelEnabled = !tunnelDns) } }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingRow(
            title = "Exit node first",
            description = "With an exit node selected, send all traffic to it instead of to individual resources.",
            enabled = exitFirst,
            onToggle = { configManager.updateConfig { c: Config -> c.copy(exitNodeTakesPrecedence = !exitFirst) } }
        )
        Spacer(modifier = Modifier.height(12.dp))
        SettingRow(
            title = "Keep tunnel logs",
            description = "Write the tunnel log to a file on this TV. It is erased when you log out.",
            enabled = collectLogs,
            onToggle = { configManager.updateConfig { c: Config -> c.copy(logCollectionEnabled = !collectLogs) } }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Always-On VPN is managed by Android, in the system VPN settings when your TV offers them.",
            fontSize = 14.sp,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvRow {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = description, fontSize = 14.sp, color = SecondaryText)
        }
        Button(
            onClick = onToggle,
            modifier = modifier,
            colors = if (enabled) {
                ButtonDefaults.colors(containerColor = BrandOrange, contentColor = Color.Black)
            } else {
                ButtonDefaults.colors()
            }
        ) { Text(if (enabled) "On" else "Off") }
    }
}
