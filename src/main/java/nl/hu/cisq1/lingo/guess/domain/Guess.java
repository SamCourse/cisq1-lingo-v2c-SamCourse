package nl.hu.cisq1.lingo.guess.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.words.domain.Word;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@NoArgsConstructor
@Entity
public class Guess {
    @Id
    private UUID id;
    @OneToOne
    private Word attempt;
    @Getter
    @OneToOne
    private Feedback feedback;

    public Guess(Word attempt, Feedback feedback) {
        this.attempt = attempt;
        this.feedback = feedback;
    }
}
