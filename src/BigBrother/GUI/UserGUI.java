package BigBrother.GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import BigBrother.Classes.User;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.DuplicateKeyException;
import BigBrother.Exceptions.FormException;
import BigBrother.Exceptions.MultipleResultsFoundException;
import BigBrother.Exceptions.NoResultsFoundException;

public class UserGUI extends JFrame
{

  private boolean isExistingUser = false;
  private int userID = -1;

  private final JTextField usernameTF = new JTextField(20);
  private final JTextField passwordTF = new JPasswordField(20);
  private final JTextField confirmPasswordTF = new JPasswordField(20);
  private final JTextField firstNameTF = new JTextField(20);
  private final JTextField lastNameTF = new JTextField(20);
  private final JTextField groupNumberTF = new JTextField(20);

  private final JButton OKButton = new JButton("OK");
  private final JButton cancelButton = new JButton("Cancel");

  // opens a New User GUI
  public UserGUI()
  {
    super("New User");

    setMinimumSize(new Dimension(385, 225));
    pack();
    setVisible(true);

    setLayout(new GridLayout(7, 2));

    JLabel usernameLabel = new JLabel("Username: ");
    JLabel passwordLabel = new JLabel("Password: ");
    JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
    JLabel firstNameLabel = new JLabel("First Name: ");
    JLabel lastNameLabel = new JLabel("Last Name: ");
    JLabel groupNumberLabel = new JLabel("Group Number: ");

    add(usernameLabel);
    add(usernameTF);
    add(passwordLabel);
    add(passwordTF);
    add(confirmPasswordLabel);
    add(confirmPasswordTF);
    add(firstNameLabel);
    add(firstNameTF);
    add(lastNameLabel);
    add(lastNameTF);
    add(groupNumberLabel);
    add(groupNumberTF);
    add(OKButton);
    add(cancelButton);

    cancelButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        closeWindow();
      }
    });

    OKButton.addActionListener(new ActionListener()
    {


      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        try
        {
          if( usernameTF.getText().equals("") )
          {
            throw new FormException("Username field cannot be empty");
          }
          else if( firstNameTF.getText().equals("") )
          {
            throw new FormException("First name field cannot be empty");
          }
          else if( lastNameTF.getText().equals("") )
          {
            throw new FormException("Last name field cannot be empty");
          }
          else if( passwordTF.getText().equals("")
              || confirmPasswordTF.getText().equals("") )
          {
            throw new FormException(
                "You must fill out both password and confirm password");
          }
          else if( !passwordTF.getText().equals(confirmPasswordTF.getText()) )
          {
            throw new FormException(
                "Password and confirm password do not match");
          }
          else
          {
            if( isExistingUser )
            {
              submitEditUser();
            }
            else
            {
              submitNewUser();
            }
          }
        }
        catch( FormException e )
        {
          JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  // opens an existing user GUI for editing
  public UserGUI(int _userID) throws MultipleResultsFoundException,
      NoResultsFoundException, SQLException
  {
    // call the normal super for a new user, but raise the flag for an existing
    // user
    this();
    isExistingUser = true;
    userID = _userID;
    User user = MySQL.getUser(_userID);

    // set the title
    this.setTitle("Edit Existing User: " + user.toString());

    // set fields
    firstNameTF.setText(user.firstName);
    lastNameTF.setText(user.lastName);
    usernameTF.setText(user.username);
    passwordTF.setText("");
    confirmPasswordTF.setText("");
    groupNumberTF.setText(String.valueOf(user.groupNum));
  }

  private void closeWindow()
  {
    WindowEvent winClosingEvent = new WindowEvent(this,
        WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit().getSystemEventQueue()
        .postEvent(winClosingEvent);
  }

  private void submitNewUser()
  {
    User tempUser = new User(userID, usernameTF.getText(),
        firstNameTF.getText(), lastNameTF.getText(), groupNumberTF.getText());
    try
    {
      userID = MySQL.addUser(tempUser, passwordTF.getText());
      dispose();

      MySQL.setAppTracked(userID, 0, true); // Other app
      MySQL.setAppTracked(userID, 1, true); // Idle app

      AdminGUI.updateUsers();
      AdminGUI.updateApps(userID);

    }
    catch( DuplicateKeyException e )
    {
      JOptionPane.showMessageDialog(null, "Username " + usernameTF.getText()
          + " already exists", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void submitEditUser()
  {
    User tempUser = new User(userID, usernameTF.getText(),
        firstNameTF.getText(), lastNameTF.getText(), groupNumberTF.getText());
    try
    {
      MySQL.editUser(tempUser, passwordTF.getText());
      dispose();
      AdminGUI.updateUsers();
    }
    catch( DuplicateKeyException e )
    {
      JOptionPane.showMessageDialog(null, "Username " + usernameTF.getText()
          + " already exists", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
