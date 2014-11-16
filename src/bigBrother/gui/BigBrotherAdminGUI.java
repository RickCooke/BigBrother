package bigBrother.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import bigBrother.main.EmptyTFException;
import bigBrother.main.mismatchedPasswordException;

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
    createUserButton.addActionListener(newUserButtonAL);
    
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
      final JFrame newUserFrame = new JFrame("New User");
      JPanel newUserPanelLeft = new JPanel();
      newUserPanelLeft.setLayout(new BoxLayout(newUserPanelLeft, BoxLayout.Y_AXIS));
      JPanel newUserPanelRight = new JPanel();
      newUserPanelRight.setLayout(new BoxLayout(newUserPanelRight, BoxLayout.Y_AXIS));
      JLabel firstNameLabel = new JLabel("First Name: ");
      JLabel lastNameLabel = new JLabel("Last Name: ");
      JLabel usernameLabel = new JLabel("Username: ");
      JLabel passwordLabel = new JLabel("Password: ");
      JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
      JLabel groupNumberLabel = new JLabel("Group Number: ");
      
      final JTextField firstNameTF = new JTextField(20);
      final JTextField lastNameTF = new JTextField(20);
      final JTextField usernameTF = new JTextField(20);
      final JTextField passwordTF = new JPasswordField(20);
      final JTextField confirmPasswordTF = new JPasswordField(20);
      final JTextField groupNumberTF = new JTextField(20);
      
      JButton OKButton = new JButton("OK");
      JButton cancelButton = new JButton("Cancel");
      

      newUserPanelLeft.add(firstNameLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(firstNameTF);
      
      newUserPanelLeft.add(lastNameLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(lastNameTF);
      
      newUserPanelLeft.add(usernameLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(usernameTF);
      
      newUserPanelLeft.add(passwordLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(passwordTF);
      
      newUserPanelLeft.add(confirmPasswordLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(confirmPasswordTF);
      
      newUserPanelLeft.add(groupNumberLabel);
      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
      newUserPanelRight.add(groupNumberTF);

      newUserPanelLeft.add(Box.createRigidArea(new Dimension(0, 10)));
      newUserPanelRight.add(Box.createRigidArea(new Dimension(0, 15)));
      newUserPanelLeft.add(OKButton);
      newUserPanelRight.add(cancelButton);
      cancelButton.setAlignmentX(CENTER_ALIGNMENT);
      
      newUserFrame.setLayout(new BorderLayout());
      newUserPanelLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      newUserPanelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      newUserFrame.add(newUserPanelLeft, BorderLayout.LINE_START);
      newUserFrame.add(newUserPanelRight, BorderLayout.LINE_END);
      newUserFrame.pack();
      
      cancelButton.addActionListener(new ActionListener()
      {
        
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
          // TODO Auto-generated method stub
          newUserFrame.setVisible(false);
          newUserFrame.dispose();
        }
      });
      
      OKButton.addActionListener(new ActionListener()
      {
        
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
          // TODO Auto-generated method stub
          try
          {
            if(firstNameTF.getText().equals("") |
                lastNameTF.getText().equals("") |
                usernameTF.getText().equals("") |
                passwordTF.getText().equals("") |
                confirmPasswordTF.getText().equals(""))
            {
              throw new EmptyTFException();
            }
            else if(!passwordTF.getText().equals(confirmPasswordTF.getText()))
            {
              throw new mismatchedPasswordException();
            }
            else
            {
              newUserFrame.setVisible(false);
              newUserFrame.dispose();
            }
          }
          catch (EmptyTFException e)
          {
           JOptionPane.showMessageDialog(newUserFrame, 
               "All Text Fields Except Group Number Must Be Filled!", 
               "Error", JOptionPane.ERROR_MESSAGE); 
          }
          catch( mismatchedPasswordException e )
          {
            JOptionPane.showMessageDialog(newUserFrame, 
                "Passwords Do Not Match!", 
                "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      });
      newUserFrame.setVisible(true);
    }
  };
};
