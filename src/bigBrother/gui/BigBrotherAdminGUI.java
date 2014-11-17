package bigBrother.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    monitorAppButton.addActionListener(newAppButtonAL);
    
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

  ActionListener newAppButtonAL = new ActionListener()
  {

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
      // TODO Auto-generated method stub
      JFrame newAppFrame = new JFrame();
      newAppFrame.setLayout(new BorderLayout());
      
      JPanel northPanel = new JPanel();
      JPanel centerPanel = new JPanel();
      JPanel centerTopPanel = new JPanel();
      JPanel centerBottomPanel = new JPanel();
      JPanel southPanel = new JPanel();
      JPanel southTopPanel = new JPanel();
      JPanel southBottomPanel = new JPanel();
      
      northPanel.setLayout(new BorderLayout());
      centerPanel.setLayout(new GridLayout(1, 2));
      centerTopPanel.setLayout(new BorderLayout());
      centerBottomPanel.setLayout(new FlowLayout());
      southPanel.setLayout(new GridLayout(1, 2));
      southTopPanel.setLayout(new BorderLayout());
      southBottomPanel.setLayout(new FlowLayout());
      
      JLabel aliasNameLabel = new JLabel("Alias Name:");
      JLabel windowNameLabel = new JLabel("Window Name:");
      JLabel processNameLabel = new JLabel("Process Name:");
      JLabel windowRegexLabel = new JLabel("Window is Regex?");
      JLabel processRegexLabel = new JLabel("Process is Regex?");
      
      JTextField aliasTF = new JTextField(20);
      JTextField windowTF = new JTextField(20);
      JTextField processTF = new JTextField(20);
      
      JCheckBox windowIsRegex = new JCheckBox();
      JCheckBox processIsRegex = new JCheckBox();
      
      northPanel.add(aliasNameLabel, BorderLayout.WEST);
      northPanel.add(aliasTF, BorderLayout.EAST);
      
      centerTopPanel.add(windowNameLabel, BorderLayout.WEST);
      centerTopPanel.add(windowTF, BorderLayout.EAST);
      centerBottomPanel.add(windowIsRegex);
      centerBottomPanel.add(windowRegexLabel);
      
      
      southTopPanel.add(processNameLabel, BorderLayout.WEST);
      southTopPanel.add(processTF, BorderLayout.EAST);
      southBottomPanel.add(processIsRegex);
      southBottomPanel.add(processRegexLabel);
      
      
      centerPanel.add(centerTopPanel);
      centerPanel.add(centerBottomPanel);
      southPanel.add(southTopPanel);
      southPanel.add(southBottomPanel);
      
      newAppFrame.add(northPanel, BorderLayout.NORTH);
      newAppFrame.add(centerPanel, BorderLayout.CENTER);
      newAppFrame.add(southPanel, BorderLayout.SOUTH);
      
      newAppFrame.pack();
      newAppFrame.setVisible(true);
      
    }
      
  };
};
