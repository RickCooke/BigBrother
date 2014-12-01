package BigBrother.Client;

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

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import BigBrother.Classes.Settings;
import BigBrother.GUI.AdminGUI;
import BigBrother.GUI.LoginGUI;


public class Main {

    public static Settings settings;

    public static int loggedInUserID;

    public static JFrame win;

    public static void main(String[] args) {

        settings = new Settings();

        // connect to the SQL server
        MySQL.establishConnection();
        SQLite.establishConnection();

        // download the Main.settings from the server
        settings.downloadSettings();

        if (settings.debug)
            System.out.println(settings.toString());

        // start the tray icon
        setupTrayIcon();

        // load up the login GUI
        startLoginGUI();

    }

    public static void setupTrayIcon() {

        PopupMenu popMenu = new PopupMenu();
        MenuItem exitMenuItem = new MenuItem("Exit");
        popMenu.add(exitMenuItem);

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        if (SystemTray.isSupported()) {
            Image img = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/BigBrother/Client/assets/spy16.png"));
            TrayIcon trayIcon = new TrayIcon(img, "Big Brother", popMenu);
            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logout() {
        // reset everything
        settings = new Settings();

        // download the Main.settings from the server
        settings.downloadSettings();

        if (settings.debug)
            System.out.println(settings.toString());

        // load up the login GUI
        startLoginGUI();
    }

    private static void startLoginGUI() {
        win = new LoginGUI();
        win.setMinimumSize(new Dimension(200, 100));
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private static void startAdminGUI() {
        win = new AdminGUI();
        win.setMinimumSize(new Dimension(200, 100));
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
