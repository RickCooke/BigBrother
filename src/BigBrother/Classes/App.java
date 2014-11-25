package BigBrother.Classes;

import java.util.Comparator;

/*comment to later be removed*/

public class App {
    public int appID = -1;
    public int count = 0;
    public String alias = "";
    public String window = null;
    public boolean window_regex = false;
    public String process = null;
    public boolean process_regex = false;
    public boolean isActive = true;

    public App() {}

    public App(int _appID, String _alias, String _window, 
        boolean _windowIsRegex, String _process, 
        boolean _processIsRegex, boolean _isActive) {
        appID = _appID;
        alias = _alias;
        window = _window;
        window_regex = _windowIsRegex;
        process = _process;
        process_regex = _processIsRegex;
        isActive = _isActive;
    }

    public void print() {
        System.out.println(appID + " " + alias + " " + window + " " 
    + window_regex + " " + process + " " + process_regex + " " + getPriorityScore());
  
    }

    public void print2() {
        System.out.printf("% 10d : ", count);
        System.out.println(alias + " (" + appID + ")");
    }

    public void clear() {
        count = 0;
    }

    public int getPriorityScore(){
        // Based on http://i.imgur.com/SREBZj2.png
        int score = 0;
        
        if(alias == "Other") return 20;
        if(alias == "Idle") return 10;
        
        if(window != null) score += 3;
        if(process != null) score += 1;
        if(!window_regex) score += 1;
        if(!process_regex) score += 1;
        
        return score; 
    }
    

    public class AppComparator implements Comparator<App> {
        @Override
        public int compare(App a1, App a2) {

            if (a1.getPriorityScore() == a2.getPriorityScore()){
                return Integer.compare(a1.getAppID(), a2.getAppID());
            }
            
            // Sort by highest priority score
            return Integer.compare(a2.getPriorityScore(), a1.getPriorityScore());
        }
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
            } else if (windowTitle.toLowerCase().equals
                (window.toLowerCase())) {
                windowMatch = true;
            }
        }

        // If process was not specified, then it automatically matches
        if (process == null || process.isEmpty()) {
            processMatch = true;
        } else {
            if (process_regex && processName.matches(process)) {
                processMatch = true;
            } else if (processName.toLowerCase().equals
                (process.toLowerCase())) {
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
