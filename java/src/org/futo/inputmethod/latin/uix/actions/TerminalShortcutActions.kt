package org.futo.inputmethod.latin.uix.actions

import android.view.KeyEvent
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

/**
 * Predefined terminal shortcut actions that users can enable
 */

val CtrlLAction = Action(
    icon = R.drawable.maximize,
    name = R.string.custom_shortcut_dynamic_name,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_L, KeyEvent.META_CTRL_ON)
    },
    windowImpl = null,
)

val CtrlRAction = Action(
    icon = R.drawable.search,
    name = R.string.custom_shortcut_dynamic_name,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_R, KeyEvent.META_CTRL_ON)
    },
    windowImpl = null,
)

val CtrlZAction = Action(
    icon = R.drawable.undo,
    name = R.string.custom_shortcut_dynamic_name,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON)
    },
    windowImpl = null,
)

val EscAction = Action(
    icon = R.drawable.close,
    name = R.string.custom_shortcut_dynamic_name,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_ESCAPE, 0)
    },
    windowImpl = null,
)

val TabAction = Action(
    icon = R.drawable.direction_arrows,
    name = R.string.custom_shortcut_dynamic_name,
    simplePressImpl = { manager, _ ->
        manager.sendKeyEvent(KeyEvent.KEYCODE_TAB, 0)
    },
    windowImpl = null,
)