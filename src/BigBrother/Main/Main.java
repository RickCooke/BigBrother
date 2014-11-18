package BigBrother.Main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.WindowConstants;

import WindowsAPI.Keyboard;
import BigBrother.GUI.AdminGUI;
import BigBrother.GUI.LoginGUI;


public class Main {
    public final static boolean debug = true; // Set this to enable debug output

    public static String MySQL_host = "23.94.98.164";
    public static String MySQL_database = "bigbrother";
    public static String MySQL_username = "bigbrother";
    public static String MySQL_password = "plzletmein";
    
    public static int loggedInUserID;
    public static long polling_interval;
    public static int memory_flush_interval;
    public static int local_flush_interval;
    public static int max_idle_time;
    public static int block_time;
    public static int start_time;
    public static int keyboard_peek_interval = 10;
    
    public static LoginGUI win;
    
    public static void main(String[] args) {
        setupTrayIcon();
        
        
        win = new LoginGUI();
        win.setMinimumSize(new Dimension(200, 100));
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        MySQL.establishConnection();
        SQLite.establishConnection();
        
    }
    
    private static void setupTrayIcon() {
        
        PopupMenu popMenu = new PopupMenu();
        MenuItem adminMenuItem = new MenuItem("Admin");
        MenuItem exitMenuItem = new MenuItem("Exit");
        popMenu.add(adminMenuItem);
        popMenu.add(exitMenuItem);

        adminMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.setVisible(true);
            }
        });
        
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        Image img = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/spy.png"));
        TrayIcon trayIcon = new TrayIcon(img, "Big Brother", popMenu);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
    }
}
