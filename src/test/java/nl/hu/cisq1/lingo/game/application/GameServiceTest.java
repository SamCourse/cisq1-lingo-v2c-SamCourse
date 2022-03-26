package nl.hu.cisq1.lingo.game.application;

import nl.hu.cisq1.lingo.game.application.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.game.data.GameRepository;
import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.game.domain.exception.GameAlreadyOverException;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    WordService wordService;
    GameRepository gameRepository;
    GameService gameService;
    Game game;

    @BeforeEach
    void prepareForTest() {
        wordService = mock(WordService.class);
        gameRepository = mock(GameRepository.class);
        gameService = new GameService(wordService, gameRepository);

        when(wordService.provideRandomWord(5)).thenReturn("kaars");
        when(wordService.provideRandomWord(6)).thenReturn("kaarsj");
        when(wordService.provideRandomWord(7)).thenReturn("kaarsje");
        when(wordService.wordExists("kaars")).thenReturn(true);
        when(wordService.wordExists("kaarsj")).thenReturn(true);
        when(wordService.wordExists("kaarsje")).thenReturn(true);

        game = gameService.startNewGame();

        when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(game));
        when(gameRepository.getById(Mockito.any())).thenReturn(game);
    }

    @Test
    void newGameHasOneRound() {
        assertEquals(1, game.getRounds().size());
    }

    @Test
    void newGameStartsNewRoundWith5LetterWord() {
        Round round = game.getLastRound();
        assertEquals(5, round.getWordLength());
    }

    @Test
    void guessWithInvalidWordIsInvalid() {
        gameService.guess(UUID.randomUUID(), "kaary");
        Round round = game.getLastRound();
        Guess guess = round.getGuesses().get(0);
        assertFalse(guess.getFeedback().isGuessValid());
    }

    @Test
    void guessWithTooLongWordIsInvalidUnlessExists() {
        gameService.guess(UUID.randomUUID(), "too_long_word");
        Round round = game.getLastRound();
        Guess guess = round.getGuesses().get(0);
        assertFalse(guess.getFeedback().isGuessValid());

        gameService.guess(UUID.randomUUID(), "kaarsje");
        Guess secondGuess = round.getGuesses().get(1);
        assertTrue(secondGuess.getFeedback().isGuessValid());
    }



    @Test
    void guessWithTooShortWordIsInvalid() {
        gameService.guess(UUID.randomUUID(), "kaars"); // Complete first round
        gameService.guess(UUID.randomUUID(), "kaars"); // Guess too short word on 6-letter word round

        Round round = game.getLastRound();
        Guess guess = round.getGuesses().get(0);
        assertFalse(guess.getFeedback().isGuessValid());
    }

    @Test
    void correctGuessStartsNewRound() {
        gameService.guess(UUID.randomUUID(), "kaars");

        Round round = game.getLastRound();
        assertEquals(6, round.getWordLength());
    }

    @Test
    void guessOnEndedGameThrowsException() {
        gameService.guess(UUID.randomUUID(), "kaart");
        gameService.guess(UUID.randomUUID(), "kaart");
        gameService.guess(UUID.randomUUID(), "kaart");
        gameService.guess(UUID.randomUUID(), "kaart");
        gameService.guess(UUID.randomUUID(), "kaart");

        assertThrows(GameAlreadyOverException.class, () -> gameService.guess(UUID.randomUUID(), "kaars"));
    }

    @Test
    void guessOnNonExistentGameThrowsGameNotFound() {
        UUID fakeId = UUID.randomUUID();

        Mockito.reset(gameRepository);
        assertThrows(GameNotFoundException.class, () -> gameService.guess(fakeId, "testy"));
    }
}