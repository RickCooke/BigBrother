package WindowsAPI;


import BigBrother.Main.Client;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;

public class KeyboardCallback {
    public static User32.HHOOK hHook;
    public static User32.LowLevelKeyboardProc lpfn;

    public KeyboardCallback() {
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        lpfn = new User32.LowLevelKeyboardProc() {
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam) {
                final int rawCode = lParam.vkCode;
                final long evtTime = lParam.time;

                switch (wParam.intValue()) {
                    case WinUser.WM_KEYDOWN:
                        System.out.println("Key : " + rawCode + "was pressed @ " + evtTime);
                        Client.resetIdleTimer();
                        break;
                    case WinUser.WM_SYSKEYDOWN:
                        break;
                    case WinUser.WM_KEYUP:
                        break;
                    case WinUser.WM_SYSKEYUP:
                        break;
                }
                return User32.INSTANCE.CallNextHookEx(hHook, nCode, wParam, lParam.getPointer());
            }
        };
        
        hHook = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, lpfn, hMod, 0);
        if (hHook == null)
            return;
        /*
         * if (User32.INSTANCE.UnhookWindowsHookEx(hHook)) System.out.println("Unhooked");
         */
    }
}
