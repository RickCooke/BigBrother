package BigBrother.Classes;

import java.util.ArrayList;

public class User {
    public int userID; // do we really need this? userID can just be the index of the array we make
    public String username;
    public String firstName;
    public String lastName;
    public int groupNum;
    private ArrayList<Integer> appsTracked; // list of appIDs being tracked for this user (we never
                                            // actually use this, FYI)

    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    public User(int _userID, String _username, String _firstName, String _lastName, int _groupNum) {
        userID = _userID;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
        groupNum = _groupNum;
    }

    public User(int _userID, String _username, String _firstName, String _lastName, String _groupNum) {
        userID = _userID;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
        try {
            groupNum = Integer.parseInt(_groupNum);
        } catch (java.lang.NumberFormatException e) {
            groupNum = -1;
        }
    }

    public User() {
        // TODO Auto-generated constructor stub
    }


}
