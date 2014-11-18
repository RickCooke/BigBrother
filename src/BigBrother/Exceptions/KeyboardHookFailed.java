package BigBrother.Exceptions;

@SuppressWarnings("serial")
public class KeyboardHookFailed extends Exception {
    public KeyboardHookFailed() {
        super();
    }

    public KeyboardHookFailed(String message) {
        super(message);
    }

    public KeyboardHookFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyboardHookFailed(Throwable cause) {
        super(cause);
    }
}
