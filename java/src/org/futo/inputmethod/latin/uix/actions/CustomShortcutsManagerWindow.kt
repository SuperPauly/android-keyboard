package org.futo.inputmethod.latin.uix.actions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.KeyboardManagerForAction

/**
 * ActionWindow for managing custom shortcuts
 */
class CustomShortcutsManagerWindow(
    private val keyboardManager: KeyboardManagerForAction
) : ActionWindow() {

    override val showCloseButton = true
    
    @Composable
    override fun windowName(): String {
        return stringResource(R.string.custom_shortcuts_settings_title)
    }

    @Composable
    override fun WindowContents(keyboardShown: Boolean) {
        val context = LocalContext.current
        var shortcuts by remember { mutableStateOf<List<CustomShortcut>>(emptyList()) }
        var enabledShortcuts by remember { mutableStateOf<Set<String>>(emptySet()) }
        var loading by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()

        // Load shortcuts when the window opens
        LaunchedEffect(Unit) {
            try {
                val predefinedShortcuts = CommonShortcuts.ALL_PREDEFINED
                val customShortcuts = CustomShortcutsManager.getCustomShortcuts(context)
                shortcuts = predefinedShortcuts + customShortcuts
                enabledShortcuts = CustomShortcutsManager.getEnabledShortcuts(context)
            } catch (e: Exception) {
                // Handle error
            } finally {
                loading = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.custom_shortcuts_settings_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    item {
                        Text(
                            text = stringResource(R.string.custom_shortcuts_predefined_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(CommonShortcuts.ALL_PREDEFINED) { shortcut ->
                        ShortcutItem(
                            shortcut = shortcut,
                            isEnabled = shortcut.id in enabledShortcuts,
                            onToggleEnabled = { enabled ->
                                scope.launch {
                                    CustomShortcutsManager.setShortcutEnabled(context, shortcut.id, enabled)
                                    enabledShortcuts = if (enabled) {
                                        enabledShortcuts + shortcut.id
                                    } else {
                                        enabledShortcuts - shortcut.id
                                    }
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.custom_shortcuts_user_defined_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    val userShortcuts = shortcuts.filter { it.id !in CommonShortcuts.ALL_PREDEFINED.map { s -> s.id } }
                    
                    if (userShortcuts.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.custom_shortcuts_no_shortcuts),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = stringResource(R.string.custom_shortcuts_tap_to_add),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(userShortcuts) { shortcut ->
                            ShortcutItem(
                                shortcut = shortcut,
                                isEnabled = shortcut.id in enabledShortcuts,
                                isCustom = true,
                                onToggleEnabled = { enabled ->
                                    scope.launch {
                                        CustomShortcutsManager.setShortcutEnabled(context, shortcut.id, enabled)
                                        enabledShortcuts = if (enabled) {
                                            enabledShortcuts + shortcut.id
                                        } else {
                                            enabledShortcuts - shortcut.id
                                        }
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        CustomShortcutsManager.removeCustomShortcut(context, shortcut.id)
                                        shortcuts = shortcuts.filter { it.id != shortcut.id }
                                        enabledShortcuts = enabledShortcuts - shortcut.id
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        ExtendedFloatingActionButton(
                            onClick = {
                                // TODO: Open add shortcut dialog
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.custom_shortcuts_add_shortcut))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    shortcut: CustomShortcut,
    isEnabled: Boolean,
    isCustom: Boolean = false,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(id = shortcut.icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shortcut.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = shortcut.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isCustom && onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        painterResource(id = R.drawable.delete),
                        contentDescription = stringResource(R.string.custom_shortcuts_delete_shortcut)
                    )
                }
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggleEnabled
            )
        }
    }
}