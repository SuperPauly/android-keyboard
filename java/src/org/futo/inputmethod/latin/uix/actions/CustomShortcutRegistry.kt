package org.futo.inputmethod.latin.uix.actions

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.KeyboardManagerForAction

/**
 * Registry for managing dynamic custom shortcuts
 */
object CustomShortcutRegistry {
    
    // Cache for dynamic actions to avoid recreating them
    private var customShortcutActions: Map<String, Action> = emptyMap()
    private var lastKnownShortcuts: List<CustomShortcut> = emptyList()
    
    /**
     * Initialize default shortcuts on first run
     */
    fun initializeIfNeeded(context: Context, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            CustomShortcutsManager.initializeDefaultShortcuts(context)
        }
    }
    
    /**
     * Get all custom shortcut actions currently enabled
     */
    suspend fun getCustomShortcutActions(context: Context): Map<String, Action> {
        val currentShortcuts = CustomShortcutsManager.getAvailableShortcuts(context)
        
        // Check if we need to regenerate actions
        if (currentShortcuts != lastKnownShortcuts) {
            customShortcutActions = currentShortcuts.associate { shortcut ->
                "custom_shortcut_${shortcut.id}" to createActionFromShortcut(shortcut)
            }
            lastKnownShortcuts = currentShortcuts
        }
        
        return customShortcutActions
    }
    
    /**
     * Create an Action from a CustomShortcut
     */
    private fun createActionFromShortcut(shortcut: CustomShortcut): Action {
        return Action(
            icon = shortcut.icon,
            name = R.string.custom_shortcut_dynamic_name, // We'll handle dynamic names in the UI
            simplePressImpl = { manager, _ ->
                manager.sendKeyEvent(shortcut.keyCode, shortcut.metaState)
            },
            windowImpl = null,
        )
    }
    
    /**
     * Get a shortcut action by ID
     */
    suspend fun getShortcutAction(context: Context, shortcutId: String): Action? {
        val actions = getCustomShortcutActions(context)
        return actions["custom_shortcut_$shortcutId"]
    }
    
    /**
     * Clear the cache to force regeneration
     */
    fun clearCache() {
        customShortcutActions = emptyMap()
        lastKnownShortcuts = emptyList()
    }
}

/**
 * Action for managing custom shortcuts (opens settings UI)
 */
val CustomShortcutsManagerAction = Action(
    icon = R.drawable.keyboard_gear_fill,
    name = R.string.action_custom_shortcuts_manager_title,
    simplePressImpl = null,
    windowImpl = { manager, _ -> 
        CustomShortcutsManagerWindow(manager)
    },
)