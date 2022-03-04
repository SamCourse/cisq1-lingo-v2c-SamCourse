package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.exception.InvalidFeedbackException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

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

    static Stream<Arguments> provideHintExamples() {
        List<String> guessWords = Arrays.asList("words", "hello", "array", "marks", "error");

        String guessWord = guessWords.get(new Random().nextInt(guessWords.size()));

        return Stream.of(
                Arguments.of(guessWord,
                        Arrays.asList(Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, guessWord.charAt(3), guessWord.charAt(4))),

                Arguments.of(guessWord,
                        Arrays.asList(Mark.PRESENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), guessWord.charAt(2), guessWord.charAt(3), Character.MIN_VALUE)),

                Arguments.of(guessWord,
                        Arrays.asList(Mark.PRESENT, Mark.PRESENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, guessWord.charAt(3), guessWord.charAt(4))),

                Arguments.of(guessWord,
                        Arrays.asList(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.CORRECT, Mark.PRESENT),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), Character.MIN_VALUE, guessWord.charAt(3), Character.MIN_VALUE)),

                Arguments.of(guessWord,
                        Arrays.asList(Mark.ABSENT, Mark.PRESENT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE))
        );
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned is the same length as the word to guess")
    void hintReturnedMatchesAnswerLength(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(hint.size(), word.length());
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned matches word to guess")
    void hintReturnedMatchesAnswer(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        for (int i = 0; i < hint.size(); i++) {
            char character = hint.get(i);

            if (character != Character.MIN_VALUE) {
                assertEquals(character, word.charAt(i));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned does not hint too much")
    void hintReturnedDoesNotSpoil(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        for (int i = 1; i < marks.size(); i++) {
            if (marks.get(i) != Mark.CORRECT) {
                assertEquals(hint.get(i), Character.MIN_VALUE);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned matches expected hint")
    void hintReturnedIsCorrect(String word, List<Mark> marks, List<Character> expectedHint) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(hint, expectedHint);
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("first hint has first letter revealed")
    void hintReturnedHasFirstLetter(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(hint.get(0), word.charAt(0));
    }
}