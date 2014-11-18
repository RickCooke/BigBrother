package BigBrother.Classes;

import java.util.ArrayList;

public class User {
    private int userID; // do we really need this? userID can just be the index of the array we make
    private String username;
    private String firstName;
    private String lastName;
    private int groupNum;
    private ArrayList<Integer> appsTracked; // list of appIDs being tracked for this user

    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }
}
