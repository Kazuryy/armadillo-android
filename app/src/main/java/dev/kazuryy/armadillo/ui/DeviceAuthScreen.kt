package dev.kazuryy.armadillo.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.CardBackground
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.AuthManager
import dev.kazuryy.armadillo.util.generateQrCodeBitmap
import kotlinx.coroutines.launch

@Composable
fun DeviceAuthScreen(authManager: AuthManager) {
    val code by authManager.deviceAuthCode.collectAsState()
    val loginURL by authManager.deviceAuthLoginURL.collectAsState()
    val inProgress by authManager.isDeviceAuthInProgress.collectAsState()
    val errorMessage by authManager.errorMessage.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authManager.loginWithDeviceAuth()
    }

    ArmadilloBackground(isActive = code != null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Armadillo",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )
            if (code == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "For Pangolin, the zero trust remote access platform.",
                    fontSize = 16.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .background(CardBackground, RoundedCornerShape(20.dp))
                    .padding(32.dp),
                verticalAlignment = Alignment.Top
            ) {
                val url = loginURL
                if (url != null) {
                    val qrBitmap = remember(url) { generateQrCodeBitmap(url) }
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR code to $url",
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Spinner(color = BrandOrange, size = 32.dp)
                    }
                }

                Spacer(modifier = Modifier.width(48.dp))

                Column(
                    modifier = Modifier.width(420.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Scan to Sign In",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Point your phone's camera at the code, or open the address below and type in the code shown.",
                        fontSize = 15.sp,
                        color = SecondaryText
                    )
                    loginURL?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = it,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SecondaryText
                        )
                    }
                    code?.let {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = it,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 3.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .background(BrandOrange, CircleShape)
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (code != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spinner(color = BrandOrange, size = 18.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Waiting for confirmation…", fontSize = 15.sp, color = SecondaryText)
                    }
                } else if (inProgress) {
                    Text(text = "Requesting device code...", fontSize = 15.sp, color = SecondaryText)
                } else {
                    Spacer(modifier = Modifier)
                }

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        authManager.cancelDeviceAuth()
                        scope.launch { authManager.loginWithDeviceAuth() }
                    },
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = SecondaryText
                    )
                ) {
                    Text("Get a new code")
                }
            }
        }
    }
}

@Composable
private fun Spinner(color: Color, size: androidx.compose.ui.unit.Dp) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )
    Canvas(modifier = Modifier.size(size)) {
        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = 270f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.toPx() * 0.12f)
        )
    }
}
