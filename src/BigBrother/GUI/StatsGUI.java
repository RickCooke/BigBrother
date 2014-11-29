/**
 * general stuff that needs to be done:
 *  - add a "num blocks" label that shows how many blocks would 
 *    be selected with the specified time range and the MYSQL.settings for
 *    block size.
 *  - JDateChooser is fucked up
 *  - getSelectedValue() isn't working right. either switch to only one
 *    user or figure it out.
 *  - still need to work on adding functionality for SQL data pulling
 *  - still need to add jfreechart stuff into viewStatsGUI.java
 */


package BigBrother.GUI;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.toedter.calendar.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import BigBrother.Classes.UserLite;
import BigBrother.Client.MySQL;

public class StatsGUI extends JFrame {
  // west panel vars
  private DefaultListModel<UserLite> usersDLM 
    = new DefaultListModel<UserLite>();
  private JLabel usersLabel;
  private JList<UserLite> userList;
  private JScrollPane userScrollPane;
  
  // east panel vars
  private JLabel dateLabel;
  private JDateChooser dateChooser;
  private JLabel isAllDayLabel;
  private JCheckBox allDayBox;
  private JLabel startTimeLabel;
  private JLabel endTimeLabel;
  private JSpinner timeSpinnerStart;
  private JSpinner timeSpinnerEnd;
  private SpinnerDateModel modelStart;
  private SpinnerDateModel modelEnd;
  private JButton viewLinePlot;
  private JButton viewPieChart;

  public StatsGUI() {
    super("User Stats");

    setLayout(new BorderLayout());

    JPanel westPanel = new JPanel(new BorderLayout());
    JPanel eastPanel = new JPanel(new GridLayout(3, 1));
    
    renderWestPanel(westPanel);
    renderEastPanel(eastPanel);

    add(westPanel, BorderLayout.WEST);
    add(eastPanel, BorderLayout.EAST);
  }
  
  public void renderWestPanel(JPanel westPanel) {
    MySQL.getUserList(usersDLM);
    usersLabel = new JLabel("Select at least one user", JLabel.CENTER);
    usersLabel.setAlignmentX(CENTER_ALIGNMENT);
    userList = new JList<UserLite>(usersDLM);
    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        // TODO: Fix selecting users
        Object[] selectedValues = userList.getSelectedValues();
        
        if (selectedValues.length > 0) {
          viewLinePlot.setEnabled(true);
          viewPieChart.setEnabled(true);
        } else {
          viewLinePlot.setEnabled(false);
          viewPieChart.setEnabled(false);
        }
      }
    });
    userScrollPane = new JScrollPane(userList);
    userScrollPane.setPreferredSize(new Dimension(200, 200));
    userScrollPane.setAlignmentX(CENTER_ALIGNMENT);

    westPanel.add(usersLabel, BorderLayout.NORTH);
    westPanel.add(userScrollPane, BorderLayout.SOUTH);
    westPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }
  
  public void renderEastPanel(JPanel eastPanel) {
    JPanel eastPanelTop = new JPanel(new FlowLayout());
    JPanel eastPanelMid = new JPanel(new FlowLayout());
    JPanel eastPanelBot = new JPanel(new FlowLayout());
    
    dateLabel = new JLabel("Date: ");
    dateChooser = new JDateChooser();
    dateChooser.setDateFormatString("MM/dd/yy");
    dateChooser.setDate(roundToHr(new Date()));
    dateChooser.setSize(new Dimension(150, 0));
    dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        // TODO Fix date chooser so it actually works
        System.out.println("changed!");
      }
    });
    isAllDayLabel = new JLabel("All Day?");
    allDayBox = new JCheckBox();
    allDayBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        boolean isSelected = allDayBox.isSelected();
        if (isSelected) {
          timeSpinnerStart.setEnabled(false);
          timeSpinnerEnd.setEnabled(false);
        } else {
          timeSpinnerStart.setEnabled(true);
          timeSpinnerEnd.setEnabled(true);
        }
      }
    });

    eastPanelTop.add(dateLabel);
    eastPanelTop.add(dateChooser);
    eastPanelTop.add(allDayBox);
    eastPanelTop.add(isAllDayLabel);

    // east panel mid
    startTimeLabel = new JLabel("Start Time:");
    endTimeLabel = new JLabel("End Time:");
    timeSpinnerStart = new JSpinner();
    timeSpinnerEnd = new JSpinner();
    
    modelStart = new SpinnerDateModel();
    modelStart.setCalendarField(Calendar.MINUTE);
    timeSpinnerStart = new JSpinner(modelStart);
    long eightHoursAgo = System.currentTimeMillis() - (8 * 60 * 60 * 1000);
    timeSpinnerStart.setValue(roundToHr(new Date(eightHoursAgo)));
    JComponent editorStart = new JSpinner.DateEditor(timeSpinnerStart, "hh:mm:ss a");
    timeSpinnerStart.setEditor(editorStart);
    
    modelEnd = new SpinnerDateModel();
    modelEnd.setCalendarField(Calendar.MINUTE);
    timeSpinnerEnd = new JSpinner(modelEnd);
    timeSpinnerEnd.setValue(roundToHr(new Date()));
    JComponent editorEnd = new JSpinner.DateEditor(timeSpinnerEnd, "hh:mm:ss a");
    timeSpinnerEnd.setEditor(editorEnd);

    eastPanelMid.add(startTimeLabel);
    eastPanelMid.add(timeSpinnerStart);
    eastPanelMid.add(endTimeLabel);
    eastPanelMid.add(timeSpinnerEnd);
    
    // east panel bottom
    viewLinePlot = new JButton("View Usage Over Time");
    viewLinePlot.setEnabled(false);
    viewLinePlot.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // TODO: get hour range working correctly
        String start = null, end = null;
        
        try
        {
          start = getDateTimeString(dateChooser, timeSpinnerStart);
          end = getDateTimeString(dateChooser, timeSpinnerEnd);
        }
        catch( ParseException e1 )
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        
        
        System.out.println(start.toString());
        System.out.println(end.toString());
        
        // for each selected user:
        
        // TODO: Fix user selecting
        UserLite[] selectedUsers = (UserLite[]) userList.getSelectedValues();
        int len = selectedUsers.length;
        int[][] data = new int[len][];
        for (int i = 0; i < len; i++) {
          int id = selectedUsers[i].getID();
          
          // should return array of values
          data[len] = getUsageStats(start, end, id);
        }
        // plot data
        ViewStatsGUI win = new ViewStatsGUI(data);
        win.pack();
        win.setVisible(true);
      }
    });
    viewPieChart = new JButton("View Usage Totals");
    viewPieChart.setEnabled(false);
    viewPieChart.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // get hour range
        
        // for each selected user:
        //    - find all applications used for entire timeframe
        
        // plot data
        //  - if multiple users,
        //    display multiple pie charts side by side.
      }
    });
    
    eastPanelBot.add(viewLinePlot);
    eastPanelBot.add(viewPieChart);
    
    // add panels to main
    eastPanel.add(eastPanelTop);
    eastPanel.add(eastPanelMid);
    eastPanel.add(eastPanelBot);
    eastPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }
  
  private int[] getUsageStats(String start, String end, int id) {
    int[] stats = null;
    
    // TODO: function will check the database and return stats for user with id=id.
    // is there is no data on this range, will return all 0s. if there is partial data,
    // it will return the partial data with 0s at every other point.
    
    return stats;
  }

  public Date roundToHr(Date d) {
    Calendar date = new GregorianCalendar();
    date.setTime(d);
    int deltaHr = date.get(Calendar.MINUTE) / 30;

    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    date.set(Calendar.MINUTE, 0);
    date.add(Calendar.HOUR, deltaHr);

    return date.getTime();
  }
  
  private String getDateTimeString(JDateChooser date, JSpinner time) throws ParseException {
    String dateTime;
    SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dateFormat.format(date.getDate());

    SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss");
    String formattedTime = timeFormat.format(time.getValue());


    Date result = fmt.parse(formattedDate + " " + formattedTime);
    dateTime = fmt.format(result);

    return dateTime;
  }
}