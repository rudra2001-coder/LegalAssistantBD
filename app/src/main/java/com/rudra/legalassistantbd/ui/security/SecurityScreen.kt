package com.rudra.legalassistantbd.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.rudra.legalassistantbd.core.security.SecurityManager
import com.rudra.legalassistantbd.ui.components.*
import com.rudra.legalassistantbd.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    private val _isAppLockEnabled = MutableStateFlow(securityManager.isAppLockEnabled())
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled.asStateFlow()

    private val _isPinSet = MutableStateFlow(securityManager.isPinSet())
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(securityManager.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isBiometricAvailable = MutableStateFlow(securityManager.isBiometricAvailable())
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun toggleAppLock(enabled: Boolean) {
        securityManager.setAppLockEnabled(enabled)
        _isAppLockEnabled.value = enabled
        _statusMessage.value = if (enabled) "App lock enabled" else "App lock disabled"
    }

    fun setPin(pin: String) {
        securityManager.setAppPin(pin)
        _isPinSet.value = true
        _statusMessage.value = "PIN set successfully"
    }

    fun verifyPin(pin: String): Boolean {
        val result = securityManager.verifyPin(pin)
        if (!result) {
            _statusMessage.value = "Incorrect PIN"
        }
        return result
    }

    fun toggleBiometric(enabled: Boolean) {
        securityManager.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
        _statusMessage.value = if (enabled) "Biometric enabled" else "Biometric disabled"
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}

@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val isPinSet by viewModel.isPinSet.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val isBiometricAvailable by viewModel.isBiometricAvailable.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(title = "Security", onBackClick = { navController.popBackStack() })
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Security Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteSoft,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App Lock",
                                style = MaterialTheme.typography.titleMedium,
                                color = WhiteSoft,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Require PIN to open the app",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayLight
                            )
                        }
                        Switch(
                            checked = isAppLockEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !isPinSet) {
                                    showPinDialog = true
                                } else {
                                    viewModel.toggleAppLock(enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = Gold)
                        )
                    }

                    if (isPinSet) {
                        HorizontalDivider(
                            color = DarkSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Keyboard, contentDescription = null, tint = GrayLight, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("Change PIN", color = WhiteSoft, modifier = Modifier.weight(1f))
                            TextButton(onClick = { showChangePinDialog = true }) {
                                Text("Change", color = Gold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (isBiometricAvailable) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Fingerprint,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Biometric Auth",
                                style = MaterialTheme.typography.titleMedium,
                                color = WhiteSoft,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Use fingerprint to unlock",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayLight
                            )
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric(it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Gold)
                        )
                    }
                }
            }

            statusMessage?.let { message ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = message,
                            color = WhiteSoft,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            containerColor = DarkSurface,
            title = { Text("Set PIN", color = WhiteSoft, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("Enter PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = { Text("Confirm PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPin == confirmPin && newPin.length >= 4) {
                            viewModel.setPin(newPin)
                            viewModel.toggleAppLock(true)
                            showPinDialog = false
                            newPin = ""
                            confirmPin = ""
                        }
                    },
                    enabled = newPin == confirmPin && newPin.length >= 4
                ) { Text("Set PIN", color = Gold) }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel", color = GrayLight) }
            }
        )
    }

    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            containerColor = DarkSurface,
            title = { Text("Change PIN", color = WhiteSoft, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = currentPin,
                        onValueChange = { currentPin = it },
                        label = { Text("Current PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = { Text("Confirm New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (viewModel.verifyPin(currentPin) && newPin == confirmPin && newPin.length >= 4) {
                            viewModel.setPin(newPin)
                            showChangePinDialog = false
                            currentPin = ""
                            newPin = ""
                            confirmPin = ""
                        }
                    },
                    enabled = newPin == confirmPin && newPin.length >= 4 && currentPin.isNotBlank()
                ) { Text("Change", color = Gold) }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) { Text("Cancel", color = GrayLight) }
            }
        )
    }
}
