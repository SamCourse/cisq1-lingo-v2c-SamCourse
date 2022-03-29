package nl.hu.cisq1.lingo.guess.domain.exception;

public class NoGuessFoundException extends RuntimeException {
    public NoGuessFoundException() {
        super("No guess has been made yet in this round.");
    }
}
