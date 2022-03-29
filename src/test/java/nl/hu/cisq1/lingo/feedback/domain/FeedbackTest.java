package nl.hu.cisq1.lingo.feedback.domain;

import nl.hu.cisq1.lingo.feedback.domain.exception.InvalidFeedbackException;
import nl.hu.cisq1.lingo.feedback.domain.exception.InvalidPreviousHintException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static nl.hu.cisq1.lingo.feedback.domain.Mark.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Feedback")
class FeedbackTest {
    String wrongAnswer = "tests";

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

    @Test
    @DisplayName("feedback correct constructor returns word has been guessed")
    void feedbackCorrectConstructorTest() {
        assertTrue(Feedback.correct("test").isWordGuessed());
    }

    @Test
    @DisplayName("feedback correct constructor returns guess is valid")
    void feedbackCorrectConstructorValidityTest() {
        assertTrue(Feedback.correct("test").isGuessValid());
    }

    @Test
    @DisplayName("feedback incorrect constructor returns word has not been guessed")
    void feedbackIncorrectConstructorTest() {
        assertFalse(Feedback.incorrect("test").isWordGuessed());
    }

    @Test
    @DisplayName("feedback invalid constructor returns word is invalid")
    void feedbackInvalidConstructorTest() {
        assertFalse(Feedback.invalid("test").isGuessValid());
    }

    @Test
    @DisplayName("feedback constructor throws exception if mismatched lengths")
    void feedbackConstructorThrowsWhenLengthMismatch() {
        assertThrows(InvalidFeedbackException.class,
                () -> Feedback.create("word", "words"));
    }

    static Stream<Arguments> provideHintExamples() {
        List<String> guessWords = Arrays.asList("words", "hello", "array", "marks", "error");

        String guessWord = guessWords.get(new Random().nextInt(guessWords.size()));

        return Stream.of(
                Arguments.of(guessWord,
                        Arrays.asList(CORRECT, PRESENT, ABSENT, CORRECT, CORRECT),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), ' ', guessWord.charAt(3), guessWord.charAt(4)),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), ' ', guessWord.charAt(3), guessWord.charAt(4))),

                Arguments.of(guessWord,
                        Arrays.asList(PRESENT, CORRECT, CORRECT, CORRECT, ABSENT),
                        Arrays.asList(' ', ' ', ' ', ' ', guessWord.charAt(4)),
                        Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), guessWord.charAt(2), guessWord.charAt(3), guessWord.charAt(4)),

                        Arguments.of(guessWord,
                                Arrays.asList(PRESENT, PRESENT, ABSENT, CORRECT, CORRECT),
                                Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), guessWord.charAt(2), ' ', ' '),
                                Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), guessWord.charAt(2), guessWord.charAt(3), guessWord.charAt(4))),

                        Arguments.of(guessWord,
                                Arrays.asList(CORRECT, CORRECT, ABSENT, CORRECT, PRESENT),
                                Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), ' ', ' ', ' '),
                                Arrays.asList(guessWord.charAt(0), guessWord.charAt(1), ' ', guessWord.charAt(3), ' ')),

                        Arguments.of(guessWord,
                                Arrays.asList(ABSENT, PRESENT, ABSENT, PRESENT, ABSENT),
                                Arrays.asList(' ', ' ', ' ', guessWord.charAt(3), ' '),
                                Arrays.asList(guessWord.charAt(0), ' ', ' ', guessWord.charAt(3), ' '))));
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned is the same length as the word to guess")
    void hintReturnedMatchesAnswerLength(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.calculateHint(wrongAnswer);

        assertEquals(word.length(), hint.size());
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned matches word to guess")
    void hintReturnedMatchesAnswer(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.calculateHint(word);

        for (int i = 0; i < hint.size(); i++) {
            char character = hint.get(i);

            if (character != ' ') {
                assertEquals(word.charAt(i), character);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned does not hint too much")
    void hintReturnedDoesNotSpoil(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.calculateHint(word);

        for (int i = 1; i < marks.size(); i++) {
            if (marks.get(i) != CORRECT) {
                assertEquals(' ', hint.get(i));
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned takes previous hint in consideration")
    void hintReturnedUsesFallback(String word, List<Mark> marks, List<Character> lastHint) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> newHint = feedback.calculateHint(lastHint, word);

        for (int i = 0; i < lastHint.size(); i++) {
            if (lastHint.get(i) != ' ') {
                assertEquals(lastHint.get(i), newHint.get(i));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("hint returned matches expected hint")
    void hintReturnedIsCorrect(String word, List<Mark> marks, List<Character> lastHint, List<Character> expectedHint) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.calculateHint(lastHint, word);

        assertEquals(expectedHint, hint);
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("first hint has first letter revealed")
    void hintReturnedHasFirstLetter(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> hint = feedback.calculateHint(word);

        assertEquals(word.charAt(0), hint.get(0));
    }


    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("mismatched hint and word size exception in giveHint")
    void giveHintThrowsExceptionIfMismatchedLengths(String word, List<Mark> marks) {
        Feedback feedback = Feedback.create(word, marks);
        List<Character> improperLastHint = new ArrayList<>();

        for (int i = 0; i < word.length() - 1; i++) {
            improperLastHint.add(word.charAt(i));
        }

        assertThrows(InvalidPreviousHintException.class,
                () -> feedback.calculateHint(improperLastHint, word));
    }


    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("giveHint returns last hint if word is invalid")
    void giveHintProperlyHandlesInvalid(String word) {
        Feedback feedback = Feedback.create(wrongAnswer, List.of(INVALID, INVALID, INVALID, INVALID, INVALID));
        assertEquals(new ArrayList<>(), feedback.calculateHint(word));
    }


    static Stream<Arguments> provideMarkProcessingExamples() {
        String answer = "words";
        Map<String, List<Mark>> guesses = new HashMap<>() {{
            put("woods", Arrays.asList(CORRECT, CORRECT, ABSENT, CORRECT, CORRECT));
            put("whale", Arrays.asList(CORRECT, ABSENT, ABSENT, ABSENT, ABSENT));
            put("shred", Arrays.asList(PRESENT, ABSENT, CORRECT, ABSENT, PRESENT));
            put("strss", Arrays.asList(ABSENT, ABSENT, CORRECT, ABSENT, CORRECT));
            put("words", Arrays.asList(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT));
            put("bawes", Arrays.asList(ABSENT, ABSENT, PRESENT, ABSENT, CORRECT));
            put("ddodr", Arrays.asList(ABSENT, ABSENT, PRESENT, CORRECT, PRESENT));
            put("wdowr", Arrays.asList(CORRECT, PRESENT, PRESENT, ABSENT, PRESENT));
            put("sdrow", Arrays.asList(PRESENT, PRESENT, CORRECT, PRESENT, PRESENT));
            put("wwwww", Arrays.asList(CORRECT, ABSENT, ABSENT, ABSENT, ABSENT));
            put("wdwdr", Arrays.asList(CORRECT, ABSENT, ABSENT, CORRECT, PRESENT));
        }};

        String answer2 = "saws";
        Map<String, List<Mark>> guesses2 = new HashMap<>() {{
            put("woss", Arrays.asList(PRESENT, ABSENT, PRESENT, CORRECT));
            put("ssas", Arrays.asList(CORRECT, ABSENT, PRESENT, CORRECT));
            put("aaws", Arrays.asList(ABSENT, CORRECT, CORRECT, CORRECT));
        }};


        List<Arguments> arguments = new ArrayList<>();
        for (Map.Entry<String, List<Mark>> entries : guesses.entrySet()) {
            arguments.add(Arguments.of(entries.getKey(), answer, entries.getValue()));
        }
        for (Map.Entry<String, List<Mark>> entries2 : guesses2.entrySet()) {
            arguments.add(Arguments.of(entries2.getKey(), answer2, entries2.getValue()));
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("provideMarkProcessingExamples")
    @DisplayName("marks returned from mark processing are correct")
    void markCalculationTest(String guess, String answer, List<Mark> expectedMarks) {
        Feedback feedback = Feedback.create(guess, answer);
        List<Mark> marks = feedback.getMarks();
        assertEquals(expectedMarks, marks);
    }
}