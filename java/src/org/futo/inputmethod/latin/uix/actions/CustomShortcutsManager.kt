package org.futo.inputmethod.latin.uix.actions

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.futo.inputmethod.latin.uix.SettingsKey
import org.futo.inputmethod.latin.uix.getSetting
import org.futo.inputmethod.latin.uix.setSetting

/**
 * Manager for custom shortcuts storage and retrieval
 */
object CustomShortcutsManager {
    
    // Settings key for storing custom shortcuts as JSON
    private val CUSTOM_SHORTCUTS_KEY = SettingsKey(
        stringPreferencesKey("custom_shortcuts"),
        "[]" // Default to empty list
    )
    
    // Settings key for storing enabled shortcuts (by ID)
    private val ENABLED_SHORTCUTS_KEY = SettingsKey(
        stringPreferencesKey("enabled_custom_shortcuts"),
        "" // Default to empty string
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Get all custom shortcuts defined by the user
     */
    suspend fun getCustomShortcuts(context: Context): List<CustomShortcut> {
        val jsonString = context.getSetting(CUSTOM_SHORTCUTS_KEY)
        return try {
            json.decodeFromString<List<CustomShortcut>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get enabled custom shortcuts
     */
    suspend fun getEnabledShortcuts(context: Context): Set<String> {
        val enabledString = context.getSetting(ENABLED_SHORTCUTS_KEY)
        return enabledString.split(",").filter { it.isNotBlank() }.toSet()
    }
    
    /**
     * Save custom shortcuts
     */
    suspend fun saveCustomShortcuts(context: Context, shortcuts: List<CustomShortcut>) {
        val jsonString = json.encodeToString(shortcuts)
        context.setSetting(CUSTOM_SHORTCUTS_KEY.key, jsonString)
    }
    
    /**
     * Save enabled shortcuts
     */
    suspend fun saveEnabledShortcuts(context: Context, enabledIds: Set<String>) {
        val enabledString = enabledIds.joinToString(",")
        context.setSetting(ENABLED_SHORTCUTS_KEY.key, enabledString)
    }
    
    /**
     * Add a new custom shortcut
     */
    suspend fun addCustomShortcut(context: Context, shortcut: CustomShortcut) {
        val existing = getCustomShortcuts(context)
        val updated = existing + shortcut
        saveCustomShortcuts(context, updated)
    }
    
    /**
     * Remove a custom shortcut by ID
     */
    suspend fun removeCustomShortcut(context: Context, shortcutId: String) {
        val existing = getCustomShortcuts(context)
        val updated = existing.filter { it.id != shortcutId }
        saveCustomShortcuts(context, updated)
        
        // Also remove from enabled list
        val enabled = getEnabledShortcuts(context)
        val updatedEnabled = enabled - shortcutId
        saveEnabledShortcuts(context, updatedEnabled)
    }
    
    /**
     * Update an existing custom shortcut
     */
    suspend fun updateCustomShortcut(context: Context, shortcut: CustomShortcut) {
        val existing = getCustomShortcuts(context)
        val updated = existing.map { if (it.id == shortcut.id) shortcut else it }
        saveCustomShortcuts(context, updated)
    }
    
    /**
     * Enable or disable a shortcut
     */
    suspend fun setShortcutEnabled(context: Context, shortcutId: String, enabled: Boolean) {
        val currentEnabled = getEnabledShortcuts(context)
        val updated = if (enabled) {
            currentEnabled + shortcutId
        } else {
            currentEnabled - shortcutId
        }
        saveEnabledShortcuts(context, updated)
    }
    
    /**
     * Get all available shortcuts (predefined + custom) that are currently enabled
     */
    suspend fun getAvailableShortcuts(context: Context): List<CustomShortcut> {
        val custom = getCustomShortcuts(context)
        val all = CommonShortcuts.ALL_PREDEFINED + custom
        val enabled = getEnabledShortcuts(context)
        
        return all.filter { it.id in enabled }
    }
    
    /**
     * Initialize with some default shortcuts enabled
     */
    suspend fun initializeDefaultShortcuts(context: Context) {
        val enabled = getEnabledShortcuts(context)
        if (enabled.isEmpty()) {
            // Enable some common shortcuts by default
            val defaultEnabled = setOf(
                CommonShortcuts.CTRL_L.id,
                CommonShortcuts.CTRL_R.id,
                CommonShortcuts.CTRL_Z.id,
                CommonShortcuts.ESC.id,
                CommonShortcuts.TAB.id
            )
            saveEnabledShortcuts(context, defaultEnabled)
        }
    }
}