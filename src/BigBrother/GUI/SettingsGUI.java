package BigBrother.GUI;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;

import BigBrother.Classes.Settings;
import BigBrother.Exceptions.MalformedSettingsException;

public class SettingsGUI extends JFrame {

    private final String[] timeUnits = {"ms", "sec", "min", "hours", "days"};
    private final NumberFormat numFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    
    private final JFormattedTextField polling_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox<String> polling_interval_unit = new JComboBox<String>(timeUnits);
    private final JFormattedTextField memory_flush_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox<String> memory_flush_interval_unit = new JComboBox<String>(timeUnits);
    private final JFormattedTextField local_flush_interval_TF = new JFormattedTextField(numFormat);
    private final JComboBox<String> local_flush_interval_unit = new JComboBox<String>(timeUnits);
    private final JFormattedTextField max_idle_time_TF = new JFormattedTextField(numFormat);
    private final JComboBox<String> max_idle_time_unit = new JComboBox<String>(timeUnits);
    private final JFormattedTextField block_time_TF = new JFormattedTextField(numFormat);
    private final JComboBox<String> block_time_unit = new JComboBox<String>(timeUnits);

    private final JButton OKButton = new JButton("Update");
    private final JButton cancelButton = new JButton("Cancel");

    // opens the Settings GUI
    public SettingsGUI() {
        super("Edit Settings");

        setLayout(new GridLayout(7, 2));
        
        //Set default units
        polling_interval_unit.setSelectedIndex(0);
        memory_flush_interval_unit.setSelectedIndex(1);
        local_flush_interval_unit.setSelectedIndex(2);
        max_idle_time_unit.setSelectedIndex(2);
        block_time_unit.setSelectedIndex(3);
        
        JPanel polling_interval_group = new JPanel(new FlowLayout());
        polling_interval_TF.setColumns(10);
        polling_interval_group.add(polling_interval_TF);
        polling_interval_group.add(polling_interval_unit);

        JPanel memory_flush_interval_group = new JPanel(new FlowLayout());
        memory_flush_interval_TF.setColumns(10);
        memory_flush_interval_group.add(memory_flush_interval_TF);
        memory_flush_interval_group.add(memory_flush_interval_unit);

        JPanel local_flush_interval_group = new JPanel(new FlowLayout());
        local_flush_interval_TF.setColumns(10);
        local_flush_interval_group.add(local_flush_interval_TF);
        local_flush_interval_group.add(local_flush_interval_unit);

        JPanel max_idle_time_group = new JPanel(new FlowLayout());
        max_idle_time_TF.setColumns(10);
        max_idle_time_group.add(max_idle_time_TF);
        max_idle_time_group.add(max_idle_time_unit);


        JPanel block_time_group = new JPanel(new FlowLayout());
        block_time_TF.setColumns(10);
        block_time_group.add(block_time_TF);
        block_time_group.add(block_time_unit);
        
        add(new JLabel("Polling Interval: "));
        add(polling_interval_group);
        
        add(new JLabel("Memory -> Local Flush Interval: "));
        add(memory_flush_interval_group);
        
        add(new JLabel("Local -> Server Flush Interval: "));
        add(local_flush_interval_group);
        
        add(new JLabel("Time to Idle: "));
        add(max_idle_time_group);
        
        //TODO: find a Date Picker Library
        //maybe this one? https://github.com/JDatePicker/JDatePicker
        add(new JLabel("Start Date: "));
        add(new JLabel("***PLACEHOLDER FOR DATE PICKER***"));
        
        add(new JLabel("Time Block Duration: "));
        add(block_time_group);
        
        add(OKButton);
        add(cancelButton);
        
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
                	submitSettings();
                } catch (MalformedSettingsException e) {
                    JOptionPane.showMessageDialog(null, 
                        e.getMessage(), "Error", 
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

    private void submitSettings() throws MalformedSettingsException {
    	Settings newSettings = new Settings();
    	
        // TODO: build the Settings (remember to take units into account),
    	// throw a MalformedSettingsException if something is wrong
    	throw new MalformedSettingsException("Not yet implemented!");

    	//upload the new settings to the SQL DB
    	//TODO: uncomment this code, it's just commented cause it's unreachable code from the placeholder throw above
		//MySQL.sendSettings(newSettings);
    }
}
