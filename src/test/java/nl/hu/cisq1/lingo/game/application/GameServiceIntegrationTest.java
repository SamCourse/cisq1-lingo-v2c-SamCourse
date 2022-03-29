package nl.hu.cisq1.lingo.game.application;

import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.game.application.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.game.data.GameRepository;
import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.game.domain.exception.GameAlreadyOverException;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.words.application.WordService;
import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GameServiceIntegrationTest {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;

    private static final String RANDOM_WORD_5 = "kaars";
    private static final String RANDOM_WORD_6 = "staart";
    private static final String RANDOM_WORD_7 = "bevries";

    @Test
    @DisplayName("ensure new games start with one round")
    void newGameHasOneRound() {
        Game game = gameService.startNewGame();
        assertEquals(1, game.getRounds().size());
    }

    @Test
    @DisplayName("ensure new games start with 5-letter word rounds")
    void newGameStartsNewRoundWith5LetterWord() {
        Game game = gameService.startNewGame();
        Round round = game.getLastRound();

        assertEquals(5, round.getWordLength());
    }

    @Test
    @DisplayName("performing guess with non-existent word is invalid guess")
    void guessWithInvalidWordIsInvalid() {
        Game game = gameService.startNewGame();
        Guess guess = gameService.guess(game.getId(), "broter");
        Feedback feedback = guess.getFeedback();

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("guessing a word that is too long will be invalid unless the cropped word exists")
    void guessWithTooLongWordIsInvalidUnlessExists() {
        Game game = gameService.startNewGame();
        Guess guess = gameService.guess(game.getId(), "too_long_word");

        Feedback feedback = guess.getFeedback();
        assertFalse(feedback.isGuessValid());

        Guess secondGuess = gameService.guess(game.getId(), "kaarsje");

        Feedback secondFeedback = secondGuess.getFeedback();
        assertTrue(secondFeedback.isGuessValid());
    }

    @Test
    @DisplayName("guess is invalid if word is too short")
    void guessWithTooShortWordIsInvalid() {
        Game game = gameService.startNewGame();
        gameService.guess(game.getId(), "kaars"); // Complete first round
        Guess guess = gameService.guess(game.getId(), "kaars"); // Guess too short word on 6-letter word round

        Feedback feedback = guess.getFeedback();

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("making a correct guess starts a new round, with correct length")
    void correctGuessStartsNewRound() {
        Game game = gameService.startNewGame();
        gameService.guess(game.getId(), "kaars");

        game = gameService.getGame(game.getId());

        Round round = game.getLastRound();
        assertEquals(6, round.getWordLength());
    }

    @Test
    @DisplayName("making a guess on a game that has ended throws an exception")
    void guessOnEndedGameThrowsException() {
        Game game = gameService.startNewGame();
        for (int i = 0; i < 5; i++) {
            gameService.guess(game.getId(), "kaart");
        }

        assertThrows(GameAlreadyOverException.class, () -> gameService.guess(game.getId(), "kaars"));
    }

    @Test
    @DisplayName("performing guess on non existent game throws exception")
    void guessOnNonExistentGameThrowsGameNotFound() {
        UUID fakeId = UUID.randomUUID();

        assertThrows(GameNotFoundException.class, () -> gameService.guess(fakeId, "testy"));
    }

    @BeforeEach
    void loadTestData() {
        // Load test fixture into test database before each test case
        wordRepository.save(new Word(RANDOM_WORD_5));
        wordRepository.save(new Word(RANDOM_WORD_6));
        wordRepository.save(new Word(RANDOM_WORD_7));
    }

    @AfterEach
    void clearTestData() {
        // Remove test fixtures from test database after each test case
        wordRepository.deleteAll();
        gameRepository.deleteAll();
    }
}
