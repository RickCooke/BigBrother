package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import BigBrother.Classes.User;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.EmptyTFException;
import BigBrother.Exceptions.MismatchedPasswordException;
import BigBrother.Exceptions.MultipleResultsFoundException;
import BigBrother.Exceptions.NoResultsFoundException;

public class UserGUI extends JFrame {

    private boolean isExistingUser = false;
    private int userID = -1; // only should be set if user is being edited

    private final JTextField usernameTF = new JTextField(20);
    private final JTextField passwordTF = new JPasswordField(20);
    private final JTextField confirmPasswordTF = new JPasswordField(20);
    private final JTextField firstNameTF = new JTextField(20);
    private final JTextField lastNameTF = new JTextField(20);
    private final JTextField groupNumberTF = new JTextField(20);

    private final JButton OKButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");

    // opens a New User GUI
    public UserGUI() {
        super("New User");

        setLayout(new BorderLayout());

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        JPanel panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        JLabel usernameLabel = new JLabel("Username: ");
        JLabel passwordLabel = new JLabel("Password: ");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        JLabel firstNameLabel = new JLabel("First Name: ");
        JLabel lastNameLabel = new JLabel("Last Name: ");
        JLabel groupNumberLabel = new JLabel("Group Number: ");

        panelLeft.add(usernameLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(usernameTF);

        panelLeft.add(passwordLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(passwordTF);

        panelLeft.add(confirmPasswordLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(confirmPasswordTF);

        panelLeft.add(firstNameLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(firstNameTF);

        panelLeft.add(lastNameLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(lastNameTF);

        panelLeft.add(groupNumberLabel);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(groupNumberTF);

        panelLeft.add(Box.createRigidArea(new Dimension(0, 10)));
        panelRight.add(Box.createRigidArea(new Dimension(0, 15)));
        panelLeft.add(OKButton);
        panelRight.add(cancelButton);
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);

        panelLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(panelLeft, BorderLayout.LINE_START);
        add(panelRight, BorderLayout.LINE_END);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                closeWindow();
            }
        });

        OKButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    // check for formatting issues
                    if (firstNameTF.getText().equals("") | 
                        lastNameTF.getText().equals("") |
                        usernameTF.getText().equals("") |
                        passwordTF.getText().equals("") |
                        confirmPasswordTF.getText().equals("")) {
                        throw new EmptyTFException();
                    } else if (!passwordTF.getText().equals(confirmPasswordTF.getText())) {
                        throw new MismatchedPasswordException();
                    } else {
                        if (isExistingUser)
                            submitEditUser();
                        else
                            submitNewUser();
                    }
                } catch (EmptyTFException e) {
                    JOptionPane.showMessageDialog(null, 
                        "All Text Fields Except Group Number "
                        + "Must Be Filled!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (MismatchedPasswordException e) {
                    JOptionPane.showMessageDialog(null, "Passwords Do "
                        + "Not Match!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // opens an existing user GUI for editing
    public UserGUI(int userID) throws MultipleResultsFoundException, NoResultsFoundException, SQLException {
        // call the normal super for a new user, but raise the flag for an existing user
        this();
        isExistingUser = true;

        User user = MySQL.getUser(userID);
        
        // set the title
        this.setTitle("Edit Existing User: " + user.toString());

        //set fields
        firstNameTF.setText(user.firstName);
        lastNameTF.setText(user.lastName);
        usernameTF.setText(user.username);
        passwordTF.setText("");
        confirmPasswordTF.setText("");
        groupNumberTF.setText(String.valueOf(user.groupNum));
    }

    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this,
            WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().
        postEvent(winClosingEvent);
    }

    private void submitNewUser() {
        // TODO

        // TODO: remember to add new "Idle" and "Other" apps for that user
    }

    private void submitEditUser() {
        // TODO
    }
}
