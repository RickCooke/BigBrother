package BigBrother.GUI;

import java.awt.*;

import javax.swing.*;

import BigBrother.Classes.UserLite;
import BigBrother.Client.MySQL;

public class UserStatsGUI extends JFrame {
  
  private DefaultListModel<UserLite> usersDLM 
    = new DefaultListModel<UserLite>();

  public UserStatsGUI() {
    super("User Stats");
    
    setLayout(new BorderLayout());
    
    JPanel westPanel = new JPanel(new BorderLayout());
    JPanel eastPanel = new JPanel(new FlowLayout());
    
    // west panel
    MySQL.getUserList(usersDLM);
    JLabel usersLabel = new JLabel("Select Users", JLabel.CENTER);
    usersLabel.setAlignmentX(CENTER_ALIGNMENT);
    JList<UserLite> userList = new JList<UserLite>(usersDLM);
    JScrollPane userScrollPane = new JScrollPane(userList);
    userScrollPane.setPreferredSize(new Dimension(200, 200));
    userScrollPane.setAlignmentX(CENTER_ALIGNMENT);
    
    westPanel.add(usersLabel, BorderLayout.NORTH);
    westPanel.add(userScrollPane, BorderLayout.SOUTH);
    westPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    
    // east panel
    JLabel viewLabel = new JLabel("Past 8 Hours", JLabel.CENTER);
    JButton viewStatsBtn = new JButton("View Stats");
    
    eastPanel.add(viewLabel, BorderLayout.CENTER);
    eastPanel.add(viewStatsBtn, BorderLayout.SOUTH);
    eastPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    add(westPanel, BorderLayout.WEST);
    add(eastPanel, BorderLayout.EAST);
    
    
  }
}