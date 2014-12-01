package BigBrother.Classes;

//A stripped down view of apps, so we don't have to store unneeded data for a potentially huge set of applications
public class AppLite implements Comparable<AppLite>
{
  private int appID;
  private String alias;

  // Constructor
  public AppLite(int _appID, String _alias)
  {
    appID = _appID;
    alias = _alias;
  }

  // just return the alias for display
  public String toString()
  {
    return alias;
  }

  // return the appID variable
  public int getID()
  {
    return appID;
  }

  // return the alias variable
  public String getAlias()
  {
    return alias;
  }

  // compares the string representations of an AppLite instance to this one,
  // ignoring case
  @Override
  public int compareTo(AppLite arg0)
  {
    return toString().toLowerCase().compareTo(arg0.toString().toLowerCase());
  }
}
