package BigBrother.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import com.toedter.calendar.JDateChooser;

import BigBrother.Classes.Settings;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.FormException;
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

    private final static JDateChooser dateChooser = new JDateChooser();
    private static JSpinner timeSpinner = new JSpinner();

    // opens the Settings GUI
    public SettingsGUI() {
        super("Edit Settings");

        setLayout(new GridLayout(7, 2));

        polling_interval_TF.setValue(Main.settings.polling_interval);
        memory_flush_interval_TF.setValue(Main.settings.memory_flush_interval);
        local_flush_interval_TF.setValue(Main.settings.local_flush_interval);
        max_idle_time_TF.setValue(Main.settings.max_idle_time);
        block_time_TF.setValue(Main.settings.block_time);



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

        add(new JLabel("Memory => Local Flush Interval: "));
        add(memory_flush_interval_group);

        add(new JLabel("Local => Server Flush Interval: "));
        add(local_flush_interval_group);

        add(new JLabel("Time to Idle: "));
        add(max_idle_time_group);


        add(new JLabel("Start Date: "));
        System.out.println(Main.settings.start_time_string);
        

        
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            add(buildDatePanel(df.parse(Main.settings.start_time_string)));
        } catch (ParseException e2) {
            e2.printStackTrace();
        }


        add(new JLabel("Time Block Duration: "));
        add(block_time_group);

        add(OKButton);
        add(cancelButton);


        ItemListener ComboIL = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox<String> combo = (JComboBox<String>) e.getSource();
                String comboString = e.getItem().toString();

                // Convert to MS
                if (e.getStateChange() == 2) {
                    try {
                        if (combo == polling_interval_unit) {
                            long old = numFormat.parse(polling_interval_TF.getText()).intValue();
                            polling_interval_TF.setValue(old * getMultiplier(comboString));
                        } else if (combo == memory_flush_interval_unit) {
                            long old = numFormat.parse(memory_flush_interval_TF.getText()).intValue();
                            memory_flush_interval_TF.setValue(old * getMultiplier(comboString));
                        } else if (combo == local_flush_interval_unit) {
                            long old = numFormat.parse(local_flush_interval_TF.getText()).intValue();
                            local_flush_interval_TF.setValue(old * getMultiplier(comboString));
                        } else if (combo == max_idle_time_unit) {
                            long old = numFormat.parse(max_idle_time_TF.getText()).intValue();
                            max_idle_time_TF.setValue(old * getMultiplier(comboString));
                        } else if (combo == block_time_unit) {
                            long old = numFormat.parse(block_time_TF.getText()).intValue();
                            block_time_TF.setValue(old * getMultiplier(comboString));
                        }
                    } catch (ParseException e1) {
                        System.err.println(e1.getMessage());
                    }
                } else if (e.getStateChange() == 1) {
                    // Convert from MS to whatever combo is
                    try {
                        if (combo == polling_interval_unit) {
                            long old = numFormat.parse(polling_interval_TF.getText()).intValue();
                            polling_interval_TF.setValue(old / getMultiplier(comboString));
                        } else if (combo == memory_flush_interval_unit) {
                            long old = numFormat.parse(memory_flush_interval_TF.getText()).intValue();
                            memory_flush_interval_TF.setValue(old / getMultiplier(comboString));
                        } else if (combo == local_flush_interval_unit) {
                            long old = numFormat.parse(local_flush_interval_TF.getText()).intValue();
                            local_flush_interval_TF.setValue(old / getMultiplier(comboString));
                        } else if (combo == max_idle_time_unit) {
                            long old = numFormat.parse(max_idle_time_TF.getText()).intValue();
                            max_idle_time_TF.setValue(old / getMultiplier(comboString));
                        } else if (combo == block_time_unit) {
                            long old = numFormat.parse(block_time_TF.getText()).intValue();
                            block_time_TF.setValue(old / getMultiplier(comboString));
                        }
                    } catch (ParseException e1) {
                        System.err.println(e1.getMessage());
                    }
                }
            }
        };


        polling_interval_unit.addItemListener(ComboIL);
        memory_flush_interval_unit.addItemListener(ComboIL);
        local_flush_interval_unit.addItemListener(ComboIL);
        max_idle_time_unit.addItemListener(ComboIL);
        block_time_unit.addItemListener(ComboIL);


        changeToLargestUnit();


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
                    if (polling_interval_TF.getText().equals("")) {
                        throw new FormException("Polling interval field cannot be empty");
                    } else if (memory_flush_interval_TF.getText().equals("")) {
                        throw new FormException("Memory flush field cannot be empty");
                    } else if (local_flush_interval_TF.getText().equals("")) {
                        throw new FormException("Local flush field cannot be empty");
                    } else if (max_idle_time_TF.getText().equals("")) {
                        throw new FormException("Max idle time field cannot be empty");
                    } else if (block_time_TF.getText().equals("")) {
                        throw new FormException("Block time field cannot be empty");
                    } else {
                        Settings newSettings = new Settings();
                        newSettings.polling_interval = numFormat.parse(polling_interval_TF.getText()).intValue();
                        newSettings.memory_flush_interval = numFormat.parse(memory_flush_interval_TF.getText()).intValue();
                        newSettings.local_flush_interval = numFormat.parse(local_flush_interval_TF.getText()).intValue();
                        newSettings.max_idle_time = numFormat.parse(max_idle_time_TF.getText()).intValue();
                        newSettings.block_time = numFormat.parse(block_time_TF.getText()).intValue();


                        newSettings.polling_interval *= getMultiplier(polling_interval_unit);
                        newSettings.memory_flush_interval *= getMultiplier(memory_flush_interval_unit);
                        newSettings.local_flush_interval *= getMultiplier(local_flush_interval_unit);
                        newSettings.max_idle_time *= getMultiplier(max_idle_time_unit);
                        newSettings.block_time *= getMultiplier(block_time_unit);

                        if (newSettings.memory_flush_interval < newSettings.polling_interval) {
                            throw new FormException("Memory flush must be greater or equal to Polling interval");
                        } else if (newSettings.local_flush_interval < newSettings.memory_flush_interval) {
                            throw new FormException("Local flush must be greater or equal to Memory flush interval");
                        } else if (newSettings.block_time < newSettings.memory_flush_interval) {
                            throw new FormException("Block time must be greater or equal to Memory flush interval");
                        } else if (newSettings.max_idle_time < newSettings.polling_interval) {
                            throw new FormException("Max idle time must be greater or equal to Polling interval");
                        } else if (newSettings.memory_flush_interval % newSettings.polling_interval != 0) {
                            throw new FormException("Memory flush must be a multiple of Polling interval");
                        } else if (newSettings.local_flush_interval % newSettings.polling_interval != 0) {
                            throw new FormException("Local flush must be a multiple of Polling interval");
                        } else if (newSettings.max_idle_time % newSettings.polling_interval != 0) {
                            throw new FormException("max idle time must be a multiple of Polling interval");
                        }
                        newSettings.start_time_string = getStartTimeString();
                        System.out.println(newSettings);

                        submitSettings(newSettings);
                    }
                } catch (FormException | ParseException | MalformedSettingsException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void submitSettings(Settings newSettings) throws MalformedSettingsException {
        MySQL.updateSettings(newSettings);
        Main.settings.downloadSettings();
        dispose();
    }

    private void changeToLargestUnit() {
        JFormattedTextField[] arrayTF = {polling_interval_TF, memory_flush_interval_TF, local_flush_interval_TF, max_idle_time_TF, block_time_TF};
        JComboBox[] arrayCB = {polling_interval_unit, memory_flush_interval_unit, local_flush_interval_unit, max_idle_time_unit, block_time_unit};
        long[] originals = {Main.settings.polling_interval, Main.settings.memory_flush_interval, Main.settings.local_flush_interval, Main.settings.max_idle_time, Main.settings.block_time};

        try {
            for (int i = 0; i < arrayTF.length; i++) {
                int ii = 4;
                while (true) {
                    arrayCB[i].setSelectedIndex(0);
                    arrayTF[i].setValue(originals[i]);
                    arrayCB[i].setSelectedIndex(ii);


                    long old = numFormat.parse(arrayTF[i].getText()).intValue();
                    if ((old * getMultiplier(arrayCB[i])) == originals[i]) {
                        break;
                    }
                    
                    ii--;
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

    private String getStartTimeString() throws ParseException {
        String startTime;
        SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(dateChooser.getDate());

        SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(timeSpinner.getValue());


        Date result = fmt.parse(formattedDate + " " + formattedTime);
        startTime = fmt.format(result);

        return startTime;
    }


    private int getMultiplier(JComboBox<String> combo) {
        return getMultiplier(combo.getSelectedItem().toString());
    }

    private int getMultiplier(String unit) {
        switch (unit) {
            case "sec":
                return 1000;
            case "min":
                return 60 * 1000;
            case "hours":
                return 60 * 60 * 1000;
            case "days":
                return 24 * 60 * 60 * 1000;
        }

        return 1;
    }


    public static JPanel buildDatePanel(Date in_date) {
        JPanel datePanel = new JPanel();


        dateChooser.setDateFormatString("MM/dd/yy");
        dateChooser.setDate(in_date);

        datePanel.add(dateChooser);


        SpinnerDateModel model = new SpinnerDateModel();
        model.setCalendarField(Calendar.MINUTE);
        timeSpinner = new JSpinner(model);
        timeSpinner.setValue(in_date);
        JComponent editor = new JSpinner.DateEditor(timeSpinner, "hh:mm:ss a");
        timeSpinner.setEditor(editor);

        datePanel.add(timeSpinner);

        return datePanel;
    }

}
