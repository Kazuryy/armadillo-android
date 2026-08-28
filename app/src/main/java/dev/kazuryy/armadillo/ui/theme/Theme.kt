package dev.kazuryy.armadillo.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.darkColorScheme

private val ArmadilloColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = BackgroundDark
)

@Composable
fun ArmadilloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArmadilloColorScheme,
        typography = Typography
    ) {
        Surface(modifier = Modifier.fillMaxSize()) { content() }
    }
}
