package com.noxy.assistant.ui.guardian

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.noxy.assistant.models.GuardianState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NoxyGuardianScreen(
    modifier: Modifier = Modifier,
    viewModel: NoxyGuardianViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycleCompat()

    Surface(modifier = modifier.fillMaxSize()) {
        if (state.safeMode) {
            GuardianSafeModeView(state = state, onUnlock = viewModel::requestDisableSafeMode)
        } else {
            GuardianDashboard(
                state = state,
                onActivateSafeMode = viewModel::activateSafeModeLocal,
                onSendLocation = viewModel::sendLocationNow,
                onTriggerAlarm = viewModel::triggerAlarmTest,
                onRefreshCommands = viewModel::fetchAndApplyCommands
            )
        }
    }
}

@Composable
private fun GuardianSafeModeView(
    state: GuardianState,
    onUnlock: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Noxy Guardian – Safe Mode",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Vos données sont protégées. Saisissez le code PIN pour déverrouiller.",
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )
        BasicTextField(
            value = pin,
            onValueChange = { pin = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = { onUnlock(pin) }) {
            Text("Déverrouiller")
        }
        Text(
            text = "Appareil verrouillé : ${state.locked}",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun GuardianDashboard(
    state: GuardianState,
    onActivateSafeMode: () -> Unit,
    onSendLocation: () -> Unit,
    onTriggerAlarm: () -> Unit,
    onRefreshCommands: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Centre de sécurité Noxy Guardian",
            style = MaterialTheme.typography.headlineMedium
        )
        Text("Safe Mode actif : ${state.safeMode}")
        Text("Appareil verrouillé : ${state.locked}")
        Text("Dernière localisation envoyée : ${state.lastLocationSentAt ?: "—"}")
        Text("Dernière alarme : ${state.lastAlarmAt ?: "—"}")

        Button(onClick = onActivateSafeMode) { Text("Activer Safe Mode") }
        Button(onClick = onSendLocation) { Text("Envoyer localisation maintenant") }
        Button(onClick = onTriggerAlarm) { Text("Déclencher alarme test") }
        Button(onClick = onRefreshCommands) { Text("Rafraîchir commandes") }
    }
}

@Composable
private fun <T> StateFlow<T>.collectAsStateWithLifecycleCompat(): State<T> {
    return collectAsState(initial = value)
}
