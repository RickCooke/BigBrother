package BigBrother.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import BigBrother.Classes.App;
import BigBrother.Classes.AppLite;
import BigBrother.Classes.UserLite;
import BigBrother.Exceptions.NoSettingsException;
import BigBrother.Exceptions.UserDoesNotExist;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class MySQL {
    private static Connection conn = null;

    public static void establishConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + Main.MySQL_host + "/" + Main.MySQL_database + "?user=" + Main.MySQL_username + "&password=" + Main.MySQL_password);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }


    public static int checkPassword(String username, String passwordHash) throws UserDoesNotExist {
        if (conn == null) {
            establishConnection();
        }

        int userid = -1;
        String SQL = "SELECT userid FROM users u WHERE u.username = ? AND u.password = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            rs = ps.executeQuery();
            if (rs.next()) {
                userid = rs.getInt("userid");
            } else {
                throw new UserDoesNotExist("Username and password combination was invalid");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }

                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        }
        return userid;
    }

    public static void getSettings() throws NoSettingsException {
        if (conn == null) {
            establishConnection();
        }

        String SQL = "SELECT * FROM settings";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            if (rs.next()) {
                Main.polling_interval = rs.getInt("polling_interval");
                Main.memory_flush_interval = rs.getInt("memory_flush_interval");
                Main.local_flush_interval = rs.getInt("local_flush_interval");
                Main.max_idle_time = rs.getInt("max_idle_time");
                Main.start_time = rs.getInt("start_time");
                Main.block_time = rs.getInt("block_time");
            } else {
                throw new NoSettingsException("There are no settings");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }

                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        }

    }

    public static ArrayList<App> getTrackedAppsArrayList(int userID) {

        ArrayList<App> userApps = new ArrayList<App>();

        if (conn == null) {
            establishConnection();
        }

        String SQL = "SELECT a . * FROM users_apps u, apps a WHERE u.userid = ? AND u.appid = a.appid";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                App temp = new App(rs.getInt("appid"), rs.getString("alias"), rs.getString("window"), rs.getBoolean("window_regex"), rs.getString("process"), rs.getBoolean("process_regex"));
                userApps.add(temp);
            }

            // If no user apps, then assume they just want to monitor idleness?
            // if(userApps.isEmpty()) {
            // throw new HasNoUserApps("You currently have no applications set to you")
            // }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }

                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
            }
        }
        return userApps;
    }

    public static DefaultListModel<UserLite> getUserList(DefaultListModel<UserLite> dlm) {
        // clear the existing list
        dlm.clear();

        // make sure connection is sound
        if (conn == null)
            establishConnection();

        // prepare the query
        PreparedStatement ps = null;
        ResultSet rs = null;
        String SQL = "SELECT u.userid, u.username, u.firstname, u.lastname FROM users u";

        try {
            ps = conn.prepareStatement(SQL);

            // execute query
            rs = ps.executeQuery();

            while (rs.next())
                dlm.addElement(new UserLite(rs.getInt("userid"), rs.getString("username"), rs.getString("firstname"), rs.getString("lastname")));
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return dlm;
    }

    public static DefaultListModel<AppLite> getTrackedAppsDLM(int userID, DefaultListModel<AppLite> dlm) {
        // clear the existing list
        dlm.clear();

        // make sure connection is sound
        if (conn == null)
            establishConnection();

        // prepare the query
        PreparedStatement ps = null;
        ResultSet rs = null;
        String SQL = "SELECT a.appid, a.alias FROM users_apps u, apps a WHERE u.userid = ? AND u.appid = a.appid";

        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, userID);

            // execute query
            rs = ps.executeQuery();

            while (rs.next())
                dlm.addElement(new AppLite(rs.getInt("appid"), rs.getString("alias")));
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

        return dlm;
    }
}
