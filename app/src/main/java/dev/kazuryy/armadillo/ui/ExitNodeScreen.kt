package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import dev.kazuryy.armadillo.util.SiteResource
import dev.kazuryy.armadillo.util.TunnelManager
import kotlinx.coroutines.launch

@Composable
fun ExitNodeScreen(tunnelManager: TunnelManager, onBack: () -> Unit) {
    val state by tunnelManager.exitNodeState.collectAsState()
    val tunnelState by tunnelManager.tunnelState.collectAsState()
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) { tunnelManager.refreshExitNodes() }
    LaunchedEffect(state.nodes.size) { runCatching { firstFocus.requestFocus() } }

    fun run(action: suspend () -> String?) {
        busy = true
        error = null
        scope.launch {
            error = action()
            busy = false
        }
    }

    TvScreen(
        title = "Exit node",
        subtitle = if (tunnelState.isFullyConnected) {
            "All traffic goes through the selected exit node."
        } else {
            "The choice is saved and applied the next time you connect."
        }
    ) {
        if (state.nodes.isEmpty()) {
            Text(
                text = "This organization has no exit node.",
                fontSize = 16.sp,
                color = SecondaryText
            )
        } else {
            ExitNodeRow(
                name = "None",
                detail = "Only private resources go through the tunnel",
                active = state.activeId == null,
                enabled = !busy,
                modifier = Modifier.focusRequester(firstFocus),
                onSelect = { run { tunnelManager.disableExitNode() } }
            )
            state.nodes.forEach { node ->
                Spacer(modifier = Modifier.height(12.dp))
                ExitNodeRow(
                    name = node.name,
                    detail = node.siteNames?.joinToString(", ").orEmpty(),
                    active = state.activeId == node.siteResourceId,
                    enabled = !busy,
                    onSelect = { run { tunnelManager.selectExitNode(node) } }
                )
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, fontSize = 14.sp, color = SecondaryText)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun ExitNodeRow(
    name: String,
    detail: String,
    active: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvRow {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            if (detail.isNotEmpty()) Text(text = detail, fontSize = 14.sp, color = SecondaryText)
        }
        Button(
            onClick = onSelect,
            enabled = enabled && !active,
            modifier = modifier,
            colors = if (active) {
                ButtonDefaults.colors(containerColor = BrandOrange, contentColor = Color.Black)
            } else {
                ButtonDefaults.colors()
            }
        ) { Text(if (active) "Active" else "Use") }
    }
}
