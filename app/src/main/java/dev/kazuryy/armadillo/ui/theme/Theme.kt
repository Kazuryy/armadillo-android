package dev.kazuryy.armadillo.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val ArmadilloColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark
)

@Composable
fun ArmadilloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArmadilloColorScheme,
        typography = Typography,
        content = content
    )
}
