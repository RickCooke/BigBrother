package BigBrother.Classes;

public class AppLite {
    private int appID;
    private String alias;

    public AppLite(int _appID, String _alias) {
        appID = _appID;
        alias = _alias;
    }

    public String toString() {
        return alias + " (appID: " + appID + ")";
    }

    public int getID() {
        return appID;
    }

    public String getAlias() {
        return alias;
    }
}
