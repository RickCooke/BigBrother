package BigBrother.Client;

import static WindowsAPI.EnumerateWindows.Kernel32.OpenProcess;
import static WindowsAPI.EnumerateWindows.Kernel32.PROCESS_QUERY_INFORMATION;
import static WindowsAPI.EnumerateWindows.Kernel32.PROCESS_VM_READ;
import static WindowsAPI.EnumerateWindows.Psapi.GetModuleBaseNameW;
import static WindowsAPI.EnumerateWindows.User32DLL.GetForegroundWindow;
import static WindowsAPI.EnumerateWindows.User32DLL.GetWindowTextW;
import static WindowsAPI.EnumerateWindows.User32DLL.GetWindowThreadProcessId;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import BigBrother.Classes.App;
import BigBrother.Exceptions.CountMismatchException;
import BigBrother.Exceptions.KeyboardHookFailed;
import WindowsAPI.Keyboard;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.PointerByReference;


public class Client
{

  private int OTHER_APP_INDEX = 0;
  private int IDLE_APP_INDEX = 1;

  private int pollNum = 0;
  private Point lastKnownMouseLocation;
  private static Timer idleTimer;
  private static TimerTask idleTimerTask;
  private Timer pollTimer;
  private static boolean idleFlag;
  private ArrayList<App> userApps;

  public Client()
  {

    Keyboard.Initialize();

    // get our list of apps
    syncApps();

    // sort the app list by priority
    sortAppList();

    setIdle(false);

    // init mouse location
    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
    lastKnownMouseLocation = pointerInfo.getLocation();

    // Set up the timers
    try
    {
      // Set up the poll timer
      pollTimer = new Timer();
      pollTimer.scheduleAtFixedRate(new TimerTask()
      {
        public void run()
        {
          poll();
        }
      }, Main.settings.polling_interval, Main.settings.polling_interval);

      // Set up the idle timer
      resetIdleTimer();
    }
    catch( Exception e )
    {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  // Compare two apps to one another
  public class AppComparator implements Comparator<App>
  {
    @Override
    public int compare(App a1, App a2)
    {

      if( a1.getPriorityScore() == a2.getPriorityScore() )
      {
        return Integer.compare(a1.getAppID(), a2.getAppID());
      }

      // Sort by highest priority score
      return Integer.compare(a2.getPriorityScore(), a1.getPriorityScore());
    }
  }

  // Query server and set our apps list
  private void syncApps()
  {
    userApps = new ArrayList<App>();
    userApps = MySQL.getTrackedAppsArrayList(Main.loggedInUserID);

    // add default "Other" and "Idle" apps
    userApps.add(new App(0, "Other", null, false, null, false, true));
    userApps.add(new App(1, "Idle", null, false, null, false, true));
  }

  private void sortAppList()
  {
    if( Main.settings.debug )
    {
      System.out.println("Before priority Sorting: ");
      for( App a : userApps )
        a.print();
    }

    // Sort the DLM
    Collections.sort(userApps, new AppComparator());

    if( Main.settings.debug )
    {
      System.out.println("");
      System.out.println("After  priority Sorting: ");
      for( App a : userApps )
        a.print();
      System.out.println("");
    }
  }

  // Function to poll the system for its running apps and add them to memory
  private void poll()
  {
    // if app list is not initialized, exit
    if( userApps == null || userApps.isEmpty() )
      return;

    // Init variables
    char[] buffer = new char[1024 * 2];
    PointerByReference pointer = new PointerByReference();
    Pointer process;
    String windowTitle;
    String processName;

    // Increment the poll counter
    pollNum++;

    // check if the user is idle
    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
    if( lastKnownMouseLocation.distance(pointerInfo.getLocation()) > 0 )
    {
      // debug output
      if( Main.settings.debug )
        System.out.println("Mouse movement detected.");

      // set the idle flag to false
      setIdle(false);

      // update the last known mouse position
      lastKnownMouseLocation = pointerInfo.getLocation();
    }

    // only waste computing power checking if the user isn't idle
    if( !idleFlag )
    {

      // get the Window & Process Names
      GetWindowTextW(
          WindowsAPI.EnumerateWindows.User32DLL.GetForegroundWindow(), buffer,
          1024);
      windowTitle = Native.toString(buffer);
      GetWindowThreadProcessId(GetForegroundWindow(), pointer);
      process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false,
          pointer.getValue());
      GetModuleBaseNameW(process, null, buffer, 1024);
      processName = Native.toString(buffer);

      // Check for app match
      boolean foundMatch = false;
      for( App a : userApps )
      {


        if( a.isMatch(windowTitle, processName) )
        {
          // window was active, record it
          a.addCount(Main.settings.polling_interval);
          foundMatch = true;
          break;
        }
      }

      // if no match was found, then it falls under the "Other" app
      if( !foundMatch )
        userApps.get(OTHER_APP_INDEX).addCount(Main.settings.polling_interval);
    }
    else
    {
      // increment idle counter
      userApps.get(IDLE_APP_INDEX).addCount(Main.settings.polling_interval);
    }


    // Debug
    for( App a : userApps )
      if( Main.settings.debug )
        a.print2();

    // If we need to, flush from memory to local SQLite DB
    try
    {
      if( pollNum
          % (Main.settings.memory_flush_interval / Main.settings.polling_interval) == 0 )
        memFlush();
    }
    catch( CountMismatchException e )
    {
      e.printStackTrace();
    }

    // if we need to, flush from SQLite DB to server
    if( pollNum
        % (Main.settings.local_flush_interval / Main.settings.polling_interval) == 0 )
      localFlush();
  }


  // resets the idle timer to 0
  public static void resetIdleTimer()
  {
    // cancel the old timer
    if( idleTimer != null && idleTimerTask != null )
    {
      idleTimer.cancel();
      idleTimer.purge();
      idleTimerTask.cancel();
    }

    try
    {
      if( Keyboard.isHooked() )
      {
        Keyboard.unhook();
      }
    }
    catch( KeyboardHookFailed e )
    {
      JOptionPane.showMessageDialog(Main.win, e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }

    // Set up the idle timer
    idleTimer = new Timer();
    idleTimerTask = new TimerTask()
    {
      public void run()
      {
        setIdle(true);

        try
        {
          if( !Keyboard.isHooked() )
          {
            Keyboard.hook();
          }
        }
        catch( KeyboardHookFailed e )
        {
          JOptionPane.showMessageDialog(Main.win, e.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }

        User32.MSG msg = new User32.MSG();
        while( true )
        {
          User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);
          try
          {
            Thread.sleep(Main.settings.keyboard_peek_interval);
          }
          catch( InterruptedException e )
          {
            e.printStackTrace();
          }
        }

      }
    };

    idleTimer.schedule(idleTimerTask, Main.settings.max_idle_time);

    // Debug text
    if( Main.settings.debug )
      System.out.println("Activity detected." + " Idle timer has been reset.");
  }

  public static void setIdle(boolean _idle)
  {
    // Debug text if idle changed
    if( Main.settings.debug && idleFlag != _idle )
      System.out.println("The idle flag is being changed to: " + _idle);

    idleFlag = _idle;

    // reset the idle timer if needed
    if( !_idle )
      resetIdleTimer();
  }

  // returns whether or not the client is idle
  public boolean isIdle()
  {
    return idleFlag;
  }

  // flush the local SQLite database to the server
  private void memFlush() throws CountMismatchException
  {

    // Error checking we can remove eventually
    // =======================================================

    int totalCount = 0;
    // Get count of all apps besides other
    for( App a : userApps )
    {
      if( a.getAppID() == 0 )
      {
        continue;
      }
      totalCount += a.getCount();
    }

    int otherCount = userApps.get(OTHER_APP_INDEX).getCount();
    if( otherCount != (Main.settings.memory_flush_interval - totalCount) )
    {
      if( Main.settings.debug )
        System.out.println("Something weird happened "
            + "where other count isn't what it should be");

      throw new CountMismatchException("'Other App' poll count does not"
          + "match the remainder of time not spent on other apps.");
    }

    // =======================================================

    SQLite.flushMemory(userApps);
    for( App a : userApps )
      a.clear();

    // Print debug text
    if( Main.settings.debug )
      System.out.println("Flushed to local.");
  }

  // flush the local SQLite database to the server
  private void localFlush()
  {

    SQLite.pushToRemote();

    // Print debug text
    if( Main.settings.debug )
      System.out.println("Flushed to server.");

    // clear the SQLite DB
    SQLite.clearStats();

    // Print debug text
    if( Main.settings.debug )
      System.out.println("Cleared SQLite Database.");
  }
}
