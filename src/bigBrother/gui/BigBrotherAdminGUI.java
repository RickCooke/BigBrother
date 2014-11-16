package bigBrother.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

/* Comment here to see if I have write access to the repo */

@SuppressWarnings("serial")
public class BigBrotherAdminGUI extends JFrame
{
  public BigBrotherAdminGUI()
  {
    setLayout(new BorderLayout());
    JPanel mainPanel = new JPanel();
    JPanel usersPanel = new JPanel();
    JPanel appsPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
    usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
    appsPanel.setLayout(new BoxLayout(appsPanel, BoxLayout.Y_AXIS));
    
    JLabel usersLabel = new JLabel("Users");
    usersLabel.setAlignmentX(CENTER_ALIGNMENT);
    DefaultListModel<String> usersDLM = new DefaultListModel<String>();
    JList<String> usersList = new JList<String>(usersDLM);
    JScrollPane usersScrollPane = new JScrollPane(usersList);
    usersScrollPane.setPreferredSize(new Dimension(100, 400));
    JButton createUserButton = new JButton("New User");
    
    usersPanel.add(usersLabel);
    usersPanel.add(usersScrollPane);
    usersPanel.add(createUserButton);
    usersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
    
    mainPanel.add(usersPanel);
    
    JLabel appsLabel = new JLabel("Monitored Applications");
    appsLabel.setAlignmentX(CENTER_ALIGNMENT);
    DefaultListModel<String> appsDLM = new DefaultListModel<String>();
    JList<String> appsList = new JList<String>(appsDLM);
    JScrollPane appsScrollPane = new JScrollPane(appsList);
    appsScrollPane.setPreferredSize(new Dimension(100, 400));
    JButton monitorAppButton = new JButton("Monitor New Application");
    
    appsPanel.add(appsLabel);
    appsPanel.add(appsScrollPane);
    appsPanel.add(monitorAppButton);
    appsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
    mainPanel.add(appsPanel);
    add(mainPanel, BorderLayout.CENTER);
    pack();
  }
  
};