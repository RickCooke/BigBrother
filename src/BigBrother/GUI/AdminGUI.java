package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

import BigBrother.Classes.AppLite;
import BigBrother.Classes.UserLite;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.UnknownSelectTypeException;

@SuppressWarnings("serial")
public class AdminGUI extends JFrame {

    private final static DefaultListModel<UserLite> usersDLM = new DefaultListModel<UserLite>();
    private final static DefaultListModel<AppLite> trackedAppsDLM = new DefaultListModel<AppLite>();
    private final static DefaultListModel<AppLite> nonTrackedAppsDLM = new DefaultListModel<AppLite>();
    private final JList<UserLite> usersList = new JList<UserLite>(usersDLM);
    private final JList<AppLite> trackedAppsList = new JList<AppLite>(trackedAppsDLM);
    private final JList<AppLite> nonTrackedAppsList = new JList<AppLite>(nonTrackedAppsDLM);

    public AdminGUI() {
        super("BigBrother Administration");

        if (Main.settings.debug)
            System.out.println("Admin GUI Initialized.");



        // Set the layout
        setLayout(new BorderLayout());

        // Setup the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        // Add File Item Menus
        JMenuItem logoutMenu = new JMenuItem("Logout");
        logoutMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Main.logout();
            }
        });
        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO: Should we exit the admin GUI only or the client too?

                // close the window
                closeWindow();

                // enable if we're killing the whole program
                // Client.destroy();
            }
        });

        fileMenu.add(logoutMenu);
        fileMenu.add(exitMenu);

        // Add Edit Item Menus
        JMenuItem settingsMenu = new JMenuItem("Settings");
        settingsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                openSettingsGUI();
            }
        });
        
        JMenuItem addUserMenu = new JMenuItem("Add User");
        addUserMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showUserGUI();
            }
        });
        
        JMenuItem editUserMenu = new JMenuItem("Edit User");
        editUserMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    SingleSelectGUI win = new SingleSelectGUI(0);
                    win.pack();
                    win.setVisible(true);
                } catch (UnknownSelectTypeException e) {
                    // This should never occur
                    e.printStackTrace();
                }
            }
        });
        
        JMenuItem addAppMenu = new JMenuItem("Add Application");
        addAppMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showAppGUI();
            }
        });
        
        JMenuItem editAppMenu = new JMenuItem("Edit Application");
        editAppMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    SingleSelectGUI win = new SingleSelectGUI(1);
                    win.pack();
                    win.setVisible(true);
                } catch (UnknownSelectTypeException e) {
                    // This should never occur
                    e.printStackTrace();
                }
            }
        });

        editMenu.add(settingsMenu);
        editMenu.add(addUserMenu);
        editMenu.add(editUserMenu);
        editMenu.add(addAppMenu);
        editMenu.add(editAppMenu);

        // add menubar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        // Users section
        JLabel usersLabel = new JLabel("Users");
        usersLabel.setAlignmentX(CENTER_ALIGNMENT);
        usersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                updateApps(usersList.getSelectedValue().getID());
            }
        });
        JScrollPane usersScrollPane = new JScrollPane(usersList);
        usersScrollPane.setPreferredSize(new Dimension(150, 400));
        JButton createUserButton = new JButton("New User");
        createUserButton.setAlignmentX(CENTER_ALIGNMENT);
        createUserButton.addActionListener(newUserButtonAL);

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.add(usersLabel);
        usersPanel.add(usersScrollPane);
        usersPanel.add(createUserButton);
        usersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));


        // Monitored apps section
        JLabel appsLabel = new JLabel("Monitored Applications");
        appsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JScrollPane appsScrollPane = new JScrollPane(trackedAppsList);
        appsScrollPane.setPreferredSize(new Dimension(200, 400));
        JButton monitorAppButton = new JButton("Monitor New Application");
        monitorAppButton.setAlignmentX(CENTER_ALIGNMENT);
        monitorAppButton.addActionListener(newAppButtonAL);

        JPanel trackedAppsPanel = new JPanel();
        trackedAppsPanel.setLayout(new BoxLayout(trackedAppsPanel, BoxLayout.Y_AXIS));
        trackedAppsPanel.add(appsLabel);
        trackedAppsPanel.add(appsScrollPane);
        trackedAppsPanel.add(monitorAppButton);
        
        // Add buttons
        JPanel appButtonsPanel = new JPanel();
        appButtonsPanel.setLayout(new GridLayout(2,1));
        JButton trackButton = new JButton("<");
        JButton untrackButton = new JButton(">");
        appButtonsPanel.add(trackButton);
        appButtonsPanel.add(untrackButton);
        appButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        

        // Non-Monitored apps section
        JLabel nonTrackedAppsLabel = new JLabel("Non-Monitored Applications");
        nonTrackedAppsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JScrollPane nonTrackedAppsScrollPane = new JScrollPane(nonTrackedAppsList);
        nonTrackedAppsScrollPane.setPreferredSize(new Dimension(200, 400));

        JPanel nonTrackedAppsPanel = new JPanel();
        nonTrackedAppsPanel.setLayout(new BoxLayout(nonTrackedAppsPanel, BoxLayout.Y_AXIS));
        nonTrackedAppsPanel.add(nonTrackedAppsLabel);
        nonTrackedAppsPanel.add(nonTrackedAppsScrollPane);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(usersPanel);
        mainPanel.add(trackedAppsPanel);
        mainPanel.add(appButtonsPanel);
        mainPanel.add(nonTrackedAppsPanel);

        add(mainPanel, BorderLayout.CENTER);

        // update the users list
        updateUsers();
        
        //set button listeners
        trackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setAppTracked(true);
            }
        });
        untrackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setAppTracked(false);
            }
        });
    }


    ActionListener newUserButtonAL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            showUserGUI();
        }
    };

    ActionListener newAppButtonAL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            showAppGUI();
        }
    };
    
    private void showUserGUI(){
        UserGUI win = new UserGUI();
        win.pack();
        win.setVisible(true);
    }

    private void showAppGUI() {
        AppGUI win = new AppGUI();
        win.pack();
        win.setSize(new Dimension(421, 166));
        win.setMinimumSize(new Dimension(421, 166));
        win.setVisible(true);
    }

    static void updateUsers() {
        // debug output
        if (Main.settings.debug)
            System.out.println("Updating user list...");


        // update the user list
        MySQL.getUserList(usersDLM);
    }

    public static void updateApps(int userID) {
        // debug output
        if (Main.settings.debug)
            System.out.println("Updating tracked app list for user #" + userID + "...");


        // update the tracked app list
        MySQL.updateTrackedAppDLM(userID, trackedAppsDLM);
        MySQL.updateNonTrackedAppDLM(userID, nonTrackedAppsDLM);
    }

    private void openSettingsGUI() {
        SettingsGUI win = new SettingsGUI();
        win.pack();
        win.setVisible(true);
    }

    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }
    
    private void setAppTracked(boolean tracked) {
    	if(usersList.getSelectedValue() == null)
    		return;
    	
    	int userID = usersList.getSelectedValue().getID();
    	int appID = -1;
	    	if(nonTrackedAppsList.getSelectedValue() != null && tracked)
	    		appID = nonTrackedAppsList.getSelectedValue().getID();
	    	else if(trackedAppsList.getSelectedValue() != null && !tracked)
	    		appID = trackedAppsList.getSelectedValue().getID();
    	
	    if(appID == -1)
	    	return;
	    
    	MySQL.setAppTracked(userID, appID, tracked);
    	
    	//update the apps list since it just changed
    	updateApps(userID);
    }
};
