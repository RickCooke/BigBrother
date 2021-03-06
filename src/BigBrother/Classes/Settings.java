package BigBrother.Classes;

import javax.swing.JOptionPane;

import BigBrother.Client.MySQL;
import BigBrother.Exceptions.NoSettingsException;

public class Settings
{

  public final boolean debug = true;

  public long polling_interval; // the interval in ms between polls
  public int memory_flush_interval; // the interval in ms between when data is
                                    // flushed from memory to the local sqlite
                                    // DB
  public int local_flush_interval; // the interval in ms between when data is
                                   // flushed from the local sqlite DB to the
                                   // external mySQL DB
  public int max_idle_time; // the interval in ms before the "idle" flag is
                            // raised
  public int block_time; // the duration in ms of one "block" of time
  public int start_time; // the start date that data began being collected
  public String start_time_string; // the start date that data began being
                                   // collected
  public int keyboard_peek_interval = 10; // the interval in ms at which the app
                                          // looks at keyboard use to check idle
  public int remote_insert_buffer_size = 100;

  // Hardcoded external MySQL server location & credentials
  public final String MySQL_host = "23.94.98.164";
  public final String MySQL_database = "bigbrother";
  public final String MySQL_username = "bigbrother";
  public final String MySQL_password = "plzletmein";

  // Download the existing settings from the database
  public void downloadSettings()
  {
    if( debug )
      System.out.println("Downloading Settings...");

    // try to pull them from SQL
    try
    {
      MySQL.recieveSettings(this);
    }
    catch( NoSettingsException e )
    {
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    if( debug )
      System.out.println("Finished!");
  }

  // print out settings (should only be used in debug mode)
  public String toString()
  {
    String retStr = "";

    retStr += "Settings:\n";
    retStr += "polling_interval: " + polling_interval + "\n";
    retStr += "memory_flush_interval: " + memory_flush_interval + "\n";
    retStr += "local_flush_interval: " + local_flush_interval + "\n";
    retStr += "max_idle_time: " + max_idle_time + "\n";
    retStr += "block_time: " + block_time + "\n";
    retStr += "start_time: " + start_time_string + " (" + start_time + ")\n";
    retStr += "keyboard_peek_interval: " + keyboard_peek_interval + "\n";
    retStr += "remote_insert_buffer_size: " + remote_insert_buffer_size + "\n";

    return retStr;
  }

}
