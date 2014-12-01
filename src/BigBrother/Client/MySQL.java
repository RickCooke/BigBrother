package BigBrother.Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import BigBrother.Classes.App;
import BigBrother.Classes.AppLite;
import BigBrother.Classes.Settings;
import BigBrother.Classes.User;
import BigBrother.Classes.UserLite;
import BigBrother.Exceptions.DuplicateKeyException;
import BigBrother.Exceptions.MalformedSettingsException;
import BigBrother.Exceptions.MultipleResultsFoundException;
import BigBrother.Exceptions.NoResultsFoundException;
import BigBrother.Exceptions.NoSettingsException;
import BigBrother.Exceptions.UserDoesNotExist;

public class MySQL
{
  private static Connection conn = null;

  public static void establishConnection()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      conn = DriverManager.getConnection("jdbc:mysql://"
          + Main.settings.MySQL_host + "/" + Main.settings.MySQL_database
          + "?user=" + Main.settings.MySQL_username + "&password="
          + Main.settings.MySQL_password);
    }
    catch( SQLException ex )
    {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    catch( Exception ex )
    {
      System.out.println("Exception: " + ex.getMessage());
    }
  }


  public static int checkPassword(String username, String passwordHash)
      throws UserDoesNotExist
  {
    if( conn == null )
    {
      establishConnection();
    }

    int userid = -1;
    String SQL = "SELECT userid FROM users u WHERE u.username = ? "
        + "AND u.password = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setString(1, username);
      ps.setString(2, passwordHash);
      rs = ps.executeQuery();
      if( rs.next() )
      {
        userid = rs.getInt("userid");
      }
      else
      {
        throw new UserDoesNotExist("Username and password "
            + "combination was invalid");
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
    return userid;
  }

  public static void recieveSettings(Settings _settings)
      throws NoSettingsException
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "SELECT polling_interval, memory_flush_interval,"
        + " local_flush_interval, max_idle_time, start_time,"
        + " UNIX_TIMESTAMP(start_time) as start_time_timestamp,"
        + " block_time FROM settings";

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);
      rs = ps.executeQuery();
      if( rs.next() )
      {
        _settings.polling_interval = rs.getInt("polling_interval");
        _settings.memory_flush_interval = rs.getInt("memory_flush_interval");
        _settings.local_flush_interval = rs.getInt("local_flush_interval");
        _settings.max_idle_time = rs.getInt("max_idle_time");
        _settings.start_time_string = rs.getString("start_time");
        _settings.start_time = rs.getInt("start_time_timestamp");
        _settings.block_time = rs.getInt("block_time");
      }
      else
      {
        throw new NoSettingsException("There are no settings");
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

  public static ArrayList<App> getTrackedAppsArrayList(int userID)
  {

    ArrayList<App> userApps = new ArrayList<App>();

    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "SELECT a . * FROM users_apps u, apps a WHERE u.userid = "
        + "? AND u.appid = a.appid AND a.active = 1";

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setInt(1, userID);
      rs = ps.executeQuery();
      while( rs.next() )
      {
        App temp = new App(rs.getInt("appid"), rs.getString("alias"),
            rs.getString("window"), rs.getBoolean("window_regex"),
            rs.getString("process"), rs.getBoolean("process_regex"), true);
        userApps.add(temp);
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
    return userApps;
  }

  public static ArrayList<App> getTrackedAppsArrayListThroughDate(int userID,
      String start, String end)
  {

    ArrayList<App> userApps = new ArrayList<App>();

    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "SELECT * FROM apps a WHERE a.active = 1 AND a.appid "
        + "IN (SELECT DISTINCT s.appid FROM stats s WHERE s.blockid >= ? "
        + "AND s.blockid <= ? AND userid = ?)";

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setString(1, start);
      ps.setString(2, end);
      ps.setInt(3, userID);
      rs = ps.executeQuery();
      while( rs.next() )
      {
        App temp = new App(rs.getInt("appid"), rs.getString("alias"),
            rs.getString("window"), rs.getBoolean("window_regex"),
            rs.getString("process"), rs.getBoolean("process_regex"), true);
        userApps.add(temp);
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
    return userApps;
  }

  public static void updateTrackedAppDLM(int userID,
      DefaultListModel<AppLite> dlm)
  {
    // clear the existing list
    dlm.clear();

    // make sure connection is sound
    if( conn == null )
      establishConnection();

    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;

    String SQL = "SELECT a.appid, a.alias FROM users_apps u, "
        + "apps a WHERE u.userid = ? AND u.appid = a.appid AND a.active = 1";

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setInt(1, userID);

      // execute query
      rs = ps.executeQuery();

      while( rs.next() )
        dlm.addElement(new AppLite(rs.getInt("appid"), rs.getString("alias")));
    }
    catch( SQLException e )
    {
      System.out.println("SQLException: " + e.getMessage());
    }
  }

  public static void updateNonTrackedAppDLM(int userID,
      DefaultListModel<AppLite> dlm)
  {
    // clear the existing list
    dlm.clear();

    // make sure connection is sound
    if( conn == null )
      establishConnection();

    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;


    String SQL = "SELECT appid, alias FROM apps WHERE active=1 AND appid NOT IN"
        + " (SELECT a.appid FROM users_apps u, apps a WHERE u.userid = ?"
        + " AND u.appid = a.appid AND a.active = 1) AND appid != 0"
        + " AND appid != 1";

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setInt(1, userID);

      // execute query
      rs = ps.executeQuery();

      while( rs.next() )
      {
        dlm.addElement(new AppLite(rs.getInt("appid"), rs.getString("alias")));
      }
    }
    catch( SQLException e )
    {
      System.out.println("SQLException: " + e.getMessage());
    }
  }

  public static DefaultListModel<UserLite> getUserList(
      DefaultListModel<UserLite> dlm)
  {
    // clear the existing list
    dlm.clear();

    // make sure connection is sound
    if( conn == null )
      establishConnection();

    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;
    String SQL = "SELECT u.userid, u.username, u.firstname, "
        + "u.lastname FROM users u";

    try
    {
      ps = conn.prepareStatement(SQL);

      // execute query
      rs = ps.executeQuery();

      while( rs.next() )
        dlm.addElement(new UserLite(rs.getInt("userid"), rs
            .getString("username"), rs.getString("firstname"), rs
            .getString("lastname")));
    }
    catch( SQLException e )
    {
      System.out.println("SQLException: " + e.getMessage());
    }

    return dlm;
  }

  public static DefaultListModel<AppLite> getActiveAppList(
      DefaultListModel<AppLite> dlm)
  {
    // clear the existing list
    dlm.clear();

    // make sure connection is sound
    if( conn == null )
      establishConnection();

    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;
    String SQL = "SELECT a.appid, a.alias FROM apps a WHERE a.active = 1";

    try
    {
      ps = conn.prepareStatement(SQL);

      // execute query
      rs = ps.executeQuery();

      while( rs.next() )
        dlm.addElement(new AppLite(rs.getInt("appid"), rs.getString("alias")));
    }
    catch( SQLException e )
    {
      System.out.println("SQLException: " + e.getMessage());
    }

    return dlm;
  }

  public static void flushLocalBuffer(ArrayList<int[]> buffer)
  {
    int rows = 0;

    if( conn == null )
    {
      establishConnection();
    }

    String UPDATE_SQL = "UPDATE stats SET count = count + ? "
        + "WHERE blockid = FROM_UNIXTIME(?) AND userid = ? AND appid = ?";
    String INSERT_SQL = "INSERT INTO stats (blockid, userid, appid,"
        + " count) VALUES (FROM_UNIXTIME(?), ?, ?, ?)";

    PreparedStatement ps = null;
    PreparedStatement ps2 = null;

    try
    {
      ps = conn.prepareStatement(UPDATE_SQL);
      ps2 = conn.prepareStatement(INSERT_SQL);

      for( int[] b : buffer )
      {
        int blockid = b[0];
        int userid = b[1];
        int appid = b[2];
        int count = b[3];

        ps.setInt(1, count);
        ps.setInt(2, blockid);
        ps.setInt(3, userid);
        ps.setInt(4, appid);

        rows = ps.executeUpdate();
        // If there has been a block change, or first time seeing
        // INSERT into stats instead of UPDATING
        if( rows == 0 )
        {
          ps2.setInt(1, blockid);
          ps2.setInt(2, userid);
          ps2.setInt(3, appid);
          ps2.setInt(4, count);

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

  public static User getUser(int userID) throws MultipleResultsFoundException,
      NoResultsFoundException, SQLException
  {
    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;
    String SQL = "SELECT u.userid, u.username, u.firstname, u.lastname, "
        + "u.group FROM users u WHERE u.userid = ?";

    ps = conn.prepareStatement(SQL);
    ps.setInt(1, userID);

    // execute query
    rs = ps.executeQuery();
    rs.next();

    // should only get one result
    if( rs.isFirst() && rs.isLast() )
    {
      User ret = new User();
      ret.userID = userID;
      ret.username = rs.getString("username");
      ret.firstName = rs.getString("firstname");
      ret.lastName = rs.getString("lastname");
      ret.groupNum = rs.getInt("group");
      return ret;
    }
    else if( !rs.first() )
      throw new NoResultsFoundException("No users found with userID #" + userID
          + ".");
    else
      throw new MultipleResultsFoundException(
          "Multiple users found with userID #" + userID + ".");
  }

  public static App getApp(int appID) throws MultipleResultsFoundException,
      NoResultsFoundException, SQLException
  {
    // prepare the query
    PreparedStatement ps = null;
    ResultSet rs = null;
    String SQL = "SELECT a.appid, a.alias, a.window, a.window_regex,"
        + " a.process, a.process_regex FROM apps a"
        + " WHERE a.appid = ? AND a.active = 1";

    ps = conn.prepareStatement(SQL);
    ps.setInt(1, appID);

    // execute query
    rs = ps.executeQuery();
    rs.next();

    // should only get one result
    if( rs.isFirst() && rs.isLast() )
    {
      App ret = new App(appID, rs.getString("alias"), rs.getString("window"),
          rs.getBoolean("window_regex"), rs.getString("process"),
          rs.getBoolean("process_regex"), true);
      return ret;
    }
    else if( !rs.first() )
      throw new NoResultsFoundException("No apps found with appID #" + appID
          + ".");
    else
      throw new MultipleResultsFoundException(
          "Multiple apps found with appID #" + appID + ".");
  }

  // Serves as an alias for editUser
  public static int addUser(User user, String password)
      throws DuplicateKeyException
  {
    int last_inserted_id = -1;
    if( user.userID == -1 )
    {
      last_inserted_id = editUser(user, password);
    }
    else
    {
      System.err
          .println("MySQL.addUser was called with a userID that wasn't -1");
    }
    return last_inserted_id;
  }

  public static int editApp(App newApp)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL;

    if( newApp.getAppID() == -1 )
    {
      SQL = "INSERT INTO apps (`appid`, `alias`, `window`, `window_regex`, "
          + "`process`, `process_regex`, `active`) "
          + "VALUES (?, ?, ?, ?, ?, ?, 1);";
    }
    else
    {
      SQL = "REPLACE INTO apps (`appid`, `alias`, `window`, `window_regex`, "
          + "`process`, `process_regex`, `active`) "
          + "VALUES (?, ?, ?, ?, ?, ?, 1);";
    }

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS);

      if( newApp.getAppID() == -1 )
      {
        ps.setNull(1, java.sql.Types.INTEGER);
      }
      else
      {
        ps.setInt(1, newApp.getAppID());
      }


      ps.setString(2, newApp.alias);
      ps.setString(3, newApp.window);
      ps.setBoolean(4, newApp.window_regex);
      ps.setString(5, newApp.process);
      ps.setBoolean(6, newApp.process_regex);

      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if( rs.next() )
      {
        return rs.getInt(1);
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

    return -1;
  }

  public static int editUser(User user, String password)
      throws DuplicateKeyException
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL;

    if( user.userID == -1 )
    {
      SQL = "INSERT INTO users (`userid`, `username`, `password`, `firstname`,"
          + " `lastname`, `group`) VALUES (?, ?, MD5(?), ?, ?, ?);";
    }
    else
    {
      SQL = "REPLACE INTO users (`userid`, `username`, `password`, `firstname`,"
          + " `lastname`, `group`) VALUES (?, ?, MD5(?), ?, ?, ?);";
    }

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS);

      if( user.userID == -1 )
      {
        ps.setNull(1, java.sql.Types.INTEGER);
      }
      else
      {
        ps.setInt(1, user.userID);
      }


      ps.setString(2, user.username);
      ps.setString(3, password);
      ps.setString(4, user.firstName);
      ps.setString(5, user.lastName);

      if( user.groupNum == -1 )
      {
        ps.setNull(6, java.sql.Types.INTEGER);
      }
      else
      {
        ps.setInt(6, user.groupNum);
      }

      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if( rs.next() )
      {
        return rs.getInt(1);
      }

    }
    catch( SQLException ex )
    {
      if( ex.getSQLState().equals("23000") )
      {
        throw new DuplicateKeyException();
      }
      else
      {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
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

    return -1;
  }


  public static void deleteUser(int userID)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "DELETE FROM users WHERE userid = ?";


    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS);

      ps.setInt(1, userID);

      int rows = ps.executeUpdate();
      if( rows != 1 )
      {
        throw new SQLException("Did not DELETE user " + userID);
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

  public static void setAppTracked(int userID, int appID, boolean tracked)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL;
    if( tracked )
    {
      SQL = "INSERT INTO users_apps (userid, appid) VALUES (?, ?)";
    }
    else
    {
      SQL = "DELETE FROM users_apps WHERE userid = ? AND appid = ?";
    }
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);

      ps.setInt(1, userID);
      ps.setInt(2, appID);

      ps.executeUpdate();

    }
    catch( SQLException ex )
    {
      if( ex.getSQLState().equals("23000") )
      {
        try
        {
          throw new DuplicateKeyException();
        }
        catch( DuplicateKeyException e )
        {
          e.printStackTrace();
        }
      }
      else
      {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
      }
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

  public static void updateSettings(Settings settings)
      throws MalformedSettingsException
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "REPLACE INTO settings (`id`, `polling_interval`, "
        + "`memory_flush_interval`, `local_flush_interval`, `max_idle_time`, "
        + "`start_time`, `block_time`) VALUES (1, ?, ?, ?, ?, ?, ?)";


    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL, java.sql.Statement.RETURN_GENERATED_KEYS);

      ps.setFloat(1, settings.polling_interval);
      ps.setInt(2, settings.memory_flush_interval);
      ps.setInt(3, settings.local_flush_interval);
      ps.setInt(4, settings.max_idle_time);
      ps.setString(5, settings.start_time_string);
      ps.setInt(6, settings.block_time);

      int rows = ps.executeUpdate();
      // rows = 1, no updates needed
      // rows = 2 updates done
      if( rows != 1 && rows != 2 )
      {
        throw new MalformedSettingsException("Settings malformed");
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

  public static void deleteAppTrackFromAll(int appID)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "DELETE FROM users_apps WHERE appid = ?";


    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);

      ps.setInt(1, appID);

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

  public static void setAppInactive(int appID)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "UPDATE apps SET active = 0 WHERE appid = ?";


    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);

      ps.setInt(1, appID);

      int rows = ps.executeUpdate();
      if( rows != 1 )
      {
        throw new SQLException("Did not UPDATE app " + appID);
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

  /**
   * functions for data visualization
   */
  public static Integer[] getTrackedApps(int user_id, String start, String end)
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "SELECT DISTINCT appid FROM stats "
        + "WHERE blockid >= ? AND blockid <= ? AND userid = ?";
    Integer[] trackedApps = null;

    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = conn.prepareStatement(SQL);
      ps.setString(1, start);
      ps.setString(2, end);
      ps.setInt(3, user_id);
      rs = ps.executeQuery();

      ArrayList<Integer> trackedAppsList = new ArrayList<Integer>();
      while( rs.next() )
      {
        trackedAppsList.add(rs.getInt(1));
      }
      trackedApps = trackedAppsList
          .toArray(new Integer[trackedAppsList.size()]);
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

    return trackedApps;
  }

  public static Integer[] getAppData(int user_id, int app_id, String start,
      String end, String[] labels)
  {
    if( conn == null )
    {
      establishConnection();
    }


    String SQL = "SELECT appid, blockid, count FROM stats WHERE blockid >= ? "
        + "AND blockid <= ? AND userid = ? AND appid = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;

    Integer[] data = null;

    try
    {
      ps = conn.prepareStatement(SQL);

      ps.setString(1, start);
      ps.setString(2, end);
      ps.setInt(3, user_id);
      ps.setInt(4, app_id);

      rs = ps.executeQuery();
      rs.last();
      data = new Integer[labels.length];
      rs.beforeFirst();

      for( int i = 0; i < data.length; i++ )
      {
        data[i] = 0; // initialize all to zero
      }

      while( rs.next() )
      {
        String date = rs.getString(2);
        date = date.substring(0, date.length() - 2);
        for( int i = 0; i < labels.length; i++ )
        {
          // if label exists, add it to the data array
          if( labels[i].equals(date) )
          {
            data[i] = rs.getInt(3) / 1000;
            break;
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
    return data;
  }

  public static float[] getAppData2(int user_id, int app_id, String start,
      String end, String[] labels)
  {
    if( conn == null )
    {
      establishConnection();
    }


    String SQL = "SELECT appid, blockid, count FROM stats WHERE blockid >= ? "
        + "AND blockid <= ? AND userid = ? AND appid = ?";

    PreparedStatement ps = null;
    ResultSet rs = null;

    float[] data = null;

    try
    {
      ps = conn.prepareStatement(SQL);

      ps.setString(1, start);
      ps.setString(2, end);
      ps.setInt(3, user_id);
      ps.setInt(4, app_id);

      rs = ps.executeQuery();
      rs.last();
      data = new float[labels.length];
      rs.beforeFirst();

      for( int i = 0; i < data.length; i++ )
      {
        data[i] = 0; // initialize all to zero
      }

      while( rs.next() )
      {
        String date = rs.getString(2);

        date = date.substring(0, date.length() - 2);
        for( int i = 0; i < labels.length; i++ )
        {
          // if label exists, add it to the data array
          if( labels[i].equals(date) )
          {
            data[i] = rs.getInt(3) / 1000;
            break;
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
    return data;
  }

  public static void clearStats()
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "TRUNCATE stats";


    PreparedStatement ps = null;
    ResultSet rs = null;
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

  public static int getStatCount()
  {
    if( conn == null )
    {
      establishConnection();
    }

    String SQL = "SELECT COUNT(*) FROM stats";


    PreparedStatement ps = null;
    ResultSet rs = null;
    int rowCount = 0;

    try
    {
      ps = conn.prepareStatement(SQL);

      rs = ps.executeQuery();
      if( rs.next() )
      {
        rowCount = rs.getInt(1);
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
    return rowCount;
  }

}
