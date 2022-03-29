package nl.hu.cisq1.lingo.game.presentation;

import lombok.AllArgsConstructor;
import nl.hu.cisq1.lingo.game.application.GameService;
import nl.hu.cisq1.lingo.game.application.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.game.domain.exception.GameAlreadyOverException;
import nl.hu.cisq1.lingo.game.presentation.dto.GameResponseDTO;
import nl.hu.cisq1.lingo.game.presentation.exception.GameAlreadyOverHTTPException;
import nl.hu.cisq1.lingo.game.presentation.exception.GameNotFoundHTTPException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
@AllArgsConstructor
public class GameController {
    private GameService gameService;

    @GetMapping("/start")
    public GameResponseDTO startGame() {
        Game game = gameService.startNewGame();
        return new GameResponseDTO(game);
    }

    @GetMapping("/list")
    public List<GameResponseDTO> getGames() {
        List<Game> games = gameService.getAllGames();
        List<GameResponseDTO> gameDTOs = new ArrayList<>();
        games.forEach(game -> gameDTOs.add(new GameResponseDTO(game)));

        return gameDTOs;
    }

    @GetMapping("/progress")
    public GameResponseDTO getGameProgress(@RequestParam(name = "game") UUID gameId) {
        Game game;

        try {
            game = gameService.getGame(gameId);
        } catch (GameNotFoundException e) {
            throw new GameNotFoundHTTPException(gameId);
        }

        return new GameResponseDTO(game);
    }

    @GetMapping("/guess")
    public GameResponseDTO guess(@RequestParam(name = "game") UUID gameId,
                                 @RequestParam(name = "guess") String guess) {
        Game game;

        try {
            gameService.guess(gameId, guess);
            game = gameService.getGame(gameId);
        } catch (GameNotFoundException e) {
            throw new GameNotFoundHTTPException(gameId);
        } catch (GameAlreadyOverException e) {
            throw new GameAlreadyOverHTTPException();
        }

        return new GameResponseDTO(game);
    }
}
