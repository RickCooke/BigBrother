package BigBrother.Classes;

import java.util.Comparator;

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

    //standard initialization constructor, sets all the class variables
    public App(int _appID, String _alias, String _window, boolean _windowIsRegex, String _process, boolean _processIsRegex, boolean _isActive) {
        appID = _appID;
        alias = _alias;
        window = _window;
        window_regex = _windowIsRegex;
        process = _process;
        process_regex = _processIsRegex;
        isActive = _isActive;
    }

    //return a string representation of the app in the format of "*alias* (appID: *appID*)"
    public String toString() {
        return alias + " (appID: " + appID + ")";
    }
    
    //print a detailed description of the app to the console (should be only used when Main.settings.debug == true)
    public void print() {
        System.out.println(appID + " " + alias + " " + window + " " + window_regex + " " + process + " " + process_regex + " " + getPriorityScore());

    }

    //print a detailed description of the app to the console (should be only used when Main.settings.debug == true)
    public void print2() {
        System.out.printf("% 10d : ", count);
        System.out.println(alias + " (" + appID + ")");
    }

    //clears the count representing the number of ticks this app has been in focus
    public void clear() {
        count = 0;
    }

    //priority score so we match more specific requests before less specific ones
    // Based on http://i.imgur.com/SREBZj2.png
    public int getPriorityScore() {
        int score = 0;

        if (alias == "Other")
            return 20;
        if (alias == "Idle")
            return 10;

        if (window != null)
            score += 3;
        if (process != null)
            score += 1;
        if (!window_regex)
            score += 1;
        if (!process_regex)
            score += 1;

        return score;
    }


    //Compare one app to another
    public class AppComparator implements Comparator<App> {
        @Override
        public int compare(App a1, App a2) {

        	//if priority score is the same, return the comparison of the appIDs
            if (a1.getPriorityScore() == a2.getPriorityScore()) {
                return Integer.compare(a1.getAppID(), a2.getAppID());
            }

            // Sort by highest priority score
            return Integer.compare(a2.getPriorityScore(), a1.getPriorityScore());
        }
    }

    //Check whether or not this app is a match for a specific windowTitle and processName
    public boolean isMatch(String windowTitle, String processName) {
        boolean windowMatch = false;
        boolean processMatch = false;

        //ignore appID 0 and 1, they are reserved for Other and Idle
        if (appID == 0 || appID == 1) {
            return false;
        }

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

        //return the result
        if (windowMatch && processMatch)
            return true;
        else
            return false;
    }

    //increment the count
    public void addCount(long addCount) {
        count += addCount;
    }

    //returns the count variable
    public int getCount() {
        return count;
    }

    //returns the alias variable
    public String getAlias() {
        return alias;
    }

    //returns the appID variable
    public int getAppID() {
        return appID;
    }
}
