package nl.hu.cisq1.lingo.feedback.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.cisq1.lingo.feedback.domain.exception.InvalidFeedbackException;
import nl.hu.cisq1.lingo.feedback.domain.exception.InvalidPreviousHintException;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Entity
public class Feedback {
    @Id
    @GeneratedValue
    private UUID id;
    private String attempt;
    @Getter
    // TODO: Add attribute converter
    private List<Mark> marks;

    public Feedback(String attempt, List<Mark> marks) {
        this.attempt = attempt;
        this.marks = marks;
    }

    public static Feedback create(String guess, String answer) {
        return Feedback.create(guess, calculateMarks(guess, answer));
    }

    public static Feedback create(String attempt, List<Mark> marks) {
        if (attempt.length() != marks.size()) {
            throw new InvalidFeedbackException();
        }

        return new Feedback(attempt, marks);
    }

    public static Feedback correct(String attempt) {
        return Feedback.create(attempt, Collections.nCopies(attempt.length(), Mark.CORRECT));
    }

    public static Feedback incorrect(String attempt) {
        return Feedback.create(attempt, Collections.nCopies(attempt.length(), Mark.ABSENT));
    }

    public static Feedback invalid(String attempt) {
        return Feedback.create(attempt, Collections.nCopies(attempt.length(), Mark.INVALID));
    }

    public static Feedback initialFeedback(String answer) {
        return Feedback.create(answer.charAt(0) + "    ",
                List.of(Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT));
    }

    private static List<Mark> calculateMarks(String guess, String answer) {
        if (guess.length() != answer.length()) {
            throw new InvalidFeedbackException();
        }

        Map<Integer, Mark> marks = new HashMap<>();
        List<Integer> answerIndexesUsed = new ArrayList<>();    // Indexes of the answer that have been used,
        // e.g. when the letter at this index has been marked as present somewhere else.

        for (int i = 0; i < guess.length(); i++) {
            if (marks.containsKey(i)) {
                continue;
            }

            char character = guess.charAt(i);
            if (character == answer.charAt(i)) {
                marks.put(i, Mark.CORRECT);
                answerIndexesUsed.add(i);
            } else if (answer.indexOf(character) == -1) {
                marks.put(i, Mark.ABSENT);
            } else {
                int index = answer.indexOf(character);
                while (answerIndexesUsed.contains(index)) {
                    index = answer.indexOf(character, index + 1);
                }

                if (index == -1) {
                    marks.put(i, Mark.ABSENT);
                } else if (guess.charAt(index) == character) {
                    marks.put(index, Mark.CORRECT);
                    answerIndexesUsed.add(index);
                    i--;
                } else {
                    marks.put(i, Mark.PRESENT);
                    answerIndexesUsed.add(index);
                }
            }
        }

        List<Mark> marksList = new ArrayList<>();

        for (Integer key : new TreeSet<>(marks.keySet())) {
            marksList.add(marks.get(key));
        }

        return marksList;
    }

    public List<Character> giveHint(List<Character> previousHint, String wordToGuess) {
        if (!previousHint.isEmpty() && previousHint.size() != wordToGuess.length()) {
            throw new InvalidPreviousHintException();
        }

        if (marks.contains(Mark.INVALID)) {
            return previousHint;
        }

        List<Character> newHint = new ArrayList<>();

        newHint.add(wordToGuess.charAt(0));

        for (int i = 1; i < attempt.length(); i++) {
            char letter = attempt.charAt(i);
            if (marks.get(i) == Mark.CORRECT) {
                newHint.add(letter);
            } else if (previousHint.isEmpty() || previousHint.get(i) == Character.MIN_VALUE) {
                newHint.add(Character.MIN_VALUE);
            } else {
                newHint.add(previousHint.get(i));
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
