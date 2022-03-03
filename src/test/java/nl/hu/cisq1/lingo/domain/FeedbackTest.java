package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.exception.InvalidFeedbackException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {

    @Test
    @DisplayName("word is guessed if all letters are correct")
    void isWordGuessed() {
        Feedback feedback = Feedback.correct("woord");
        assertTrue(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("word is not guessed if not all letters are correct")
    void wordIsNotGuessed() {
        Feedback feedback = Feedback.incorrect("woord");
        assertFalse(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("guess is valid if none of the marks are invalid")
    void guessIsValid() {
        Feedback feedback = Feedback.correct("woord");
        assertTrue(feedback.isGuessValid());
    }

    @Test
    @DisplayName("guess is invalid if any of the marks are invalid")
    void guessIsInvalid() {
        Feedback feedback = Feedback.invalid("woord");
        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("feedback throws exception if guess word length does not match feedback mark amount")
    void feedbackThrowsExceptionForMismatchedLengths() {
        assertThrows(InvalidFeedbackException.class,
                () -> Feedback.create("woord", Arrays.asList(Mark.INVALID, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT)));

    }
}