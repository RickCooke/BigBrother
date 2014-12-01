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

import org.jfree.ui.RefineryUtilities;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import com.toedter.calendar.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import BigBrother.Classes.UserLite;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;

public class StatsGUI extends JFrame
{
  // west panel vars
  private DefaultListModel<UserLite> usersDLM 
    = new DefaultListModel<UserLite>();
  private JLabel usersLabel;
  private JList<UserLite> userList;
  private JScrollPane userScrollPane;
  private JDateChooser dateChooserStart;
  private JLabel startTimeLabel;
  private JLabel endTimeLabel;
  private JSpinner timeSpinnerStart;
  private JSpinner timeSpinnerEnd;
  private SpinnerDateModel modelStart;
  private SpinnerDateModel modelEnd;
  private JButton viewLinePlot;
  private JDateChooser dateChooserEnd;
  private JPanel eastPanel_1;

  public StatsGUI()
  {
    super("User Stats");

    getContentPane().setLayout(new BorderLayout());

    JPanel westPanel = new JPanel(new BorderLayout());
    eastPanel_1 = new JPanel(new GridLayout(3, 1));

    renderWestPanel(westPanel);
    renderEastPanel(eastPanel_1);

    getContentPane().add(westPanel, BorderLayout.WEST);
    getContentPane().add(eastPanel_1, BorderLayout.EAST);
  }

  public void renderWestPanel(JPanel westPanel)
  {
    MySQL.getUserList(usersDLM);
    usersLabel = new JLabel("Select user", JLabel.CENTER);
    usersLabel.setAlignmentX(CENTER_ALIGNMENT);
    userList = new JList<UserLite>(usersDLM);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        UserLite selectedValue = userList.getSelectedValue();

        if( selectedValue != null )
        {
          viewLinePlot.setEnabled(true);
        }
        else
        {
          viewLinePlot.setEnabled(false);
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

  public void renderEastPanel(JPanel eastPanel)
  {
    long eightBlocksAgo = System.currentTimeMillis() - 20
        * (Main.settings.block_time);
    Date startDate = roundToMin(new Date(eightBlocksAgo));
    Date endDate = roundToMin(new Date());

    JPanel eastPanelTop = new JPanel(new FlowLayout());
    JPanel eastPanelMid = new JPanel(new FlowLayout());
    JPanel eastPanelBot = new JPanel(new FlowLayout());
    dateChooserStart = new JDateChooser();
    dateChooserStart.setDateFormatString("MM/dd/yy");
    dateChooserStart.setDate(startDate);
    dateChooserStart.setSize(new Dimension(150, 0));
    dateChooserStart.addPropertyChangeListener(new PropertyChangeListener()
    {
      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        System.out.println("changed!");
      }
    });

    // east panel mid
    startTimeLabel = new JLabel("Start Time:");
    eastPanelTop.add(startTimeLabel);
    eastPanelTop.add(dateChooserStart);
    modelStart = new SpinnerDateModel();
    modelStart.setCalendarField(Calendar.MINUTE);
    timeSpinnerStart = new JSpinner();
    timeSpinnerEnd = new JSpinner();


    modelEnd = new SpinnerDateModel();
    modelEnd.setCalendarField(Calendar.MINUTE);
    timeSpinnerEnd = new JSpinner(modelEnd);
    timeSpinnerEnd.setValue(endDate);
    JComponent editorEnd = new JSpinner.DateEditor(timeSpinnerEnd, "HH:mm:ss");
    timeSpinnerEnd.setEditor(editorEnd);
    endTimeLabel = new JLabel("End Time:");
    eastPanelMid.add(endTimeLabel);

    dateChooserEnd = new JDateChooser();
    dateChooserEnd.setDateFormatString("MM/dd/yy");
    dateChooserEnd.setDate(endDate);
    dateChooserEnd.setSize(new Dimension(150, 0));
    dateChooserEnd.addPropertyChangeListener(new PropertyChangeListener()
    {
      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        System.out.println("changed!");
      }
    });
    eastPanelMid.add(dateChooserEnd);
    eastPanelMid.add(timeSpinnerEnd);

    // east panel bottom
    viewLinePlot = new JButton("View Usage Over Time");
    viewLinePlot.setEnabled(false);
    viewLinePlot.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        String start = null, end = null;

        try
        {
          start = getDateTimeString(dateChooserStart, timeSpinnerStart);
          end = getDateTimeString(dateChooserEnd, timeSpinnerEnd);
        }
        catch( ParseException e1 )
        {
          e1.printStackTrace();
        }

        UserLite selectedUser = userList.getSelectedValue();
        int user_id = selectedUser.getID();

        final String title = "Time Series Management";
        final ViewStatsGUI win = new ViewStatsGUI(title, user_id, start, end);
        win.pack();
        RefineryUtilities.positionFrameRandomly(win);
        win.setVisible(true);
      }
    });

    eastPanelBot.add(viewLinePlot);

    // add panels to main
    eastPanel.add(eastPanelTop);


    timeSpinnerStart = new JSpinner(modelStart);
    eastPanelTop.add(timeSpinnerStart);
    timeSpinnerStart.setValue(startDate);


    JComponent editorStart = new JSpinner.DateEditor(timeSpinnerStart,
        "HH:mm:ss");
    timeSpinnerStart.setEditor(editorStart);
    eastPanel.add(eastPanelMid);
    eastPanel.add(eastPanelBot);
    eastPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }

  public int getNumBlocks(String start, String end, int block_size)
  {
    final org.joda.time.format.DateTimeFormatter format = DateTimeFormat
        .forPattern("yyyy-MM-dd HH:mm:ss");
    final DateTime date1 = format.parseDateTime(start);
    final DateTime date2 = format.parseDateTime(end);

    return Seconds.secondsBetween(date1, date2).getSeconds() / block_size;
  }

  public String[] getXAxisLabels(String start, String end)
      throws ParseException
  {
    int block_size = Main.settings.block_time / 1000; // to seconds
    int numBlocks = getNumBlocks(start, end, block_size);

    System.out.println("block size: " + block_size);

    String[] labels = new String[numBlocks + 1];

    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final Date date = df.parse(start); // conversion from String
    final java.util.Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(date);

    for( int i = 0; i <= numBlocks; i++ )
    {
      labels[i] = df.format(cal.getTime());
      cal.add(GregorianCalendar.SECOND, block_size);
    }
    return labels;
  }

  public Date roundToHr(Date d)
  {
    Calendar date = new GregorianCalendar();
    date.setTime(d);
    int deltaHr = date.get(Calendar.MINUTE) / 30;

    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    date.set(Calendar.MINUTE, 0);
    date.add(Calendar.HOUR, deltaHr);

    return date.getTime();
  }

  public Date roundToMin(Date d)
  {
    Calendar date = new GregorianCalendar();
    date.setTime(d);
    int deltaHr = date.get(Calendar.SECOND) / 30;

    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    date.add(Calendar.MINUTE, deltaHr);

    return date.getTime();
  }

  private String getDateTimeString(JDateChooser date, JSpinner time)
      throws ParseException
  {
    String dateTime;
    SimpleDateFormat fmt 
      = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dateFormat.format(date.getDate());

    SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss");
    String formattedTime = timeFormat.format(time.getValue());


    Date result = fmt.parse(formattedDate + " " + formattedTime);
    dateTime = fmt.format(result);

    return dateTime;
  }
}
