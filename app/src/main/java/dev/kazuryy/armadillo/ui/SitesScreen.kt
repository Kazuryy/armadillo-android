package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.PeerDetails
import dev.kazuryy.armadillo.util.TunnelManager
import dev.kazuryy.armadillo.util.gatewayLabel
import dev.kazuryy.armadillo.util.peerDetails
import dev.kazuryy.armadillo.util.relativeTime

@Composable
fun SitesScreen(tunnelManager: TunnelManager, onBack: () -> Unit) {
    val status by tunnelManager.connectionStatus.collectAsState()
    val tunnelState by tunnelManager.tunnelState.collectAsState()
    val exitNodes by tunnelManager.exitNodeState.collectAsState()
    val backFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { backFocus.requestFocus() } }

    val current = status
    val sites: List<PeerDetails> = if (tunnelState.isServiceRunning && current != null) {
        current.peers.orEmpty().keys.sorted().mapNotNull { peerDetails(current, it) }
    } else {
        emptyList()
    }

    TvScreen(
        title = "Sites",
        subtitle = current?.takeIf { tunnelState.isServiceRunning }
            ?.let { "Exit node: " + gatewayLabel(it, exitNodes.nodes) }
    ) {
        if (sites.isEmpty()) {
            Text(
                text = if (tunnelState.isServiceRunning) "No site reported yet." else "Connect to see your sites.",
                fontSize = 16.sp,
                color = SecondaryText
            )
        } else {
            sites.forEachIndexed { index, site ->
                if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                SiteRow(site)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, modifier = Modifier.focusRequester(backFocus)) { Text("Back") }
    }
}

@Composable
private fun SiteRow(site: PeerDetails) {
    TvRow {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (site.connected) BrandOrange else SecondaryText, CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = site.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            val detail = listOfNotNull(
                site.connection,
                site.endpoint,
                "seen " + relativeTime(site.lastSeen)
            ).joinToString("  ·  ")
            Text(text = detail, fontSize = 14.sp, color = SecondaryText)
        }
    }
}
