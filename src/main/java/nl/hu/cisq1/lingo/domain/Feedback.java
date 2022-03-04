package nl.hu.cisq1.lingo.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.hu.cisq1.lingo.domain.exception.InvalidFeedbackException;
import nl.hu.cisq1.lingo.domain.exception.InvalidPreviousHintException;

import java.util.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Feedback {
    private final String attempt;
    private final List<Mark> marks;

    public static Feedback create(String attempt, List<Mark> marks) {
        if (attempt.length() != marks.size()) {
            throw new InvalidFeedbackException();
        }

        return new Feedback(attempt, marks);
    }

    public static Feedback correct(String attempt) {
        return new Feedback(attempt, Collections.nCopies(attempt.length(), Mark.CORRECT));
    }

    public static Feedback incorrect(String attempt) {
        return new Feedback(attempt, Collections.nCopies(attempt.length(), Mark.ABSENT));
    }

    public static Feedback invalid(String attempt) {
        return new Feedback(attempt, Collections.nCopies(attempt.length(), Mark.INVALID));
    }

    public List<Character> giveHint(List<Character> previousHint, String wordToGuess) {
        if (!previousHint.isEmpty() && previousHint.size() != wordToGuess.length()) {
            throw new InvalidPreviousHintException();
        }

        List<Character> newHint = new ArrayList<>();

        if (previousHint.isEmpty()) {
            newHint.add(wordToGuess.charAt(0));
        }

        for (int i = 1; i < attempt.length(); i++) {
            char letter = attempt.charAt(i);
            if (marks.get(i) == Mark.CORRECT) {
                newHint.add(letter);
            } else if (marks.get(i) == Mark.PRESENT || marks.get(i) == Mark.ABSENT) {
                if (previousHint.isEmpty() || previousHint.get(i) == Character.MIN_VALUE) {
                    newHint.add(Character.MIN_VALUE);
                } else {
                    newHint.add(previousHint.get(i));
                }
            }
        }

        return newHint;
    }

    public boolean isWordGuessed() {
        return marks.stream().allMatch(mark -> mark.equals(Mark.CORRECT));
    }

    public boolean isGuessValid() {
        return marks.stream().noneMatch(mark -> mark.equals(Mark.INVALID));
    }
}
