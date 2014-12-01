package BigBrother.Exceptions;

@SuppressWarnings("serial")
public class UserDoesNotExist extends Exception
{
  public UserDoesNotExist(String message)
  {
    super(message);
  }
}
