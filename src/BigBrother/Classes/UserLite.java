package BigBrother.Classes;

// A "lite" version of a user so unneeded data did not need to be stored in memory.
// This helps when the user database is very large, which it could potentially be.
public class UserLite implements Comparable <UserLite> {
    private int userID;
    private String username;
    private String firstName;
    private String lastName;

    //default constructor
    public UserLite(int _userID, String _username, String _firstName, String _lastName) {
        userID = _userID;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
    }

    //String representation of a UserLite
    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    //return the userID variable
    public int getID() {
        return userID;
    }

    //alphabetically compare this UserLite's string representation to that of another, ignoring case
	@Override
	public int compareTo(UserLite arg0) {
		return toString().toLowerCase().compareTo(arg0.toString().toLowerCase());
	}
	
}
