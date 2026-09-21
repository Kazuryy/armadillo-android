package dev.kazuryy.armadillo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.CardBackground
import dev.kazuryy.armadillo.ui.theme.DangerRed
import dev.kazuryy.armadillo.ui.theme.SecondaryText
import dev.kazuryy.armadillo.util.Account
import dev.kazuryy.armadillo.util.AuthManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun AccountsScreen(
    authManager: AuthManager,
    onAddAccount: () -> Unit,
    onBack: () -> Unit
) {
    val store by authManager.accountManager.store.collectAsState()
    val scope = rememberCoroutineScope()
    val accounts = store.accounts.values.sortedBy { it.email.lowercase() }

    var pendingLogout by remember { mutableStateOf<Account?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val initialFocus = remember { FocusRequester() }

    // The confirmation dialog handles Back first, then the screen itself
    BackHandler(enabled = pendingLogout != null) { pendingLogout = null }

    LaunchedEffect(pendingLogout) { initialFocus.requestFocus() }

    ArmadilloBackground(isActive = false) {
        Column(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val pending = pendingLogout
            if (pending != null) {
                LogoutConfirmation(
                    account = pending,
                    isBusy = isBusy,
                    cancelFocus = initialFocus,
                    onCancel = { pendingLogout = null },
                    onConfirm = {
                        isBusy = true
                        scope.launch {
                            try {
                                authManager.logoutAccount(pending.userId)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                error = e.message ?: "Logout failed"
                            } finally {
                                isBusy = false
                                pendingLogout = null
                            }
                        }
                    }
                )
            } else {
                Text(text = "Accounts", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                accounts.forEach { account ->
                    AccountRow(
                        account = account,
                        isActive = account.userId == store.activeUserId,
                        isBusy = isBusy,
                        onSwitch = {
                            isBusy = true
                            error = null
                            scope.launch {
                                try {
                                    authManager.switchAccount(account.userId)
                                    onBack()
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    error = e.message ?: "Could not switch account"
                                } finally {
                                    isBusy = false
                                }
                            }
                        },
                        onLogout = { pendingLogout = account }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                error?.let {
                    Text(text = it, fontSize = 14.sp, color = SecondaryText)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onAddAccount,
                        enabled = !isBusy,
                        modifier = Modifier.focusRequester(initialFocus),
                        colors = ButtonDefaults.colors(containerColor = BrandOrange, contentColor = Color.Black)
                    ) { Text("Add account") }
                    Button(onClick = onBack, enabled = !isBusy) { Text("Back") }
                }
            }
        }
    }
}

@Composable
private fun AccountRow(
    account: Account,
    isActive: Boolean,
    isBusy: Boolean,
    onSwitch: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(720.dp)
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = account.email, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(
                text = account.hostname.removePrefix("https://") + if (isActive) "  ·  Active" else "",
                fontSize = 14.sp,
                color = if (isActive) BrandOrange else SecondaryText
            )
        }
        if (!isActive) {
            Button(onClick = onSwitch, enabled = !isBusy) { Text("Switch") }
        }
        Button(
            onClick = onLogout,
            enabled = !isBusy,
            colors = ButtonDefaults.colors(containerColor = DangerRed, contentColor = Color.White)
        ) { Text("Log out") }
    }
}

@Composable
private fun LogoutConfirmation(
    account: Account,
    isBusy: Boolean,
    cancelFocus: FocusRequester,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Text(text = "Log out ${account.email}?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "This erases the session and the device credentials of this account from this TV.",
        fontSize = 16.sp,
        color = SecondaryText
    )
    Spacer(modifier = Modifier.height(28.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onCancel,
            enabled = !isBusy,
            modifier = Modifier.focusRequester(cancelFocus)
        ) { Text("Cancel") }
        Button(
            onClick = onConfirm,
            enabled = !isBusy,
            colors = ButtonDefaults.colors(containerColor = DangerRed, contentColor = Color.White)
        ) { Text(if (isBusy) "Logging out..." else "Log out") }
    }
}
