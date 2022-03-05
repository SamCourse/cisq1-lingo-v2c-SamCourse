package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.exception.InvalidFeedbackException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static nl.hu.cisq1.lingo.domain.Mark.*;
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
                () -> Feedback.create("woord", Arrays.asList(INVALID, CORRECT, CORRECT, CORRECT, CORRECT, CORRECT)));

    }

    static Stream<Arguments> provideHintExamples() {
        List<String> guessWords = Arrays.asList("words", "hello", "array", "marks", "error");

        String guessWord = guessWords.get(new Random().nextInt(guessWords.size()));

        return Stream.of(
                Arguments.of(guessWord,
                        Arrays.asList(CORRECT, PRESENT, ABSENT, CORRECT, CORRECT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, guessWord.charAt(3), guessWord.charAt(4))),

                Arguments.of(guessWord,
                        Arrays.asList(PRESENT, CORRECT, CORRECT, CORRECT, ABSENT),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), guessWord.charAt(2), guessWord.charAt(3), Character.MIN_VALUE)),

                Arguments.of(guessWord,
                        Arrays.asList(PRESENT, PRESENT, ABSENT, CORRECT, CORRECT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, guessWord.charAt(3), guessWord.charAt(4))),

                Arguments.of(guessWord,
                        Arrays.asList(CORRECT, CORRECT, ABSENT, CORRECT, PRESENT),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), Character.MIN_VALUE, guessWord.charAt(3), Character.MIN_VALUE)),

                Arguments.of(guessWord,
                        Arrays.asList(ABSENT, PRESENT, ABSENT, PRESENT, ABSENT),
                        Arrays.asList(guessWord.charAt(0), Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE, Character.MIN_VALUE))
        );
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned is the same length as the word to guess")
    void hintReturnedMatchesAnswerLength(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(word.length(), hint.size());
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
                assertEquals(word.charAt(i), character);
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
            if (marks.get(i) != CORRECT) {
                assertEquals(Character.MIN_VALUE, hint.get(i));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned matches expected hint")
    void hintReturnedIsCorrect(String word, List<Mark> marks, List<Character> expectedHint) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(expectedHint, hint);
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("first hint has first letter revealed")
    void hintReturnedHasFirstLetter(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.giveHint(new ArrayList<>(), word);

        assertEquals(word.charAt(0), hint.get(0));
    }
}