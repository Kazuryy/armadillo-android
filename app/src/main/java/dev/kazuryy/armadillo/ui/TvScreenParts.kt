package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.CardBackground
import dev.kazuryy.armadillo.ui.theme.SecondaryText

/** Common layout of the secondary screens: a title, a short subtitle and a scrollable body. */
@Composable
internal fun TvScreen(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    ArmadilloBackground(isActive = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            subtitle?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, fontSize = 15.sp, color = SecondaryText)
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

/** A card-shaped row, 720dp wide like the account rows. */
@Composable
internal fun TvRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .width(720.dp)
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}
