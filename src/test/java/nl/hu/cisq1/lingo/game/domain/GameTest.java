package nl.hu.cisq1.lingo.game.domain;

import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.round.domain.exception.NoRoundFoundException;
import nl.hu.cisq1.lingo.round.domain.exception.RoundNotEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game game;
    String answer = "tests";
    String wrongAnswer = "hello";

    @BeforeEach
    void resetGame() {
        game = new Game();
    }

    @Test
    @DisplayName("last round throws exception if no round has been initiated.")
    void lastRoundBeforeInitThrows() {
        assertThrows(NoRoundFoundException.class,
                game::getLastRound);
    }

    @Test
    @DisplayName("last round does not throw exception if round has been initiated")
    void lastRoundAfterInitDoesNotThrow() {
        game.initializeRound("tests");
        assertDoesNotThrow(game::getLastRound);
    }

    static Stream<Arguments> calculatePointsResults() {
        return Stream.of(Arguments.of(0, 30),
                    Arguments.of(1, 25),
                    Arguments.of(2, 20),
                    Arguments.of(3, 15),
                    Arguments.of(4, 10));
        }

    @ParameterizedTest
    @MethodSource("calculatePointsResults")
    @DisplayName("calculate points uses correct formula")
    void calculatePointsResultTest(int attempts, int expectedResult) {
        int points = game.calculatePoints(attempts);
        assertEquals(expectedResult, points);
    }

    @Test
    @DisplayName("initializing round returns hint with first letter displayed")
    void firstLetterHintOnRoundInitialization() {
        Round round = game.initializeRound(answer);
        List<Character> hint = round.getFirstHint();

        assertEquals(answer.charAt(0), hint.get(0));
    }

    @Test
    @DisplayName("initializing round adds round to the game")
    void roundInitAddsRoundToGame() {
        Round round = game.initializeRound(answer);
        assertEquals(round, game.getLastRound());
    }

    @Test
    @DisplayName("game is over if the round has been lost")
    void gameIsOverTest() {
        assertFalse(game.isOver());
        Round round = game.initializeRound(answer);

        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);

        game.completeRound();
        assertTrue(game.isOver());
    }

    @Test
    @DisplayName("can not complete round if round has not ended")
    void roundCanOnlyCompleteIfDone() {
        game.initializeRound(answer);
        assertThrows(RoundNotEndedException.class,
                game::completeRound);
    }

    @Test
    @DisplayName("winning game gives points as reward")
    void gameWinGivesPoints() {
        Round round = game.initializeRound(answer);
        int points = game.getPoints();
        round.guess(answer);
        game.completeRound();
        assertTrue(game.getPoints() > points);
    }

    @Test
    @DisplayName("new round started will be started with 5-letter round")
    void gameStartsNewRoundWith5() {
        assertEquals(5, game.getNextRoundWordLength());
    }

    @Test
    @DisplayName("5-letter round finished will be followed by 6-letter round")
    void gameFollows5letterRoundWith6() {
        game.initializeRound(answer);
        assertEquals(6, game.getNextRoundWordLength());
    }

    @Test
    @DisplayName("6-letter round finished will be followed by 7-letter round")
    void gameFollows6letterRoundWith7() {
        game.initializeRound("tester");
        assertEquals(7, game.getNextRoundWordLength());
    }

    @Test
    @DisplayName("7-letter round finished will be followed by 5-letter round")
    void gameFollows7letterRoundWith5() {
        game.initializeRound("testing");
        assertEquals(5, game.getNextRoundWordLength());
    }

    @Test
    @DisplayName("losing game does not give points as rewards")
    void gameLossDoesNotGivePoints() {
        Round round = game.initializeRound(answer);
        int points = game.getPoints();

        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);
        round.guess(wrongAnswer);

        game.completeRound();
        assertEquals(points, game.getPoints());
    }

    @Test
    @DisplayName("can not start new round if round has not ended")
    void ensureOnlyOneRoundAtATime() {
        game.initializeRound(answer);
        assertThrows(RoundNotEndedException.class,
                () -> game.initializeRound(answer));
    }
}