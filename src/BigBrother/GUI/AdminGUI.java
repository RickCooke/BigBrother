package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import BigBrother.Classes.AppLite;
import BigBrother.Classes.UserLite;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.UnknownSelectTypeException;

/* Comment here to see if I have write access to the repo */

@SuppressWarnings("serial")
public class AdminGUI extends JFrame {

    private final DefaultListModel<UserLite> usersDLM 
    = new DefaultListModel<UserLite>();
    private final DefaultListModel<AppLite> appsDLM 
    = new DefaultListModel<AppLite>();
    private final JList<UserLite> usersList = new JList<UserLite>(usersDLM);
    private final JList<AppLite> appsList = new JList<AppLite>(appsDLM);

    public AdminGUI() {
        super("BigBrother Administration");

        if (Main.settings.debug)
            System.out.println("Admin GUI Initialized.");

        // Set the layout
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        JPanel usersPanel = new JPanel();
        JPanel appsPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        appsPanel.setLayout(new BoxLayout(appsPanel, BoxLayout.Y_AXIS));

        //Setup the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        
        //Add File Item Menus
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
            	//TODO: implement this
            	//(Is this exiting the admin GUI only or the client too?)
            }
        });
        
        fileMenu.add(logoutMenu);
        fileMenu.add(exitMenu);

        //Add Edit Item Menus
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
            	UserGUI win = new UserGUI();
	            win.pack();
	            win.setVisible(true);
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
            	AppGUI win = new AppGUI();
	            win.pack();
	            win.setVisible(true);
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

        //add menubar
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

        usersPanel.add(usersLabel);
        usersPanel.add(usersScrollPane);
        usersPanel.add(createUserButton);
        usersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        mainPanel.add(usersPanel);

        // Monitored apps section
        JLabel appsLabel = new JLabel("Monitored Applications");
        appsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JScrollPane appsScrollPane = new JScrollPane(appsList);
        appsScrollPane.setPreferredSize(new Dimension(200, 400));
        JButton monitorAppButton = new JButton("Monitor New Application");
        monitorAppButton.setAlignmentX(CENTER_ALIGNMENT);
        monitorAppButton.addActionListener(newAppButtonAL);

        appsPanel.add(appsLabel);
        appsPanel.add(appsScrollPane);
        appsPanel.add(monitorAppButton);
        appsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        mainPanel.add(appsPanel);

        add(mainPanel, BorderLayout.CENTER);

        // update the users list
        updateUsers();
    }


    ActionListener newUserButtonAL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            UserGUI win = new UserGUI();
            win.pack();
            win.setVisible(true);
        }
    };

    ActionListener newAppButtonAL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            AppGUI win = new AppGUI();
            win.pack();
            win.setVisible(true);
        }
    };

    private void updateUsers() {
        // debug output
        if (Main.settings.debug)
            System.out.println("Updating user list...");


        // update the user list
        MySQL.getUserList(usersDLM);
    }

    private void updateApps(int userID) {
        // debug output
        if (Main.settings.debug)
            System.out.println("Updating tracked app list for user #" 
        + userID + "...");


        // update the tracked app list
        MySQL.getTrackedAppsDLM(userID, appsDLM);
    }
    
    private void openSettingsGUI() {
    	SettingsGUI win = new SettingsGUI();
        win.pack();
        win.setVisible(true);
    }
};
