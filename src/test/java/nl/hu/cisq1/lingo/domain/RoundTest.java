package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.round.domain.exception.RoundAlreadyOverException;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Round")
class RoundTest {
    Word wordToGuess = new Word("test");
    Word wrongWord = new Word("tset");

    @Test
    @DisplayName("round has not ended if the tries count is not yet 5 and if answer has not been guessed")
    void roundHasNotEnded() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasEnded());

        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);

        assertFalse(round.hasEnded());
    }

    @Test
    @DisplayName("round has ended if the tries count is 5")
    void roundHasEndedLoss() {
        Round round = new Round(wordToGuess);
        assertFalse(round.hasEnded());

        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);
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

        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);
        assertFalse(round.hasBeenLost());

        round.guess(wrongWord);

        assertTrue(round.hasBeenLost());
    }

    @Test
    @DisplayName("another guess can not be made if the round is over")
    void canNoLongerGuess() {
        Round round = new Round(wordToGuess);

        assertDoesNotThrow(() ->
                round.guess(wrongWord));

        round.guess(wrongWord);
        round.guess(wrongWord);
        round.guess(wrongWord);

        assertDoesNotThrow(() ->
                round.guess(wrongWord));

        assertThrows(RoundAlreadyOverException.class,
                () -> round.guess(wrongWord));
    }

}