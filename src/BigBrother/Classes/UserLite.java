package BigBrother.Classes;

public class UserLite implements Comparable <UserLite> {
    private int userID;
    private String username;
    private String firstName;
    private String lastName;

    public UserLite(int _userID, String _username, String _firstName, String _lastName) {
        userID = _userID;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
    }

    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    public int getID() {
        return userID;
    }

	@Override
	public int compareTo(UserLite arg0) {
		return toString().toLowerCase().compareTo(arg0.toString().toLowerCase());
	}
	
}
