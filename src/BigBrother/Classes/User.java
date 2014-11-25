package BigBrother.Classes;

import java.util.ArrayList;

public class User {
	public int userID; // do we really need this? userID can just be the index of the array we make
    public String username;
    public String firstName;
    public String lastName;
    public int groupNum;
    private ArrayList<Integer> appsTracked; // list of appIDs being tracked for this user

    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }
}
