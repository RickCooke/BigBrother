package BigBrother.GUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

import BigBrother.Classes.App;
import BigBrother.Client.Main;
import BigBrother.Client.MySQL;

public class Time extends ApplicationFrame {
    public Time(final String title, int user_id,  String start, String end) {
        super(title);

        String[] xAxisLabels = null;
        try {
            xAxisLabels = getXAxisLabels(start, end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        ArrayList<App> apps = MySQL.getTrackedAppsArrayList(user_id);
        apps.add(new App(0, "Other", null, false, null, false, true));
        apps.add(new App(1, "Idle", null, false, null, false, true));

        int block_size = Main.settings.block_time/1000;
        int numBlocks = getNumBlocks(start, end, block_size);

        
        MilliDTSC dataset = new MilliDTSC(100, numBlocks, new MultipleOfMillisecond(Main.settings.block_time));
        dataset.setTimeBase(new MultipleOfMillisecond(Main.settings.block_time));
        

        int i = 0;
        for (App app : apps) {
            float[] appData = MySQL.getAppData2(user_id, app.appID, start, end, xAxisLabels);
            dataset.addSeries(appData, i, app.alias);
            i++;
        }

        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(final XYDataset dataset) {
        return ChartFactory.createTimeSeriesChart("Computing Test", "Time", "Seconds", dataset, true, true, false);
    }

    private static class MilliDTSC extends DynamicTimeSeriesCollection {

        public MilliDTSC(int nSeries, int nMoments, RegularTimePeriod timeSample) {
            super(nSeries, nMoments, timeSample);
            if (timeSample instanceof MultipleOfMillisecond) {
                this.pointsInTime = new MultipleOfMillisecond[nMoments];
            } else if (timeSample instanceof Millisecond) {
                this.pointsInTime = new Millisecond[nMoments];
            }
        }

    }

    public class MultipleOfMillisecond extends Millisecond {

        private static final long serialVersionUID = 1L;
        private int periodMs = 100;

        public MultipleOfMillisecond(int periodMs) {
            super();
            this.periodMs = periodMs;
        }

        public MultipleOfMillisecond(int periodMs, int millisecond, Second second) {
            super(millisecond, second);
            this.periodMs = periodMs;
        }

        @Override
        public RegularTimePeriod next() {

            RegularTimePeriod result = null;
            if (getMillisecond() + periodMs <= LAST_MILLISECOND_IN_SECOND) {
                result = new MultipleOfMillisecond(periodMs, (int) (getMillisecond() + periodMs), getSecond());
            } else {
                Second next = (Second) getSecond().next();
                if (next != null) {
                    result = new MultipleOfMillisecond(periodMs, (int) (getMillisecond() + periodMs - LAST_MILLISECOND_IN_SECOND - 1), next);
                }
            }
            return result;

        }

        @Override
        public RegularTimePeriod previous() {

            RegularTimePeriod result = null;
            if (getMillisecond() - periodMs >= FIRST_MILLISECOND_IN_SECOND) {
                result = new MultipleOfMillisecond(periodMs, (int) getMillisecond() - periodMs, getSecond());
            } else {
                Second previous = (Second) getSecond().previous();
                if (previous != null) {
                    result = new MultipleOfMillisecond(periodMs, (int) (getMillisecond() - periodMs + LAST_MILLISECOND_IN_SECOND + 1), previous);
                }
            }
            return result;

        }
    }

    
    public int getNumBlocks(String start, String end, int block_size) {
      final org.joda.time.format.DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
      final DateTime date1 = format.parseDateTime(start);
      final DateTime date2 = format.parseDateTime(end);
      return Seconds.secondsBetween(date1, date2).getSeconds() / block_size;
  }
    
    public String[] getXAxisLabels(String start, String end) throws ParseException {
      int block_size = Main.settings.block_time / 1000; // to seconds
      int numBlocks = getNumBlocks(start, end, block_size);

      System.out.println("block size: " + block_size);

      String[] labels = new String[numBlocks + 1];

      final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      final Date date = df.parse(start); // conversion from String
      final java.util.Calendar cal = GregorianCalendar.getInstance();
      cal.setTime(date);

      for (int i = 0; i <= numBlocks; i++) {
          labels[i] = df.format(cal.getTime());
          cal.add(GregorianCalendar.SECOND, block_size);
      }
      return labels;
  }
}
