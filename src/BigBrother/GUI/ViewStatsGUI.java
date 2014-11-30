package BigBrother.GUI;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;

public class ViewStatsGUI extends JFrame
{
  public ViewStatsGUI(DefaultCategoryDataset data) {
    super("User Data");
    
    final JFreeChart chart = createChart(data);
    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(chartPanel);
    
  }
  
  private JFreeChart createChart(final DefaultCategoryDataset dataset) {
    
    // create the chart...
    final JFreeChart chart = ChartFactory.createLineChart(
        "App Usage Chart",         // chart title
        "Time",                    // domain axis label
        "Seconds",                   // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        true,                      // tooltips
        false                      // urls
    );
    
    chart.setBackgroundPaint(Color.white);

    final CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.white);
    
    return chart;
  }
}
