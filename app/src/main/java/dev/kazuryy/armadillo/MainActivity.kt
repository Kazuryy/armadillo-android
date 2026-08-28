package dev.kazuryy.armadillo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.DeviceAuthScreen
import dev.kazuryy.armadillo.ui.HomeScreen
import dev.kazuryy.armadillo.ui.theme.ArmadilloTheme
import dev.kazuryy.armadillo.util.AccountManager
import dev.kazuryy.armadillo.util.AndroidFingerprintCollector
import dev.kazuryy.armadillo.util.APIClient
import dev.kazuryy.armadillo.util.AuthManager
import dev.kazuryy.armadillo.util.ConfigManager
import dev.kazuryy.armadillo.util.FingerprintManager
import dev.kazuryy.armadillo.util.SecretManager
import dev.kazuryy.armadillo.util.TunnelManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "0.1.0"
        } catch (e: Exception) {
            "0.1.0"
        }

        val secretManager = SecretManager.getInstance(applicationContext)
        val accountManager = AccountManager.getInstance(applicationContext)
        val configManager = ConfigManager.getInstance(applicationContext)
        val apiClient = APIClient("https://app.pangolin.net", versionName = versionName)
        val socketManager = (application as ArmadilloApplication).socketManager
        val fingerprintManager = FingerprintManager(applicationContext, socketManager, AndroidFingerprintCollector(applicationContext))

        val authManager = AuthManager(
            context = applicationContext,
            apiClient = apiClient,
            configManager = configManager,
            accountManager = accountManager,
            secretManager = secretManager
        )
        val tunnelManager = TunnelManager.getInstance(
            context = applicationContext,
            authManager = authManager,
            accountManager = accountManager,
            secretManager = secretManager,
            configManager = configManager,
            socketManager = socketManager,
            fingerprintManager = fingerprintManager
        )
        authManager.tunnelManager = tunnelManager

        setContent {
            ArmadilloTheme {
                val isInitializing by authManager.isInitializing.collectAsState()
                val isAuthenticated by authManager.isAuthenticated.collectAsState()
                var didInit by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!didInit) {
                        didInit = true
                        launch { authManager.initialize() }
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        isInitializing -> Text("Loading...")
                        !isAuthenticated -> DeviceAuthScreen(authManager)
                        else -> HomeScreen(authManager, tunnelManager)
                    }
                }
            }
        }
    }
}
