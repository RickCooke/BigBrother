package BigBrother.Classes;

public class AppLite {
    protected int appID;
    protected String alias;

    public AppLite(int _appID, String _alias) {
        appID = _appID;
        alias = _alias;
    }

    public String toString() {
        return alias + " (appID: " + appID + ")";
    }
}
