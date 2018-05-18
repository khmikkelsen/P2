package RSA;

public class InvalidRSAKeyException extends Exception {
    public InvalidRSAKeyException() {
        super();
    }

    public InvalidRSAKeyException(String message) {
        super(message);
    }
}
