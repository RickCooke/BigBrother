package BigBrother.GUI;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import BigBrother.Exceptions.UserDoesNotExist;
import BigBrother.Main.Client;
import BigBrother.Main.Main;
import BigBrother.Main.MySQL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * This class creates the LoginGUI JFrame and is the main GUI class.
 */

@SuppressWarnings("serial")
public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * Constructor. This creates the JFrame that controls the simulation.
     */
    public LoginGUI() {
        super("BigBrother Login");
        getContentPane().setLayout(new FlowLayout());

        JPanel containerPanel = new JPanel();
        getContentPane().add(containerPanel);
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.PAGE_AXIS));

        JPanel fieldPanel = new JPanel();
        containerPanel.add(fieldPanel);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        fieldPanel.add(usernameLabel);

        usernameField = new JTextField();
        fieldPanel.add(usernameField);
        usernameField.setToolTipText("Enter your username");
        usernameField.setColumns(15);

        JLabel passwordLabel = new JLabel("Password:");
        fieldPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        fieldPanel.add(passwordField);
        passwordField.setToolTipText("Enter your password");
        passwordField.setColumns(15);

        JPanel buttonPanel = new JPanel();
        containerPanel.add(buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        setResizable(false);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passwordStr = new String(passwordField.getPassword());
                attemptLogin(usernameField.getText(), passwordStr);
            }
        });

        // TODO: remove this to prompt for username/pw
        attemptLogin("defaultUser", "password");
    }

    private void attemptLogin(String username, String password) {
        MySQL.establishConnection();
        String passwordHash = MD5(password);


        try {
            Main.loggedInUserID = MySQL.checkPassword(username, passwordHash);
            closeDialog();
            new Client();
        } catch (UserDoesNotExist e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String MD5(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (new HexBinaryAdapter()).marshal(md.digest(input.getBytes()));
    }

    /**
     * Easy and quick way to close the current window
     */
    private void closeDialog() {
        WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }
}
