package nl.hu.cisq1.lingo.domain.exception;

public class InvalidPreviousHintException extends RuntimeException {
    public InvalidPreviousHintException() {
        super("The previous hint length did not match the guess word's length.");
    }
}
