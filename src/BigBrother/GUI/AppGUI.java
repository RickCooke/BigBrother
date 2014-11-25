package BigBrother.GUI;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import BigBrother.Classes.App;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.EmptyTFException;
import BigBrother.Exceptions.MultipleResultsFoundException;
import BigBrother.Exceptions.NoResultsFoundException;

public class AppGUI extends JFrame {

    private boolean isExistingApp = false;

    private final JTextField aliasTF = new JTextField(20);
    private final JTextField windowTF = new JTextField(20);
    private final JTextField processTF = new JTextField(20);
    private final JCheckBox windowIsRegex = new JCheckBox();
    private final JCheckBox processIsRegex = new JCheckBox();

    private final JButton OKButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");

    public AppGUI() {
        super("New Appliction");

        setLayout(new GridLayout(4, 3));

        JPanel buttonGroup = new JPanel(new FlowLayout());
        buttonGroup.add(OKButton);
        buttonGroup.add(cancelButton);
        
        add(new JLabel("Alias Name:"));
        add(aliasTF);
        add(new JPanel(new FlowLayout())); //filler grid space
        
        add(new JLabel("Window Name:"));
        add(windowTF);
        JPanel windowRegexGroup = new JPanel(new FlowLayout());
        windowRegexGroup.add(windowIsRegex);
        windowRegexGroup.add(new JLabel("is Regex"));
        add(windowRegexGroup);
        
        add(new JLabel("Process Name:"));
        add(processTF);
        JPanel processRegexGroup = new JPanel(new FlowLayout());
        processRegexGroup.add(processIsRegex);
        processRegexGroup.add(new JLabel("is Regex"));
        add(processRegexGroup);

        add(new JPanel(new FlowLayout())); //filler grid space
        add(buttonGroup);
        add(new JPanel(new FlowLayout())); //filler grid space

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
                    if (aliasTF.getText().equals("")) {
                        throw new EmptyTFException();
                    } else {
                        if (isExistingApp)
                            submitEditApp();
                        else
                            submitNewApp();
                    }
                } catch (EmptyTFException e) {
                    JOptionPane.showMessageDialog(null, "App Alias Field Must Be Filled!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    public AppGUI(int appID) throws MultipleResultsFoundException, NoResultsFoundException, SQLException {
        // call the normal super for a new user, but raise the flag for an existing user
        this();
        isExistingApp = true;

        App app = MySQL.getApp(appID);
        // set the title
        this.setTitle("Edit Existing Application: " + app.toString());

        //set fields
        aliasTF.setText(app.alias);
        windowTF.setText(app.window);
        windowIsRegex.setSelected(app.window_regex);
        processTF.setText(app.process);
        processIsRegex.setSelected(app.process_regex);
    }

    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

    private void submitNewApp() {
        // TODO: implement this
    }

    private void submitEditApp() {
        // TODO: implement this
    }
}
