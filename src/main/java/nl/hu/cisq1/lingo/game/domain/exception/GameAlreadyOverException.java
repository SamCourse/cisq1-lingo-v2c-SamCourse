package nl.hu.cisq1.lingo.game.domain.exception;

public class GameAlreadyOverException extends RuntimeException {
    public GameAlreadyOverException() {
        super("This game has already ended.");
    }
}
