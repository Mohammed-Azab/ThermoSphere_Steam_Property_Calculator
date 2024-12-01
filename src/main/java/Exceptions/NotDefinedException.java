package Exceptions;

public class NotDefinedException extends IllegalArgumentException{
    public NotDefinedException(String message) {
        super(message);
    }
    public NotDefinedException() {
        super("These Quantities are not defined due to lack of data");
    }
    public NotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }
    public NotDefinedException(Throwable cause) {
        super(cause);
    }

}
