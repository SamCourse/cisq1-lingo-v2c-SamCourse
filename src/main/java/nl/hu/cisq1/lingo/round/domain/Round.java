package nl.hu.cisq1.lingo.round.domain;

import lombok.Getter;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.guess.domain.exception.NoGuessFoundException;
import nl.hu.cisq1.lingo.round.domain.exception.RoundAlreadyOverException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Round {
    @Id
    @GeneratedValue
    private UUID id;
    private String answer;
    @OneToMany(cascade = CascadeType.ALL)
    private final List<Guess> guesses;
    private int tries;
    private int wordLength;
    @ElementCollection
    private List<Character> firstHint;

    public Round() {
        this.guesses = new ArrayList<>();
        this.firstHint = new ArrayList<>();
    }

    public Round(String answer) {
        this();
        this.answer = answer;
        this.wordLength = answer.length();
    }

    public Guess guess(String attempt) {
        return this.guess(attempt, false);
    }

    public Guess guess(String attempt, boolean invalid) {
        if (this.hasEnded()) {
            throw new RoundAlreadyOverException();
        }

        Feedback feedback;

        if (invalid) {
            feedback = Feedback.invalid(attempt);
        } else {
            feedback = Feedback.create(attempt, answer);
        }

        feedback.calculateHint(answer);

        Guess guess = new Guess(attempt, feedback);
        guesses.add(guess);
        tries++;

        return guess;
    }

    public List<Character> getFirstHint() {
        firstHint = Feedback.initialFeedback(answer).calculateHint(answer);
        return firstHint;
    }

    public boolean hasEnded() {
        return hasBeenWon() || tries == 5;
    }

    public boolean hasBeenWon() {
        if (guesses.size() == 0) {
            return false;
        }

        Guess lastGuess = getLastGuess();

        return lastGuess.getFeedback().isWordGuessed();
    }

    public boolean hasBeenLost() {
        if (guesses.size() == 0) {
            return false;
        }

        return !hasBeenWon() && tries == 5;
    }

    public Guess getLastGuess() {
        if (guesses.size() == 0) {
            throw new NoGuessFoundException();
        }

        return guesses.get(guesses.size() - 1);
    }
}
