package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.util.UpdateInfo

@Composable
fun UpdateBanner(
    updateInfo: UpdateInfo,
    isInstalling: Boolean,
    onInstallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(BrandOrange.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Version ${updateInfo.versionName} available", fontSize = 14.sp, color = Color.White)
        Button(
            onClick = onInstallClick,
            enabled = !isInstalling,
            colors = ButtonDefaults.colors(containerColor = BrandOrange, contentColor = Color.Black)
        ) {
            Text(if (isInstalling) "Downloading..." else "Update")
        }
    }
}
