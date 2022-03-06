package nl.hu.cisq1.lingo.round.domain.exception;

public class NoRoundFoundException extends RuntimeException {
    public NoRoundFoundException() {
        super("No round has been started yet in this game.");
    }
}
