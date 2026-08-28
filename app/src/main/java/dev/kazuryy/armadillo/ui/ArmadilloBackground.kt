package dev.kazuryy.armadillo.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.kazuryy.armadillo.ui.theme.BackgroundDark
import dev.kazuryy.armadillo.ui.theme.BrandOrange

@Composable
fun ArmadilloBackground(isActive: Boolean = false, content: @Composable () -> Unit) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.10f else 0f,
        animationSpec = tween(600),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(Color(0xFF1F1F23), BackgroundDark))
            )
    ) {
        Box(
            modifier = Modifier
                .width(640.dp)
                .height(640.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-420).dp)
                .background(BrandOrange.copy(alpha = glowAlpha), CircleShape)
                .blur(160.dp)
        )
        content()
    }
}
