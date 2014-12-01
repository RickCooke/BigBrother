package WindowsAPI;


import BigBrother.Client.Client;
import BigBrother.Client.Main;
import BigBrother.Exceptions.KeyboardHookFailed;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;

public class Keyboard
{
  private static User32.HHOOK hHook;
  private static User32.LowLevelKeyboardProc lpfn;
  private static boolean hooked = false;

  public static void Initialize()
  {
    lpfn = new User32.LowLevelKeyboardProc()
    {
      public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT lParam)
      {
        final int rawCode = lParam.vkCode;
        final long evtTime = lParam.time;

        switch ( wParam.intValue() )
        {
          case WinUser.WM_KEYDOWN:
            if( Main.settings.debug )
              System.out.println("Key : " + rawCode + " was pressed @ "
                  + evtTime);
            Client.setIdle(false);
            break;
        }
        return User32.INSTANCE.CallNextHookEx(hHook, nCode, wParam,
            lParam.getPointer());
      }
    };
  }

  public static void hook() throws KeyboardHookFailed
  {
    HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
    hHook = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, lpfn, hMod,
        0);
    if( hHook == null )
    {
      throw new KeyboardHookFailed();
    }

    hooked = true;
    if( Main.settings.debug )
      System.out.println("Keyboard Hooked");
  }

  public static boolean isHooked()
  {
    return hooked;
  }

  public static void unhook() throws KeyboardHookFailed
  {
    if( User32.INSTANCE.UnhookWindowsHookEx(hHook) == false )
    {
      throw new KeyboardHookFailed();
    }

    hooked = false;
    if( Main.settings.debug )
      System.out.println("Keyboard Unhooked");
  }
}
