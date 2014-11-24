package BigBrother.Classes;

public class UserLite {
    protected int userID;
    protected String username;
    protected String firstName;
    protected String lastName;

    public UserLite(int _userID, String _username, 
        String _firstName, String _lastName) {
        userID = _userID;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
    }

    public String toString() {
        return lastName + ", " + firstName + " (" + username + ")";
    }

    public int getUserID() {
        return userID;
    }
}
