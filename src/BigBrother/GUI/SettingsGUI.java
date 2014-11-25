package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;

import BigBrother.Classes.Settings;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.NoSettingsException;

public class SettingsGUI extends JFrame {

    private final String[] timeUnits = {"ms", "sec", "min", "hours", "days"};
    private final NumberFormat numFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    
    private final JFormattedTextField polling_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox polling_interval_unit = new JComboBox(timeUnits);
    private final JFormattedTextField memory_flush_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox memory_flush_interval_unit = new JComboBox(timeUnits);
    private final JFormattedTextField local_flush_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox local_flush_interval_unit = new JComboBox(timeUnits);
    private final JFormattedTextField max_idle_time_TF = new JFormattedTextField(numFormat);
    private final JComboBox max_idle_time_unit = new JComboBox(timeUnits);
    private final JFormattedTextField start_time_TF = new JFormattedTextField(numFormat); //TODO: make this a date selector
    private final JFormattedTextField block_time_TF = new JFormattedTextField(numFormat);
    private final JComboBox block_time_unit = new JComboBox(timeUnits);

    private final JButton OKButton = new JButton("Update");
    private final JButton cancelButton = new JButton("Cancel");

    // opens the Settings GUI
    public SettingsGUI() {
        super("Edit Settings");

        setLayout(new BorderLayout());

        
        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        JPanel panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        JLabel polling_interval_label = new JLabel("Polling Interval: ");
        JLabel memory_flush_interval_label = new JLabel("Memory -> Local Flush Interval: ");
        JLabel local_flush_interval_TF_label = new JLabel("Local -> Server Flush Interval: ");
        JLabel max_idle_time_label = new JLabel("Time to Idle: ");
        JLabel start_time_TF_label = new JLabel("Start Date: ");
        JLabel block_time_label = new JLabel("Time Block Duration: ");

        panelLeft.add(polling_interval_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(polling_interval_TF);
        panelRight.add(polling_interval_unit);

        panelLeft.add(memory_flush_interval_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(memory_flush_interval_TF);
        panelRight.add(memory_flush_interval_unit);

        panelLeft.add(local_flush_interval_TF_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(local_flush_interval_TF);
        panelRight.add(local_flush_interval_unit);

        panelLeft.add(max_idle_time_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(max_idle_time_TF);
        panelRight.add(max_idle_time_unit);

        panelLeft.add(start_time_TF_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(start_time_TF);

        panelLeft.add(block_time_label);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(block_time_TF);
        panelRight.add(block_time_unit);

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
                    //TODO: check for valid settings
                	
                	submitSettings();
                	
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Unspecified Error!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this,
            WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().
        postEvent(winClosingEvent);
    }

    private void submitSettings() {
    	Settings newSettings = new Settings();
    	
        // TODO: build the Settings (remember to take units into account)
    	
    	//upload the new settings to the SQL DB
    	try {
			MySQL.sendSettings(newSettings);
		} catch (NoSettingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
