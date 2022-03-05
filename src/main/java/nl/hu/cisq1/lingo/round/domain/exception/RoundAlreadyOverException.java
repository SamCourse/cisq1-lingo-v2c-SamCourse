package nl.hu.cisq1.lingo.round.domain.exception;

public class RoundAlreadyOverException extends RuntimeException {
    public RoundAlreadyOverException() {
        super("This round has already ended.");
    }
}
