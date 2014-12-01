package BigBrother.Client;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import BigBrother.Classes.App;


public class SQLite
{
  private static Connection conn = null;
  private static PreparedStatement ps = null;
  private static ResultSet rs = null;
  private static boolean tableCreated = false;

  public static void establishConnection()
  {
    try
    {
      Class.forName("org.sqlite.JDBC").newInstance();
      conn = DriverManager.getConnection("jdbc:sqlite:local.db");

      String SQL = "SELECT name FROM sqlite_master WHERE type = 'table' "
          + "AND name = ?;";

      ps = conn.prepareStatement(SQL);
      ps.setString(1, "stats");
      rs = ps.executeQuery();
      if( rs.next() )
      {
        tableCreated = true;
      }
      else
      {
        createTables();
      }

    }
    catch( SQLException ex )
    {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    catch( Exception ex )
    {
      System.out.println("Exception: " + ex.getMessage());
    }
    finally
    {
      try
      {
        if( rs != null )
        {
          rs.close();
          rs = null;
        }

        if( ps != null )
        {
          ps.close();
          ps = null;
        }
      }
      catch( SQLException ex )
      {
        System.out.println("SQLException: " + ex.getMessage());
      }
    }
  }


  public static void createTables()
  {
    System.out.println("createTables");
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "CREATE TABLE `stats` (" + "`blockid` INTEGER,"
        + "`userid` INTEGER," + "`appid` INTEGER," + "`count` INTEGER,"
        + "PRIMARY KEY(blockid,userid,appid)" + ");";

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.executeUpdate();


      tableCreated = true;
    }
    catch( SQLException ex )
    {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    finally
    {
      try
      {
        if( ps != null )
        {
          ps.close();
          ps = null;
        }
      }
      catch( SQLException ex )
      {
        System.out.println("SQLException: " + ex.getMessage());
      }
    }
  }


  public static void clearStats()
  {
    if( conn == null )
    {
      establishConnection();
    }

    if( tableCreated == false )
    {
      createTables();
    }

    String SQL = "DELETE FROM stats; VACUUM;";
    try
    {
      ps = conn.prepareStatement(SQL);
      ps.executeUpdate();
    }
    catch( SQLException ex )
    {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    finally
    {
      try
      {
        if( ps != null )
        {
          ps.close();
          ps = null;
        }
      }
      catch( SQLException ex )
      {
        System.out.println("SQLException: " + ex.getMessage());
      }
    }
  }

  public static void pushToRemote()
  {
    if( conn == null )
    {
      establishConnection();
    }

    if( tableCreated == false )
    {
      return;
    }

    String SQL = "SELECT * FROM stats;";
    try
    {


      ps = conn.prepareStatement(SQL);
      rs = ps.executeQuery();

      ArrayList<int[]> buffer = new ArrayList<int[]>();

      int i = 0;
      while( rs.next() )
      {
        int temp[] = new int[4];
        temp[0] = rs.getInt("blockid");
        temp[1] = rs.getInt("userid");
        temp[2] = rs.getInt("appid");
        temp[3] = rs.getInt("count");
        buffer.add(temp);
        i++;
        if( i % Main.settings.remote_insert_buffer_size == 0 )
        {
          MySQL.flushLocalBuffer(buffer);
          buffer.clear();
        }
      }

      MySQL.flushLocalBuffer(buffer);
    }
    catch( SQLException ex )
    {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    catch( Exception ex )
    {
      System.out.println("Exception: " + ex.getMessage());
    }
    finally
    {
      try
      {
        if( rs != null )
        {
          rs.close();
          rs = null;
        }

        if( ps != null )
        {
          ps.close();
          ps = null;
        }
      }
      catch( SQLException ex )
      {
        System.out.println("SQLException: " + ex.getMessage());
      }
    }
  }


  public static void flushMemory(ArrayList<App> userApps)
  {
    int rows = 0;

    if( conn == null )
    {
      establishConnection();
    }

    if( tableCreated == false )
    {
      createTables();
    }

    int unixTime = (int) (System.currentTimeMillis() / 1000L);

    int block_time_in_seconds = Main.settings.block_time / 1000;

    int numBlocks = (unixTime - Main.settings.start_time)
        / block_time_in_seconds;
    int blockid = (Main.settings.start_time + (numBlocks * block_time_in_seconds));

    if( Main.settings.debug )
    {
      Date date2 = new Date(blockid * 1000L);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
      System.out.println("This blockID: " + sdf.format(date2));
    }

    String UPDATE_SQL = "UPDATE stats SET count = count + ? WHERE "
        + "blockid = ? AND userid = ? AND appid = ?";
    String INSERT_SQL = "INSERT INTO stats (blockid, userid, appid,"
        + " count) VALUES (?, ?, ?, ?)";

    PreparedStatement ps2 = null;
    try
    {
      ps = conn.prepareStatement(UPDATE_SQL);
      ps2 = conn.prepareStatement(INSERT_SQL);

      for( App a : userApps )
      {
        ps.setInt(1, a.getCount());
        ps.setInt(2, blockid);
        ps.setInt(3, Main.loggedInUserID);
        ps.setInt(4, a.getAppID());

        rows = ps.executeUpdate();
        // If there has been a block change, or first time seeing
        // INSERT into stats instead of UPDATING
        if( rows == 0 )
        {
          ps2.setInt(1, blockid);
          ps2.setInt(2, Main.loggedInUserID);
          ps2.setInt(3, a.getAppID());
          ps2.setInt(4, a.getCount());

          rows = ps2.executeUpdate();
          if( rows == 0 )
          {
            // Says ps2 is leaked, but the finally below will catch this
            throw new SQLException("Unable to INSERT");
          }
        }
      }

    }
    catch( SQLException ex )
    {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    finally
    {
      try
      {
        if( ps != null )
        {
          ps.close();
          ps = null;
        }
        if( ps2 != null )
        {
          ps2.close();
          ps2 = null;
        }
      }
      catch( SQLException ex )
      {
        System.out.println("SQLException: " + ex.getMessage());
      }
    }
  }
}
