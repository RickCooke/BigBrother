package BigBrother.Exceptions;

@SuppressWarnings("serial")
public class CountMismatchException extends Exception {
  public CountMismatchException() { super(); }
  public CountMismatchException(String message) { super(message); }
  public CountMismatchException(String message, Throwable cause) { super(message, cause); }
  public CountMismatchException(Throwable cause) { super(cause); }
}
