package WindowsAPI;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

public class EnumerateWindows
{
  public static class Psapi
  {
    static
    {
      Native.register("psapi");
    }

    public static native int GetModuleBaseNameW(Pointer hProcess,
        Pointer hmodule, char[] lpBaseName, int size);
  }

  public static class Kernel32
  {
    static
    {
      Native.register("kernel32");
    }
    public static int PROCESS_QUERY_INFORMATION = 0x0400;
    public static int PROCESS_VM_READ = 0x0010;

    public static native int GetLastError();

    public static native Pointer OpenProcess(int dwDesiredAccess,
        boolean bInheritHandle, Pointer pointer);
  }

  public static class User32DLL
  {
    static
    {
      Native.register("user32");
    }

    public static native int GetWindowThreadProcessId(HWND hWnd,
        PointerByReference pref);

    public static native HWND GetForegroundWindow();

    public static native int GetWindowTextW(HWND hWnd, char[] lpString,
        int nMaxCount);
  }
}
