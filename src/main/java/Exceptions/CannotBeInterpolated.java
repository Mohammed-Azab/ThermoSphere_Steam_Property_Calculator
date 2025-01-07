package Exceptions;

public class CannotBeInterpolated extends RuntimeException {
    public CannotBeInterpolated(String message) {
        super(message);
    }

    public CannotBeInterpolated(String message, Throwable cause) {
        super(message, cause);
    }
    public CannotBeInterpolated(Throwable cause) {
        super(cause);
    }

    public CannotBeInterpolated() {
        super("Can't be interpolated");
    }
}
