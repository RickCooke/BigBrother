package bigBrother.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import bigBrother.gui.*;

public class mainFile
{
  
  static BigBrotherLoginGUI loginGui;
  static BigBrotherAdminGUI adminGui;
  static String[] loginInfo = new String[2];
  public static void main( String[] args)
  {
    loginGui = new BigBrotherLoginGUI();
    adminGui = new BigBrotherAdminGUI();
    loginGui.pack();
    loginGui.setVisible(true);
    loginGui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
    loginGui.okButton.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        // TODO Auto-generated method stub
        loginInfo[0] = loginGui.usernameTF.getText();
        loginInfo[1] = new String(loginGui.passwordTF.getPassword());
        if(loginGui.authenticate(loginInfo[0], loginInfo[1]))
        {
          loginGui.setVisible(false);
          adminGui.setVisible(true);
        }
        else
        {
          JOptionPane.showMessageDialog(loginGui, "Wrong username and password",
              "Login Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }
};