package nl.hu.cisq1.lingo.game.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Could not find game with id")
public class GameNotFoundHTTPException extends RuntimeException {
    public GameNotFoundHTTPException(UUID id) {
        super("Could not find a game with id " + id.toString());
    }
}
