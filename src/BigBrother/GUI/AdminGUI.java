package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

import com.toedter.calendar.JDateChooser;

import BigBrother.Classes.AppLite;
import BigBrother.Classes.UserLite;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.MultipleResultsFoundException;
import BigBrother.Exceptions.NoResultsFoundException;
import BigBrother.Exceptions.UnknownSelectTypeException;

import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

import java.awt.Color;

import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class AdminGUI extends JFrame {

    private final static DefaultListModel<UserLite> usersDLM = new DefaultListModel<UserLite>();
    private final static DefaultListModel<AppLite> trackedAppsDLM = new DefaultListModel<AppLite>();
    private final static DefaultListModel<AppLite> nonTrackedAppsDLM = new DefaultListModel<AppLite>();
    private final static JList<UserLite> usersList = new JList<UserLite>(usersDLM);
    private final static JList<AppLite> trackedAppsList = new JList<AppLite>(trackedAppsDLM);
    private final static JList<AppLite> nonTrackedAppsList = new JList<AppLite>(nonTrackedAppsDLM);

    public AdminGUI() {
        super("BigBrother Administration");

        if (Main.settings.debug)
            System.out.println("Admin GUI Initialized.");

        // Set the layout
        getContentPane().setLayout(new BorderLayout());

        // Setup the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");

        // Add File Item Menus
        JMenuItem clearStatsMenu = new JMenuItem("Clear Statistics");
        clearStatsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearStats();
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

        fileMenu.add(clearStatsMenu);
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

        JMenuItem editUserMenu = new JMenuItem("Edit Users");
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

        JMenuItem editAppMenu = new JMenuItem("Edit Applications");
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

        
        // add view menu items
        JMenuItem statsMenu = new JMenuItem("Usage Statistics");
        statsMenu.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            StatsGUI win = new StatsGUI();
            win.pack();
            win.setVisible(true);
          }
        });

        viewMenu.add(statsMenu);
                
        // add menubar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);

        // Users section
        JLabel usersLabel = new JLabel("Users");
        usersLabel.setAlignmentX(CENTER_ALIGNMENT);

        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane usersScrollPane = new JScrollPane(usersList);
        usersScrollPane.setPreferredSize(new Dimension(200, 300));
        JButton createUserButton = new JButton("New User");
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createUserButton.addActionListener(newUserButtonAL);

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.add(usersLabel);
        usersPanel.add(usersScrollPane);
        usersPanel.add(createUserButton);
        usersPanel.setBorder(new EmptyBorder(0, 0, 0, 20));


        // Monitored apps section
        
        trackedAppsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        JLabel appsLabel = new JLabel("Monitored Applications");
        appsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JScrollPane appsScrollPane = new JScrollPane(trackedAppsList);
        appsScrollPane.setPreferredSize(new Dimension(200, 300));

        JPanel trackedAppsPanel = new JPanel();
        trackedAppsPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        trackedAppsPanel.setLayout(new BoxLayout(trackedAppsPanel, BoxLayout.Y_AXIS));
        trackedAppsPanel.add(appsLabel);
        trackedAppsPanel.add(appsScrollPane);


        
        
        // Add buttons
        JPanel appButtonsPanel = new JPanel();
        appButtonsPanel.setLayout(new GridLayout(2,1));
        JButton trackButton = new JButton("<");
        JButton untrackButton = new JButton(">");
        appButtonsPanel.add(trackButton);
        appButtonsPanel.add(untrackButton);
        appButtonsPanel.setBorder(new EmptyBorder(15, 20, 25, 20));
        

        // Non-Monitored apps section
        
        nonTrackedAppsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JLabel nonTrackedAppsLabel = new JLabel("Non-Monitored Applications");
        nonTrackedAppsLabel.setAlignmentX(CENTER_ALIGNMENT);
        JScrollPane nonTrackedAppsScrollPane = new JScrollPane(nonTrackedAppsList);
        nonTrackedAppsScrollPane.setPreferredSize(new Dimension(200, 300));
        JButton newAppButton = new JButton("Create New Application");
        newAppButton.setAlignmentX(CENTER_ALIGNMENT);
        newAppButton.addActionListener(newAppButtonAL);

        JPanel nonTrackedAppsPanel = new JPanel();
        nonTrackedAppsPanel.setLayout(new BoxLayout(nonTrackedAppsPanel, BoxLayout.Y_AXIS));
        nonTrackedAppsPanel.add(nonTrackedAppsLabel);
        nonTrackedAppsPanel.add(nonTrackedAppsScrollPane);
        nonTrackedAppsPanel.add(newAppButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(usersPanel);
        mainPanel.add(trackedAppsPanel);
        mainPanel.add(appButtonsPanel);
        mainPanel.add(nonTrackedAppsPanel);

        getContentPane().add(mainPanel, BorderLayout.CENTER);

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
        
        
        createRightClickMenus();
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

    private void showUserGUI() {
        UserGUI win = new UserGUI();
    }

    private void showUserGUI(int userID) {
        UserGUI win = null;
        try {
            win = new UserGUI(userID);
        } catch (MultipleResultsFoundException | NoResultsFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showAppGUI() {
        AppGUI win = new AppGUI();
    }
  
    private void showAppGUI(int appID) {
        try {
            AppGUI win = new AppGUI(appID);
        } catch (MultipleResultsFoundException | NoResultsFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    static void deleteUser(int userID) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            MySQL.deleteUser(userID);
            updateUsers();
        }
    }

    public static void deleteApp(int appID) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "This will mark the application as inactive and untrack it from all users\nAre you sure you want to delete?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            MySQL.setAppInactive(appID);
            MySQL.deleteAppTrackFromAll(appID);
            updateApps();
            SingleSelectGUI.updateList();
        }
    }


    static void updateUsers() {
        // debug output
        if (Main.settings.debug)
            System.out.println("Updating user list...");


        // update the user list
        MySQL.getUserList(usersDLM);
        SingleSelectGUI.updateList();
    }

    public static void updateApps() {
    	if(usersList.getSelectedValue() != null) {
    		updateApps(usersList.getSelectedValue().getID());
    	}
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
    
    static boolean clearStats() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Clearing statistics will erase all usage statistics permanently\nDo you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            MySQL.clearStats();
            JOptionPane.showMessageDialog(null, "Statistics were successfully cleared", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
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

    public static JPanel buildDatePanel(Date in_date) {
        JPanel datePanel = new JPanel();

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(in_date);
        dateChooser.setDateFormatString("MM/dd/YY");
        dateChooser.setSize(new Dimension(150, 0));

        datePanel.add(dateChooser);


        SpinnerDateModel model = new SpinnerDateModel();
        model.setCalendarField(Calendar.MINUTE);
        JSpinner timeSpinner = new JSpinner(model);
        timeSpinner.setValue(in_date); 
        JComponent editor = new JSpinner.DateEditor(timeSpinner, "hh:mm:ss a");
        timeSpinner.setEditor(editor);

        datePanel.add(timeSpinner);

        return datePanel;
    }
    
    private void createRightClickMenus() {


        final JPopupMenu userPopupMenu = new JPopupMenu();
        JMenuItem UserMenuEdit = new JMenuItem("Edit");
        JMenuItem UserMenuDelete = new JMenuItem("Delete");

        userPopupMenu.add(UserMenuEdit);
        userPopupMenu.add(new JPopupMenu.Separator());
        userPopupMenu.add(UserMenuDelete);


        UserMenuEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = usersList.getSelectedValue().getID();
                showUserGUI(id);
            }
        });

        UserMenuDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = usersList.getSelectedValue().getID();
                deleteUser(id);
            }
        });
        
        usersList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = usersList.locationToIndex(e.getPoint());
                    usersList.setSelectedIndex(row);
                    userPopupMenu.show(usersList, e.getX(), e.getY());
                }
                updateApps(usersList.getSelectedValue().getID());
            }
        });

        
        final JPopupMenu trackedAppsPopupMenu = new JPopupMenu();
        JMenuItem trackedAppsMenuEdit = new JMenuItem("Edit");
        JMenuItem trackedAppsMenuDelete = new JMenuItem("Delete");

        trackedAppsPopupMenu.add(trackedAppsMenuEdit);
        trackedAppsPopupMenu.add(new JPopupMenu.Separator());
        trackedAppsPopupMenu.add(trackedAppsMenuDelete);


        trackedAppsMenuEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = trackedAppsList.getSelectedValue().getID();
                showAppGUI(id);
            }
        });

        trackedAppsMenuDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = trackedAppsList.getSelectedValue().getID();
                deleteApp(id);
            }
        });



        trackedAppsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = trackedAppsList.locationToIndex(e.getPoint());
                    trackedAppsList.setSelectedIndex(row);
                    trackedAppsPopupMenu.show(trackedAppsList, e.getX(), e.getY());
                }
            }
        });
        
        
        
        final JPopupMenu nonTrackedAppsPopupMenu = new JPopupMenu();
        JMenuItem nonTrackedAppsMenuEdit = new JMenuItem("Edit");
        JMenuItem nonTrackedAppsMenuDelete = new JMenuItem("Delete");

        nonTrackedAppsPopupMenu.add(nonTrackedAppsMenuEdit);
        nonTrackedAppsPopupMenu.add(new JPopupMenu.Separator());
        nonTrackedAppsPopupMenu.add(nonTrackedAppsMenuDelete);


        nonTrackedAppsMenuEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = nonTrackedAppsList.getSelectedValue().getID();
                showAppGUI(id);
            }
        });

        nonTrackedAppsMenuDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int id = nonTrackedAppsList.getSelectedValue().getID();
                deleteApp(id);
            }
        });



        nonTrackedAppsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = nonTrackedAppsList.locationToIndex(e.getPoint());
                    nonTrackedAppsList.setSelectedIndex(row);
                    nonTrackedAppsPopupMenu.show(nonTrackedAppsList, e.getX(), e.getY());
                }
            }
        });
    }

};
