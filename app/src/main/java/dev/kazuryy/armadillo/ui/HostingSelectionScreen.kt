package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.SecondaryText

@Composable
fun HostingSelectionScreen(
    onCloudSelected: () -> Unit,
    onSelfHostedSelected: () -> Unit
) {
    val firstCardFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstCardFocusRequester.requestFocus()
    }

    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        HostingOptionCard(
            title = "Pangolin Cloud",
            subtitle = "app.pangolin.net",
            icon = HostingIcon.Cloud,
            onClick = onCloudSelected,
            modifier = Modifier.focusRequester(firstCardFocusRequester)
        )
        HostingOptionCard(
            title = "Self-Hosted",
            subtitle = "Enter your custom hostname",
            icon = HostingIcon.Server,
            onClick = onSelfHostedSelected
        )
    }
}

private enum class HostingIcon { Cloud, Server }

@Composable
private fun HostingOptionCard(
    title: String,
    subtitle: String,
    icon: HostingIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.width(320.dp),
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(16.dp)),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = Color.White.copy(alpha = 0.06f),
            focusedContainerColor = Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(BrandOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(30.dp)) {
                    when (icon) {
                        HostingIcon.Cloud -> drawCloud(BrandOrange)
                        HostingIcon.Server -> drawServer(BrandOrange)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 14.sp, color = SecondaryText)
        }
    }
}

/**
 * Drawn glyphs instead of Unicode symbol characters (the previous "☁︎" and "▤"): a TV's system
 * font may not carry those glyphs, which showed up as a tofu box or the wrong shape on some
 * devices. A Canvas draw always renders the same way regardless of font coverage.
 */
private fun DrawScope.drawCloud(color: Color) {
    val w = size.width
    val h = size.height
    val bodyTop = h * 0.32f
    val bodyBottom = h * 0.78f
    val bodyHeight = bodyBottom - bodyTop

    // Flattened base so the puffs read as one cloud instead of three separate circles
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.08f, bodyTop + bodyHeight * 0.35f),
        size = androidx.compose.ui.geometry.Size(w * 0.84f, bodyHeight * 0.65f),
        cornerRadius = CornerRadius(bodyHeight * 0.32f, bodyHeight * 0.32f)
    )
    drawCircle(color = color, radius = bodyHeight * 0.52f, center = Offset(w * 0.30f, bodyTop + bodyHeight * 0.42f))
    drawCircle(color = color, radius = bodyHeight * 0.68f, center = Offset(w * 0.56f, bodyTop + bodyHeight * 0.30f))
    drawCircle(color = color, radius = bodyHeight * 0.46f, center = Offset(w * 0.78f, bodyTop + bodyHeight * 0.48f))
}

private fun DrawScope.drawServer(color: Color) {
    val w = size.width
    val barHeight = size.height * 0.22f
    val gap = size.height * 0.12f
    val cornerRadius = CornerRadius(barHeight * 0.3f, barHeight * 0.3f)
    val dotRadius = barHeight * 0.16f

    var y = 0f
    repeat(3) {
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, y),
            size = androidx.compose.ui.geometry.Size(w, barHeight),
            cornerRadius = cornerRadius
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.35f),
            radius = dotRadius,
            center = Offset(w - barHeight / 2f, y + barHeight / 2f)
        )
        y += barHeight + gap
    }
}
