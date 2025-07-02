package org.futo.inputmethod.latin.uix.actions

import android.view.KeyEvent
import kotlinx.serialization.Serializable
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

/**
 * Represents a custom key combination shortcut
 */
@Serializable
data class CustomShortcut(
    val id: String,
    val name: String,
    val description: String,
    val keyCode: Int,
    val metaState: Int,
    val icon: Int = R.drawable.keyboard_gear_fill // Default icon
) {
    /**
     * Creates an Action from this custom shortcut
     */
    fun toAction(): Action {
        return Action(
            icon = icon,
            name = R.string.custom_shortcut_dynamic_name, // We'll handle dynamic names differently
            simplePressImpl = { manager, _ ->
                manager.sendKeyEvent(keyCode, metaState)
            },
            windowImpl = null,
        )
    }
}

/**
 * Predefined common shortcuts that users might want
 */
object CommonShortcuts {
    val CTRL_L = CustomShortcut(
        id = "ctrl_l",
        name = "Clear Screen (Ctrl+L)",
        description = "Clear terminal screen",
        keyCode = KeyEvent.KEYCODE_L,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_R = CustomShortcut(
        id = "ctrl_r",
        name = "Search History (Ctrl+R)",
        description = "Search command history",
        keyCode = KeyEvent.KEYCODE_R,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_Z = CustomShortcut(
        id = "ctrl_z",
        name = "Suspend Process (Ctrl+Z)",
        description = "Suspend current process",
        keyCode = KeyEvent.KEYCODE_Z,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_D = CustomShortcut(
        id = "ctrl_d",
        name = "EOF/Logout (Ctrl+D)",
        description = "Send EOF or logout",
        keyCode = KeyEvent.KEYCODE_D,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_W = CustomShortcut(
        id = "ctrl_w",
        name = "Delete Word (Ctrl+W)",
        description = "Delete previous word",
        keyCode = KeyEvent.KEYCODE_W,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_U = CustomShortcut(
        id = "ctrl_u",
        name = "Delete Line (Ctrl+U)",
        description = "Delete entire line",
        keyCode = KeyEvent.KEYCODE_U,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val CTRL_K = CustomShortcut(
        id = "ctrl_k",
        name = "Delete to End (Ctrl+K)",
        description = "Delete from cursor to end of line",
        keyCode = KeyEvent.KEYCODE_K,
        metaState = KeyEvent.META_CTRL_ON,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val ESC = CustomShortcut(
        id = "esc",
        name = "Escape",
        description = "Escape key",
        keyCode = KeyEvent.KEYCODE_ESCAPE,
        metaState = 0,
        icon = R.drawable.keyboard_key_feedback_more_background
    )
    
    val TAB = CustomShortcut(
        id = "tab",
        name = "Tab",
        description = "Tab key",
        keyCode = KeyEvent.KEYCODE_TAB,
        metaState = 0,
        icon = R.drawable.keyboard_key_feedback_more_background
    )

    /**
     * List of all predefined shortcuts
     */
    val ALL_PREDEFINED = listOf(
        CTRL_L, CTRL_R, CTRL_Z, CTRL_D, CTRL_W, CTRL_U, CTRL_K, ESC, TAB
    )
}