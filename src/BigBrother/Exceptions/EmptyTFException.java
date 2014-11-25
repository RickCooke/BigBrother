package BigBrother.Exceptions;

@SuppressWarnings("serial")
public class EmptyTFException extends Exception {

    public EmptyTFException() {
        super();
    }
    
    public EmptyTFException(String string) {
        super(string);
    }

}
