package org.futo.inputmethod.latin.uix.actions;

import android.content.Context;
import android.view.KeyEvent;

import androidx.test.InstrumentationRegistry;

import org.futo.inputmethod.latin.uix.KeyboardManagerForAction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for custom shortcuts functionality
 */
public class CustomShortcutsTest {

    @Test
    public void testCommonShortcutsExist() {
        // Test that all predefined shortcuts are defined
        Assert.assertNotNull("Ctrl+L shortcut should exist", CommonShortcuts.CTRL_L);
        Assert.assertNotNull("Ctrl+R shortcut should exist", CommonShortcuts.CTRL_R);
        Assert.assertNotNull("Ctrl+Z shortcut should exist", CommonShortcuts.CTRL_Z);
        Assert.assertNotNull("Escape shortcut should exist", CommonShortcuts.ESC);
        Assert.assertNotNull("Tab shortcut should exist", CommonShortcuts.TAB);
        
        // Test that the predefined shortcuts list contains our shortcuts
        Assert.assertTrue("ALL_PREDEFINED should contain Ctrl+L", 
            CommonShortcuts.ALL_PREDEFINED.contains(CommonShortcuts.CTRL_L));
        Assert.assertTrue("ALL_PREDEFINED should contain Tab", 
            CommonShortcuts.ALL_PREDEFINED.contains(CommonShortcuts.TAB));
    }

    @Test
    public void testCustomShortcutKeyMapping() {
        // Test that shortcuts have correct key codes and meta states
        Assert.assertEquals("Ctrl+L should have L key code", 
            KeyEvent.KEYCODE_L, CommonShortcuts.CTRL_L.getKeyCode());
        Assert.assertEquals("Ctrl+L should have Ctrl meta state", 
            KeyEvent.META_CTRL_ON, CommonShortcuts.CTRL_L.getMetaState());
        
        Assert.assertEquals("Tab should have Tab key code", 
            KeyEvent.KEYCODE_TAB, CommonShortcuts.TAB.getKeyCode());
        Assert.assertEquals("Tab should have no meta state", 
            0, CommonShortcuts.TAB.getMetaState());
        
        Assert.assertEquals("Escape should have Escape key code", 
            KeyEvent.KEYCODE_ESCAPE, CommonShortcuts.ESC.getKeyCode());
        Assert.assertEquals("Escape should have no meta state", 
            0, CommonShortcuts.ESC.getMetaState());
    }

    @Test 
    public void testCustomShortcutIds() {
        // Test that shortcuts have unique IDs
        Assert.assertEquals("Ctrl+L should have correct ID", "ctrl_l", CommonShortcuts.CTRL_L.getId());
        Assert.assertEquals("Ctrl+R should have correct ID", "ctrl_r", CommonShortcuts.CTRL_R.getId());
        Assert.assertEquals("Ctrl+Z should have correct ID", "ctrl_z", CommonShortcuts.CTRL_Z.getId());
        Assert.assertEquals("Escape should have correct ID", "esc", CommonShortcuts.ESC.getId());
        Assert.assertEquals("Tab should have correct ID", "tab", CommonShortcuts.TAB.getId());
    }

    @Test
    public void testActionGeneration() {
        // Test that shortcuts can be converted to Actions
        org.futo.inputmethod.latin.uix.Action action = CommonShortcuts.CTRL_L.toAction();
        Assert.assertNotNull("Ctrl+L action should not be null", action);
        Assert.assertNotNull("Ctrl+L action should have a simplePressImpl", action.getSimplePressImpl());
        Assert.assertNull("Ctrl+L action should not have a windowImpl", action.getWindowImpl());
    }

    /**
     * Mock KeyboardManagerForAction for testing
     */
    private static class MockKeyboardManager implements KeyboardManagerForAction {
        private int lastKeyCode = -1;
        private int lastMetaState = -1;
        
        @Override
        public void sendKeyEvent(int keyCode, int metaState) {
            this.lastKeyCode = keyCode;
            this.lastMetaState = metaState;
        }
        
        public int getLastKeyCode() { return lastKeyCode; }
        public int getLastMetaState() { return lastMetaState; }
        
        // Stub implementations for other required methods
        @Override public Context getContext() { return InstrumentationRegistry.getTargetContext(); }
        @Override public androidx.lifecycle.LifecycleCoroutineScope getLifecycleScope() { return null; }
        @Override public org.futo.inputmethod.latin.uix.ActionInputTransaction createInputTransaction() { return null; }
        @Override public void typeText(String v) {}
        @Override public boolean typeUri(android.net.Uri uri, java.util.List<String> mimeTypes, boolean ignoreConnectionOverride) { return false; }
        @Override public boolean appSupportsImageInsertion(String schema, boolean ignoreConnectionOverride) { return false; }
        @Override public void backspace(int amount) {}
        @Override public void closeActionWindow() {}
        @Override public void forceActionWindowAboveKeyboard(boolean to) {}
        @Override public void triggerSystemVoiceInput() {}
        @Override public void updateTheme(org.futo.inputmethod.latin.uix.theme.ThemeOption newTheme) {}
        @Override public org.futo.inputmethod.latin.uix.DynamicThemeProvider getThemeProvider() { return null; }
        @Override public void sendCodePointEvent(int codePoint) {}
        @Override public boolean isShifted() { return false; }
        @Override public void cursorLeft(int steps, boolean stepOverWords, boolean select) {}
        @Override public void cursorRight(int steps, boolean stepOverWords, boolean select) {}
        @Override public void performHapticAndAudioFeedback(int code, android.view.View view) {}
        @Override public void announce(String s) {}
        @Override public java.util.List<java.util.Locale> getActiveLocales() { return null; }
        @Override public void overrideInputConnection(android.view.inputmethod.InputConnection inputConnection, android.view.inputmethod.EditorInfo editorInfo) {}
        @Override public void unsetInputConnection() {}
        @Override public void requestDialog(String text, java.util.List<org.futo.inputmethod.latin.uix.DialogRequestItem> options, Runnable onCancel) {}
        @Override public void openInputMethodPicker() {}
        @Override public void activateAction(org.futo.inputmethod.latin.uix.Action action) {}
        @Override public void showActionEditor() {}
        @Override public org.futo.inputmethod.latin.SuggestionBlacklist getSuggestionBlacklist() { return null; }
        @Override public org.futo.inputmethod.latin.LatinIME getLatinIMEForDebug() { return null; }
        @Override public boolean isDeviceLocked() { return false; }
        @Override public org.futo.inputmethod.v2keyboard.KeyboardSizingCalculator getSizingCalculator() { return null; }
        @Override public void showResizer() {}
        @Override public org.futo.inputmethod.latin.uix.TutorialMode getTutorialMode() { return org.futo.inputmethod.latin.uix.TutorialMode.None; }
        @Override public void setTutorialArrowPosition(androidx.compose.ui.layout.LayoutCoordinates coordinates) {}
        @Override public void markTutorialCompleted() {}
        @Override public void overrideKeyboardTypeface(android.graphics.Typeface typeface) {}
    }

    @Test
    public void testActionExecution() {
        // Test that actions send the correct key events
        MockKeyboardManager manager = new MockKeyboardManager();
        
        // Test Ctrl+L action
        org.futo.inputmethod.latin.uix.Action ctrlLAction = CommonShortcuts.CTRL_L.toAction();
        ctrlLAction.getSimplePressImpl().invoke(manager, null);
        
        Assert.assertEquals("Ctrl+L should send L key code", 
            KeyEvent.KEYCODE_L, manager.getLastKeyCode());
        Assert.assertEquals("Ctrl+L should send Ctrl meta state", 
            KeyEvent.META_CTRL_ON, manager.getLastMetaState());
        
        // Test Tab action
        org.futo.inputmethod.latin.uix.Action tabAction = CommonShortcuts.TAB.toAction();
        tabAction.getSimplePressImpl().invoke(manager, null);
        
        Assert.assertEquals("Tab should send Tab key code", 
            KeyEvent.KEYCODE_TAB, manager.getLastKeyCode());
        Assert.assertEquals("Tab should send no meta state", 
            0, manager.getLastMetaState());
    }
}