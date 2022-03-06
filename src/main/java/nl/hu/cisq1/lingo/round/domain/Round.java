package nl.hu.cisq1.lingo.round.domain;

import lombok.Getter;
import nl.hu.cisq1.lingo.round.domain.exception.RoundAlreadyOverException;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.guess.domain.Guess;
import nl.hu.cisq1.lingo.words.domain.Word;

import java.util.ArrayList;
import java.util.List;

public class Round {
    private final Word answer;
    private final List<Guess> guesses;
    @Getter
    private int tries;
    @Getter
    private final int wordLength;

    public Round(Word answer) {
        this.answer = answer;
        this.guesses = new ArrayList<>();
        this.wordLength = answer.getLength();
    }

    public void guess(Word attempt) {
        if (this.hasEnded()) {
            throw new RoundAlreadyOverException();
        }

        Feedback feedback = Feedback.create(attempt.getValue(), answer.getValue());
        guesses.add(new Guess(attempt, feedback));
        tries++;
    }

    public List<Character> getFirstHint() {
        return Feedback.initialFeedback(answer.getValue()).giveHint(new ArrayList<>(), answer.getValue());
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
