package BigBrother.Exceptions;

public class MultipleResultsFoundException extends Exception
{
  public MultipleResultsFoundException()
  {
    super();
  }

  public MultipleResultsFoundException(String message)
  {
    super(message);
  }

  public MultipleResultsFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public MultipleResultsFoundException(Throwable cause)
  {
    super(cause);
  }
}
