package BigBrother.Exceptions;

public class RequiredAppsNotFoundException extends Exception {
	  public RequiredAppsNotFoundException() { super(); }
	  public RequiredAppsNotFoundException(String message) { super(message); }
	  public RequiredAppsNotFoundException(String message, Throwable cause) { super(message, cause); }
	  public RequiredAppsNotFoundException(Throwable cause) { super(cause); }
}
