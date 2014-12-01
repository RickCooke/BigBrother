package BigBrother.Classes;

public class AppLite implements Comparable <AppLite> {
    private int appID;
    private String alias;

    public AppLite(int _appID, String _alias) {
        appID = _appID;
        alias = _alias;
    }

    public String toString() {
        return alias;
    }

    public int getID() {
        return appID;
    }

    public String getAlias() {
        return alias;
    }

	@Override
	public int compareTo(AppLite arg0) {
		return toString().toLowerCase().compareTo(arg0.toString().toLowerCase());
	}
}
