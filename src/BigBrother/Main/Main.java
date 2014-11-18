package BigBrother.Main;

import java.awt.Dimension;

import javax.swing.WindowConstants;

import BigBrother.GUI.LoginGUI;


public class Main
{
  public final static boolean debug = true; //Set this to enable debug output
  public static int loggedInUserID;
  public static long polling_interval;
  public static int memory_flush_interval;
  public static int local_flush_interval;
  public static int max_idle_time;
  public static int block_time;
  public static int start_time;
  
  
  public static void main(String[] args)
  {
	LoginGUI win = new LoginGUI();
    win.setMinimumSize(new Dimension(200, 100));
    win.pack();
    win.setVisible(true);
    win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }
}