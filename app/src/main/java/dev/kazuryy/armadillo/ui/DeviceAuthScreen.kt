package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
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

    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 64.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val url = loginURL
        if (url != null) {
            val qrBitmap = remember(url) { generateQrCodeBitmap(url) }
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR code to $url",
                modifier = Modifier.size(220.dp)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Connect Armadillo to Pangolin",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Scan this code with your phone, or visit the URL below and enter the code shown.",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            code?.let {
                Text(
                    text = it,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (inProgress && code == null) {
                Text(text = "Requesting device code...", fontSize = 16.sp)
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                authManager.cancelDeviceAuth()
                scope.launch { authManager.loginWithDeviceAuth() }
            }) {
                Text("Get a new code")
            }
        }
    }
}
