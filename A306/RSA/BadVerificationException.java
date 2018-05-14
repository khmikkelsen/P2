package RSA;

public class BadVerificationException extends Exception
{
    public BadVerificationException() {
        super();
    }

    public BadVerificationException(String message) {
        super(message);
    }
}
