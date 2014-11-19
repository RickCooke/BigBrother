package BigBrother.Classes;

/*comment to later be removed*/

public class App {
    private int appID = -1;
    private int count = 0;
    private String alias = "";
    private String window = null;
    private boolean window_regex = false;
    private String process = null;
    private boolean process_regex = false;

    public App() {}

    public App(int _appID, String _alias, String _window, boolean _windowIsRegex, String _process, boolean _processIsRegex) {
        appID = _appID;
        alias = _alias;
        window = _window;
        window_regex = _windowIsRegex;
        process = _process;
        process_regex = _processIsRegex;
    }

    public void print() {
        System.out.println(appID + " " + alias + " " + window + " " + window_regex + " " + process + " " + process_regex);
    }

    public void print2() {
        System.out.printf("% 10d : ", count);
        System.out.println(alias + " (" + appID + ")");
    }

    public void clear() {
        count = 0;
    }

    public boolean isMatch(String windowTitle, String processName) {
        boolean windowMatch = false;
        boolean processMatch = false;

        if (appID == 0 || appID == 1) {
            return false;
        }
        
        // TODO: catch cases where both window name and process name are empty,
        // right now one instance of that would match everything

        // If window was not specified, then it automatically matches
        if (window == null || window.isEmpty()) {
            windowMatch = true;
        } else {
            // A window was specified, if its marked as regex do a regex match,
            // otherwise normal
            if (window_regex && windowTitle.matches(window)) {
                windowMatch = true;
            } else if (windowTitle.toLowerCase().equals(window.toLowerCase())) {
                windowMatch = true;
            }
        }

        // If process was not specified, then it automatically matches
        if (process == null || process.isEmpty()) {
            processMatch = true;
        } else {
            if (process_regex && processName.matches(process)) {
                processMatch = true;
            } else if (processName.toLowerCase().equals(process.toLowerCase())) {
                processMatch = true;
            }
        }
    

        if (windowMatch && processMatch)
            return true;
        else
            return false;
    }

    public void addCount(long addCount) {
        count += addCount;
    }

    public int getCount() {
        return count;
    }

    public String getAlias() {
        return alias;
    }

    public int getAppID() {
        return appID;
    }
}
