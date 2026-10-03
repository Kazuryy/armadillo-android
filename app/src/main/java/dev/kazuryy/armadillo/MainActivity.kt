package dev.kazuryy.armadillo

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.lifecycleScope
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.AccountsScreen
import dev.kazuryy.armadillo.ui.ExitNodeScreen
import dev.kazuryy.armadillo.ui.SettingsScreen
import dev.kazuryy.armadillo.ui.SitesScreen
import dev.kazuryy.armadillo.ui.HomeScreen
import dev.kazuryy.armadillo.ui.LoginFlow
import dev.kazuryy.armadillo.ui.theme.ArmadilloTheme
import dev.kazuryy.armadillo.util.TunnelManager
import kotlinx.coroutines.launch

private enum class Screen { HOME, ACCOUNTS, ADD_ACCOUNT, EXIT_NODE, SITES, SETTINGS }

class MainActivity : ComponentActivity() {

    private lateinit var tunnelManager: TunnelManager

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch { tunnelManager.connect() }
        }
    }

    private fun requestConnect() {
        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            lifecycleScope.launch { tunnelManager.connect() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val runtime = (application as ArmadilloApplication).runtime
        val authManager = runtime.authManager
        tunnelManager = runtime.tunnelManager
        val disconnectFromUser: suspend () -> Boolean = { runtime.disconnectFromUser() }

        setContent {
            ArmadilloTheme {
                val isInitializing by authManager.isInitializing.collectAsState()
                val isAuthenticated by authManager.isAuthenticated.collectAsState()
                var didInit by remember { mutableStateOf(false) }
                var screen by remember { mutableStateOf(Screen.HOME) }
                val loginCount by authManager.loginCount.collectAsState()

                // A completed sign-in ends the "add account" flow
                LaunchedEffect(loginCount) {
                    if (screen == Screen.ADD_ACCOUNT) screen = Screen.HOME
                }
                // Logging out the last account must not leave a stale screen behind
                LaunchedEffect(isAuthenticated) {
                    if (!isAuthenticated) screen = Screen.HOME
                }

                val leaveAddAccount = {
                    authManager.cancelDeviceAuth()
                    // The login flow pointed the shared API client at the new host
                    authManager.syncApiClientForActiveAccount()
                    screen = Screen.HOME
                }
                BackHandler(enabled = screen == Screen.ACCOUNTS || screen == Screen.EXIT_NODE || screen == Screen.SITES || screen == Screen.SETTINGS) {
                    screen = Screen.HOME
                }
                BackHandler(enabled = screen == Screen.ADD_ACCOUNT) { leaveAddAccount() }

                LaunchedEffect(Unit) {
                    if (!didInit) {
                        didInit = true
                        launch { authManager.initialize() }
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        isInitializing -> Text("Loading...")
                        !isAuthenticated -> LoginFlow(authManager)
                        screen == Screen.ACCOUNTS -> AccountsScreen(
                            authManager = authManager,
                            onAddAccount = { screen = Screen.ADD_ACCOUNT },
                            onBack = { screen = Screen.HOME }
                        )
                        screen == Screen.ADD_ACCOUNT -> LoginFlow(authManager, onExit = leaveAddAccount)
                        screen == Screen.EXIT_NODE -> ExitNodeScreen(tunnelManager, onBack = { screen = Screen.HOME })
                        screen == Screen.SITES -> SitesScreen(tunnelManager, onBack = { screen = Screen.HOME })
                        screen == Screen.SETTINGS -> SettingsScreen(runtime.configManager, onBack = { screen = Screen.HOME })
                        else -> HomeScreen(
                            authManager,
                            tunnelManager,
                            onConnectRequested = ::requestConnect,
                            onDisconnectRequested = disconnectFromUser,
                            onOpenAccounts = { screen = Screen.ACCOUNTS },
                            onOpenExitNode = { screen = Screen.EXIT_NODE },
                            onOpenSites = { screen = Screen.SITES },
                            onOpenSettings = { screen = Screen.SETTINGS }
                        )
                    }
                }
            }
        }
    }
}
