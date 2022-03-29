package nl.hu.cisq1.lingo.game.application;

import lombok.AllArgsConstructor;
import nl.hu.cisq1.lingo.game.application.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.game.data.GameRepository;
import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.game.domain.exception.GameAlreadyOverException;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class GameService {
    private WordService wordService;
    private GameRepository gameRepository;

    public Game startNewGame() {
        String word = wordService.provideRandomWord(5);
        Game game = new Game();
        game.initializeRound(word);
        gameRepository.save(game);
        return game;
    }

    public Guess guess(UUID gameId, String word) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.isOver()) {
            throw new GameAlreadyOverException();
        }

        Round round = game.getLastRound();

        int currentRoundWordLength = round.getWordLength();

        if (word.length() > currentRoundWordLength) {
            word = word.substring(0, currentRoundWordLength);
        }

        boolean invalid = !wordService.wordExists(word) || word.length() < currentRoundWordLength;

        Guess guess = round.guess(word, invalid);

        if (round.hasEnded()) {
            game.completeRound();

            startNewRound(gameId, game.getNextRoundWordLength());
        }

        return guess;
    }

    public Game startNewRound(UUID gameId, int wordLength) {
        Game game = gameRepository.getById(gameId);
        String word = wordService.provideRandomWord(wordLength);
        game.initializeRound(word);
        return game;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGame(UUID gameid) {
        return gameRepository.findById(gameid)
                .orElseThrow(() -> new GameNotFoundException(gameid));
    }

}
