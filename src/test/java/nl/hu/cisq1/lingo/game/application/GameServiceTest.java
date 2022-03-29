package nl.hu.cisq1.lingo.game.application;

import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.game.application.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.game.data.GameRepository;
import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.game.domain.exception.GameAlreadyOverException;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("ensure new games start with one round")
    void newGameHasOneRound() {
        assertEquals(1, game.getRounds().size());
    }

    @Test
    @DisplayName("ensure new games start with 5-letter word rounds")
    void newGameStartsNewRoundWith5LetterWord() {
        Round round = game.getLastRound();
        assertEquals(5, round.getWordLength());
    }

    @Test
    @DisplayName("performing guess with non-existent word is invalid guess")
    void guessWithInvalidWordIsInvalid() {
        Guess guess = gameService.guess(UUID.randomUUID(), "kaary");
        Feedback feedback = guess.getFeedback();

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("guessing a word that is too long will be invalid unless the cropped word exists")
    void guessWithTooLongWordIsInvalidUnlessExists() {
        Guess guess = gameService.guess(UUID.randomUUID(), "too_long_word");
        Feedback feedback = guess.getFeedback();

        assertFalse(feedback.isGuessValid());

        Guess secondGuess = gameService.guess(UUID.randomUUID(), "kaarsje");
        Feedback secondFeedback = secondGuess.getFeedback();

        assertTrue(secondFeedback.isGuessValid());
    }

    @Test
    @DisplayName("guess is invalid if word is too short")
    void guessWithTooShortWordIsInvalid() {
        gameService.guess(UUID.randomUUID(), "kaars"); // Complete first round
        Guess guess = gameService.guess(UUID.randomUUID(), "kaars"); // Guess too short word on 6-letter word round

        Feedback feedback = guess.getFeedback();

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("making a correct guess starts a new round, with correct length")
    void correctGuessStartsNewRound() {
        gameService.guess(UUID.randomUUID(), "kaars");

        Round round = game.getLastRound();
        assertEquals(6, round.getWordLength());
    }

    @Test
    @DisplayName("making a guess on a game that has ended throws an exception")
    void guessOnEndedGameThrowsException() {
        for (int i = 0; i < 5; i++) {
            gameService.guess(UUID.randomUUID(), "kaart");
        }

        assertThrows(GameAlreadyOverException.class, () -> gameService.guess(UUID.randomUUID(), "kaars"));
    }

    @Test
    @DisplayName("performing guess on non existent game throws exception")
    void guessOnNonExistentGameThrowsGameNotFound() {
        UUID fakeId = UUID.randomUUID();

        Mockito.reset(gameRepository);
        assertThrows(GameNotFoundException.class, () -> gameService.guess(fakeId, "testy"));
    }
}