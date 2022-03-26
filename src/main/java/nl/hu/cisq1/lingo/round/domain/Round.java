package nl.hu.cisq1.lingo.round.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.round.domain.exception.RoundAlreadyOverException;
import nl.hu.cisq1.lingo.words.domain.Word;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
public class Round {
    @Id
    @GeneratedValue
    private UUID id;
    private String answer;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Guess> guesses;
    @Getter
    private int tries;
    @Getter
    private int wordLength;
    @ElementCollection
    private List<Character> firstHint;

    public Round(String answer) {
        this.answer = answer;
        this.guesses = new ArrayList<>();
        this.wordLength = answer.length();
    }

    public void guess(String attempt) {
        if (this.hasEnded()) {
            throw new RoundAlreadyOverException();
        }

        Feedback feedback = Feedback.create(attempt, answer);
        guesses.add(new Guess(attempt, feedback));
        tries++;
    }

    public List<Character> getFirstHint() {
        if (firstHint == null || firstHint.isEmpty()) {
            firstHint = Feedback.initialFeedback(answer).calculateHint(answer);
        }

        return firstHint;
    }

    public boolean hasEnded() {
        return hasBeenWon() || tries == 5;
    }

    public boolean hasBeenWon() {
        if (guesses.size() == 0) {
            return false;
        }

        Guess lastGuess = guesses.get(guesses.size() - 1);

        return lastGuess.getFeedback().isWordGuessed();
    }

    public boolean hasBeenLost() {
        if (guesses.size() == 0) {
            return false;
        }

        return !hasBeenWon() && tries == 5;
    }
}
