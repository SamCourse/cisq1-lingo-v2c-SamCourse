package nl.hu.cisq1.lingo.game.domain;

import lombok.Getter;
import nl.hu.cisq1.lingo.round.domain.Round;
import nl.hu.cisq1.lingo.round.domain.exception.NoRoundFoundException;
import nl.hu.cisq1.lingo.round.domain.exception.RoundNotEndedException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Game {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Round> rounds;
    private int points;
    private boolean isOver;

    public Game() {
        this.rounds = new ArrayList<>();
        this.isOver = false;
    }

    public Round initializeRound(String word) {
        if (!rounds.isEmpty() && !getLastRound().hasEnded()) {
            throw new RoundNotEndedException("Can not start new round; last round has not ended yet.");
        }

        Round round = new Round(word);
        rounds.add(round);

        return round;
    }

    public void completeRound() {
        Round round = getLastRound();

        if (!round.hasEnded()) {
            throw new RoundNotEndedException("Can not complete this round as it has not ended yet.");
        }

        if (round.hasBeenWon()) {
            this.points += calculatePoints(round.getTries());
        } else {
            this.isOver = true;
        }
    }

    public Round getLastRound() {
        if (rounds.size() == 0) {
            throw new NoRoundFoundException();
        }

        return rounds.get(rounds.size() - 1);
    }

    public int getNextRoundWordLength() {
        Round lastRound;
        try {
            lastRound = getLastRound();
        } catch (NoRoundFoundException e) {
            return 5;
        }

        switch (lastRound.getWordLength()) {
            default:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
        }
    }

    protected int calculatePoints(int attempts) {
        return 5 * (5 - attempts) + 5;
    }
}
