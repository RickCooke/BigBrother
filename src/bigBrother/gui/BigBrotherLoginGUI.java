package bigBrother.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class BigBrotherLoginGUI extends JFrame
{
  public JTextField usernameTF;
  public JPasswordField passwordTF;
  public JButton okButton;
  boolean loginSuccessful = false;
  String[] loginInfo = new String[2];
  public BigBrotherLoginGUI()
  {
    super("Login");
    setLayout(new BorderLayout());
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JPanel usernamePanel = new JPanel();
    JPanel passwordPanel = new JPanel();
    JPanel controlPanel = new JPanel();
    usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
    passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
    
    JLabel usernameLabel = new JLabel("Username: ");
    usernameTF = new JTextField(15);
    usernamePanel.add(usernameLabel);
    usernamePanel.add(usernameTF);
    usernamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    
    JLabel passwordLabel = new JLabel("Password: ");
    passwordTF = new JPasswordField(15);
    passwordPanel.add(passwordLabel);
    passwordPanel.add(passwordTF);
    passwordPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
    
    okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    controlPanel.add(okButton);
    controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
    controlPanel.add(cancelButton);
    controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 20 , 20, 20));
    
    mainPanel.add(usernamePanel);
    mainPanel.add(passwordPanel);
    mainPanel.add(controlPanel);
    add(mainPanel);
    
    cancelButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        // TODO Auto-generated method stub
        System.exit(0);
      }
    });
  }
  public boolean authenticate(String username, String password)
  {
    if(username.equals("Richard") && password.equals("password"))
    {
      JOptionPane.showMessageDialog(this, "Welcome");
      return true;
    }
    else
      return false;
  }
  
  public boolean getLoginSuccess()
  {
    return loginSuccessful;
  }
  
}
