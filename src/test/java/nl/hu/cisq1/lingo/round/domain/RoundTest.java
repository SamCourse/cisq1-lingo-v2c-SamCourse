package nl.hu.cisq1.lingo.round.domain;

import nl.hu.cisq1.lingo.guess.domain.exception.NoGuessFoundException;
import nl.hu.cisq1.lingo.round.domain.exception.RoundAlreadyOverException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Round")
class RoundTest {
    String wordToGuess = "test";
    String wrongWord = "tset";

    @Test
    @DisplayName("round has not ended if the tries count is not yet 5 and if answer has not been guessed")
    void roundHasNotEnded() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasEnded());

        for (int i = 0; i < 4; i++ ) {
            round.guess(wrongWord);
        }

        assertFalse(round.hasEnded());
    }

    @Test
    @DisplayName("round has ended if the tries count is 5")
    void roundHasEndedLoss() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasEnded());

        for (int i = 0; i < 4; i++ ) {
            round.guess(wrongWord);
        }

        assertFalse(round.hasEnded());
        round.guess(wrongWord);

        assertTrue(round.hasEnded());
    }

    @Test
    @DisplayName("round has ended if answer has been guessed")
    void roundHasEndedWin() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasEnded());

        round.guess(wordToGuess);

        assertTrue(round.hasEnded());
    }

    @Test
    @DisplayName("round has been won if the word has been guessed")
    void roundHasBeenWon() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasBeenWon());

        round.guess(wordToGuess);

        assertTrue(round.hasBeenWon());
    }

    @Test
    @DisplayName("round has been lost if the word has not been guessed and tries count has reached 5")
    void roundHasBeenLost() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasBeenLost());

        for (int i = 0; i < 4; i++ ) {
            round.guess(wrongWord);
        }

        assertFalse(round.hasBeenLost());

        round.guess(wrongWord);

        assertTrue(round.hasBeenLost());
    }

    @Test
    @DisplayName("round has been won if the word has been guessed and tries count has reached 5")
    void roundHasBeenWonWith5Tries() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasBeenLost());

        for (int i = 0; i < 4; i++ ) {
            round.guess(wrongWord);
        }

        assertFalse(round.hasBeenLost());

        round.guess(wordToGuess);

        assertFalse(round.hasBeenLost());
    }

    @Test
    @DisplayName("another guess can not be made if the round is over")
    void canNoLongerGuess() {
        Round round = new Round(wordToGuess);

        assertDoesNotThrow(() ->
                round.guess(wrongWord));

        for (int i = 0; i < 3; i++ ) {
            round.guess(wrongWord);
        }

        assertDoesNotThrow(() ->
                round.guess(wrongWord));

        assertThrows(RoundAlreadyOverException.class,
                () -> round.guess(wrongWord));
    }

    @Test
    @DisplayName("round has no last guess if no guess has been made yet")
    void noLastGuessIfNewRound() {
        Round round = new Round(wordToGuess);
        assertThrows(NoGuessFoundException.class, round::getLastGuess);
    }

}