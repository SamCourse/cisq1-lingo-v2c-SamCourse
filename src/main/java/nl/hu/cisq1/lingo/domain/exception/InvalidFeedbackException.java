package nl.hu.cisq1.lingo.domain.exception;

public class InvalidFeedbackException extends RuntimeException {
    public InvalidFeedbackException() {
        super("The feedback's guess word did not match the feedback's amount of marks.");
    }
}
