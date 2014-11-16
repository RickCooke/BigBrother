package bigBrother.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

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
    usersScrollPane.setPreferredSize(new Dimension(150, 400));
    JButton createUserButton = new JButton("New User");
    createUserButton.setAlignmentX(CENTER_ALIGNMENT);
    
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
    appsScrollPane.setPreferredSize(new Dimension(200, 400));
    JButton monitorAppButton = new JButton("Monitor New Application");
    monitorAppButton.setAlignmentX(CENTER_ALIGNMENT);
    
    appsPanel.add(appsLabel);
    appsPanel.add(appsScrollPane);
    appsPanel.add(monitorAppButton);
    appsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
    mainPanel.add(appsPanel);
    add(mainPanel, BorderLayout.CENTER);
    pack();
  }
  
  
  ActionListener newUserButtonAL = new ActionListener()
  {
    
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
      // TODO Auto-generated method stub
      JPanel newUserPanel = new JPanel();
      JLabel firstNameLabel = new JLabel("First Name: ");
      JLabel lastNameLabel = new JLabel("Last Name: ");
      JLabel usernameLabel = new JLabel("Username: ");
      JLabel passwordLabel = new JLabel("Password: ");
      JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
      JLabel groupNumberLabel = new JLabel("Group Number: ");
      
      JTextField firstNameTF = new JTextField(20);
      JTextField lastNameTF = new JTextField(20);
      JTextField usernameNameTF = new JTextField(20);
      JTextField passwordTF = new JPasswordField(20);
      JTextField confirmPasswordTF = new JPasswordField(20);
      JTextField groupNumberTF = new JTextField(20);

      JPanel firstNamePanel = new JPanel();
      
      newUserPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
  };
};