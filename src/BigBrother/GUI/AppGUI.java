package BigBrother.GUI;

import java.awt.Dimension;
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

import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

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

        
        
        GridLayout gridLayout = new GridLayout(4, 3);
        getContentPane().setLayout(gridLayout);

        FlowLayout fl_buttonGroup = new FlowLayout();
        fl_buttonGroup.setVgap(2);
        JPanel buttonGroup = new JPanel(fl_buttonGroup);
        buttonGroup.add(OKButton);
        buttonGroup.add(cancelButton);

        JLabel label = new JLabel("Alias Name:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(label);
        getContentPane().add(aliasTF);
        aliasTF.setToolTipText("Enter the unique identifier for this new application");
        getContentPane().add(new JPanel(new FlowLayout())); // filler grid space

        JLabel label_1 = new JLabel("Window Name:");
        label_1.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(label_1);
        windowTF.setToolTipText("Enter the window name that will match this application. (OPTIONAL)");
        getContentPane().add(windowTF);
        FlowLayout fl_windowRegexGroup = new FlowLayout();
        fl_windowRegexGroup.setHgap(2);
        fl_windowRegexGroup.setVgap(2);
        fl_windowRegexGroup.setAlignment(FlowLayout.LEFT);
        JPanel windowRegexGroup = new JPanel(fl_windowRegexGroup);
        windowRegexGroup.add(windowIsRegex);
        windowRegexGroup.add(new JLabel("is Regex"));
        getContentPane().add(windowRegexGroup);

        JLabel label_2 = new JLabel("Process Name:");
        label_2.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(label_2);
        processTF.setToolTipText("Enter the process name that will match this application. (OPTIONAL)");
        getContentPane().add(processTF);
        FlowLayout fl_processRegexGroup = new FlowLayout();
        fl_processRegexGroup.setVgap(2);
        fl_processRegexGroup.setHgap(2);
        fl_processRegexGroup.setAlignment(FlowLayout.LEFT);
        JPanel processRegexGroup = new JPanel(fl_processRegexGroup);
        processRegexGroup.add(processIsRegex);
        JLabel label_3 = new JLabel("is Regex");
        processRegexGroup.add(label_3);
        getContentPane().add(processRegexGroup);

        getContentPane().add(new JPanel(new FlowLayout())); // filler grid space
        getContentPane().add(buttonGroup);
        getContentPane().add(new JPanel(new FlowLayout())); // filler grid space

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
                        throw new EmptyTFException("App Alias field cannot be empty");
                    } else if (windowTF.getText().equals("") && processTF.getText().equals("")) {
                        throw new EmptyTFException("You must fill out either Window name or Process name");
                    } else {
                        if (isExistingApp)
                            submitEditApp();
                        else
                            submitNewApp();
                    }
                } catch (EmptyTFException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        // set fields
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
