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

import javax.swing.WindowConstants;

import BigBrother.Classes.Settings;
import BigBrother.GUI.LoginGUI;


public class Main {

	public static Settings settings;
	
    public static int loggedInUserID;
    
    public static LoginGUI win;
    
    public static void main(String[] args) {

        settings = new Settings();
        
        if(settings.debug)
        	System.out.println(settings.toString());
        
        //connect to the SQL server
        MySQL.establishConnection();
        SQLite.establishConnection();
        
        //download the Main.settings from the server
        settings.downloadSettings();
        
    	//start the tray icon
        setupTrayIcon();
        
        //load up the login GUI
        win = new LoginGUI();
        win.setMinimumSize(new Dimension(200, 100));
        win.pack();
        win.setVisible(true);
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
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


        Image img = Toolkit.getDefaultToolkit().getImage(Main.class.
            getResource("/BigBrother/Client/assets/spy16.png"));
        TrayIcon trayIcon = new TrayIcon(img, "Big Brother", popMenu);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
    }
}
