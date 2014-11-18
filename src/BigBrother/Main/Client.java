package BigBrother.Main;

import static WindowsAPI.EnumerateWindows.Kernel32.OpenProcess;
import static WindowsAPI.EnumerateWindows.Kernel32.PROCESS_QUERY_INFORMATION;
import static WindowsAPI.EnumerateWindows.Kernel32.PROCESS_VM_READ;
import static WindowsAPI.EnumerateWindows.Psapi.GetModuleBaseNameW;
import static WindowsAPI.EnumerateWindows.User32DLL.GetForegroundWindow;
import static WindowsAPI.EnumerateWindows.User32DLL.GetWindowTextW;
import static WindowsAPI.EnumerateWindows.User32DLL.GetWindowThreadProcessId;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import BigBrother.Classes.App;
import BigBrother.Exceptions.CountMismatchException;
import BigBrother.Exceptions.KeyboardHookFailed;
import BigBrother.Exceptions.NoSettingsException;
import BigBrother.Exceptions.RequiredAppsNotFoundException;
import BigBrother.GUI.AdminGUI;
import BigBrother.GUI.LoginGUI;
import WindowsAPI.KeyboardCallback;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.PointerByReference;


public class Client {

    private int OTHER_APP_INDEX = -1;
    private int IDLE_APP_INDEX = -1;

    private int pollNum = 0;
    private Point lastKnownMouseLocation;
    private static Timer idleTimer;
    private static TimerTask idleTimerTask;
    private Timer pollTimer;
    private static boolean idleFlag;
    private ArrayList<App> userApps;
    
    private static KeyboardCallback Keyboard  = new KeyboardCallback();
    
    public Client() {

        // get our settings
        syncSettings();

        // get our list of apps
        syncApps();
        
        // TODO: actually find the indecies in an efficient way, we shouldn't just gurantee that
        // they're first
        // Figure out and set the indexes of idle and other apps
        try {
            int index = 0;
            for (App a : userApps) {
                if (a.getAlias().equals("Idle"))
                    IDLE_APP_INDEX = index;
                else if (a.getAlias().equals("Other"))
                    OTHER_APP_INDEX = index;
                index++;
            }

            if (OTHER_APP_INDEX == -1)
                throw new RequiredAppsNotFoundException("'Other' App index not found.");
            else if (IDLE_APP_INDEX == -1)
                throw new RequiredAppsNotFoundException("'Idle' App index not found.");
        } catch (RequiredAppsNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // TODO: make it so userApps is sorted based on specificity
        // Note from Michael: we shouldn't need to do this, when adding initially,
        // just add anything with both processName and windowName to the top,
        // and anything with only 1 of them to the bottom

        // TODO: delete this call before release
        // call our test function to set our hardcoded test values
        test();

        // Prints your user apps if debug flag is set
        if (Main.debug)
            for (App a : userApps)
                a.print();

        setIdle(false);

        // init mouse location
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        lastKnownMouseLocation = pointerInfo.getLocation();

        // TODO: Set up the keyboard and mouseClick listeners

        // Set up the timers
        try {
            // Set up the poll timer
            pollTimer = new Timer();
            pollTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    poll();
                }
            }, 1000, Main.polling_interval);

            // Set up the idle timer
            resetIdleTimer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            // TODO: handle exception by using default timers? or prompt the user
            // for valid times/notify user settings are malformed?

            // Exit if timers are not working
            System.exit(1);
        }

        // TODO: delete this? idk?
        openAdminGUI();



        // TODO: is there a better way to keep the process active than just an infinite loop?
        try {
            while (true)
                Thread.sleep(999999999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ends the current session
    private void destroy() {
        // TODO: implement this
    }

    // Query server and set our local settings
    private void syncSettings() {
        // Attempt to set the GLOBAL settings a.k.a Main's variables
        try {
            MySQL.getSettings();
        } catch (NoSettingsException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Query server and set our apps list
    private void syncApps() {
        userApps = new ArrayList<App>();
        userApps = MySQL.getTrackedAppsArrayList(Main.loggedInUserID);
        
        // add default "Other" and "Idle" apps
        // TODO: delete this, these should exist and get pulled from the DB
        
        // Brian(11/18): If we rely on DB then every user must have a user_app pair
        // that adds the apps always, and does not allow them to delete.
        // seems the best way, but a lot more work
        userApps.add(new App(0, "Other", null, false, null, false));
        userApps.add(new App(1, "Idle", null, false, null, false));
    }

    // Function to poll the system for its running apps and add them to memory
    private void poll() {
        // if app list is not initialized, exit
        if (userApps == null || userApps.isEmpty())
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
        if (lastKnownMouseLocation.distance(pointerInfo.getLocation()) > 0) {
            // debug output
            if (Main.debug)
                System.out.println("Mouse movement detected.");

            // set the idle flag to false
            setIdle(false);

            // update the last known mouse position
            lastKnownMouseLocation = pointerInfo.getLocation();
        }

        // only waste computing power checking if the user isn't idle
        if (!idleFlag) {

            // get the Window & Process Names
            GetWindowTextW(WindowsAPI.EnumerateWindows.User32DLL.GetForegroundWindow(), buffer, 1024);
            windowTitle = Native.toString(buffer);
            GetWindowThreadProcessId(GetForegroundWindow(), pointer);
            process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
            GetModuleBaseNameW(process, null, buffer, 1024);
            processName = Native.toString(buffer);

            // Check for app match
            // TODO: make this more efficient, it's poorly optimized right now (Rather than looping
            // through all apps).
            // maybe a map of pointers? I'm not sure, that may take up a large
            // amount of space, as youd need 2 maps, one for window and one for process
            boolean foundMatch = false;
            for (App a : userApps) {


                if (a.isMatch(windowTitle, processName)) {
                    // window was active, record it
                    a.addCount(Main.polling_interval);
                    foundMatch = true;
                    break;
                }
            }

            // if no match was found, then it falls under the "Other" app
            if (!foundMatch)
                userApps.get(OTHER_APP_INDEX).addCount(Main.polling_interval);
        } else {
            // increment idle counter
            userApps.get(IDLE_APP_INDEX).addCount(Main.polling_interval);
        }


        // Debug
        for (App a : userApps)
            if (Main.debug)
                a.print2();

        // If we need to, flush from memory to local SQLite DB
        try {
            if (pollNum % (Main.memory_flush_interval / Main.polling_interval) == 0)
                memFlush();
        } catch (CountMismatchException e) {
            // TODO: handle this better
            e.printStackTrace();
        }

        // if we need to, flush from SQLite DB to server
        if (pollNum % (Main.local_flush_interval / Main.polling_interval) == 0)
            localFlush();
    }


    // resets the idle timer to 0
    public static void resetIdleTimer() {
        // cancel the old timer
        if (idleTimer != null && idleTimerTask != null) {
            idleTimer.cancel();
            idleTimer.purge();
            idleTimerTask.cancel();
        }

        try {
            if(Keyboard.isHooked()) {
                Keyboard.unhook();
            }
        } catch (KeyboardHookFailed e) {
            JOptionPane.showMessageDialog(Main.win, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Set up the idle timer
        idleTimer = new Timer();
        idleTimerTask = new TimerTask() {
            public void run() {
                setIdle(true);
                
                try {
                    if(!Keyboard.isHooked()) {
                        Keyboard.hook();
                    }
                } catch (KeyboardHookFailed e) {
                    JOptionPane.showMessageDialog(Main.win, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                User32.MSG msg = new User32.MSG();
                while (true) {
                    User32.INSTANCE.PeekMessage(msg, null, 0, 0, 0);
                    try {
                        Thread.sleep(Main.keyboard_peek_interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
            }
        };
        
        idleTimer.schedule(idleTimerTask, Main.max_idle_time);

        // Debug text
        if (Main.debug)
            System.out.println("Activity detected. Idle timer has been reset.");
    }

    public static void setIdle(boolean _idle) {
        // Debug text if idle changed
        if (Main.debug && idleFlag != _idle)
            System.out.println("The idle flag is being changed to: " + _idle);

        idleFlag = _idle;

        // reset the idle timer if needed
        if (!_idle)
            resetIdleTimer();
    }

    // returns whether or not the client is idle
    public boolean isIdle() {
        return idleFlag;
    }

    // flush the local SQLite database to the server
    private void memFlush() throws CountMismatchException {

        // Error checking we can remove eventually
        // =======================================================

        int totalCount = 0;
        // Get count of all apps besides other
        for (App a : userApps) {
            if (a.getAppID() == 0) {
                continue;
            }
            totalCount += a.getCount();
        }

        int otherCount = userApps.get(OTHER_APP_INDEX).getCount();
        if (otherCount != (Main.memory_flush_interval - totalCount)) {
            if (Main.debug)
                System.out.println("Something weird happened where other count isn't what it should be");

            // TODO: reenable this, it was freaking out
            // throw new
            // CountMismatchException("'Other App' poll count does not match the remainder of time not spent on other apps.");
        }

        // =======================================================

        SQLite.flushMemory(userApps);
        for (App a : userApps)
            a.clear();

        // Print debug text
        if (Main.debug)
            System.out.println("Flushed to local.");
    }

    // flush the local SQLite database to the server
    private void localFlush() {

        // TODO Push from Local to Server DB

        // Print debug text
        if (Main.debug)
            System.out.println("Flushed to server.");

        // clear the SQLite DB
        SQLite.clearStats();

        // Print debug text
        if (Main.debug)
            System.out.println("Cleared SQLite Database.");
    }

    // opens the login GUI
    public void openLoginGUI() {
        // open the login GUI
        LoginGUI win = new LoginGUI();
        win.setMinimumSize(new Dimension(200, 100));
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // kill the current session
        destroy();
    }

    // opens the admin GUI
    public void openAdminGUI() {
        // open the admin GUI
        AdminGUI win = new AdminGUI();
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    // TODO: delete this
    // test function to directly set program values, delete before release
    private void test() {
        Main.memory_flush_interval = 10 * 1000;
        Main.local_flush_interval = 60 * 1000;
        Main.max_idle_time = 20 * 1000;
    }
}
