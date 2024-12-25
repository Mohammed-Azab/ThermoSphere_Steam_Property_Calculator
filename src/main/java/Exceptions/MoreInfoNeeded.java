package Exceptions;

public class MoreInfoNeeded extends RuntimeException {
    public MoreInfoNeeded(String message) {
        super(message);
    }
    public MoreInfoNeeded() {
        super("These Quantities are not Enough, More Info Needed");
    }
    public MoreInfoNeeded(String message, Throwable cause) {
        super(message, cause);
    }
    public MoreInfoNeeded(Throwable cause) {
        super(cause);
    }

}
