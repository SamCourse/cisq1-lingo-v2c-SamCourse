package nl.hu.cisq1.lingo.round.domain.exception;

public class RoundNotEndedException extends RuntimeException {
    public RoundNotEndedException(String msg) {
        super(msg);
    }
}
