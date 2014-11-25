package BigBrother.Exceptions;

@SuppressWarnings("serial")
public class DuplicateKeyException extends Exception {

    public DuplicateKeyException() {
        super();
    }

    public DuplicateKeyException(String string) {
        super(string);
    }

}
