package nl.hu.cisq1.lingo.game.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="Game already ended")
public class GameAlreadyOverHTTPException extends RuntimeException {
    public GameAlreadyOverHTTPException() {
        super("This game has already ended.");
    }
}

