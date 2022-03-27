package nl.hu.cisq1.lingo.guess.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Getter
public class Guess {
    @Id
    @GeneratedValue
    private UUID id;
    private String attempt;
    @OneToOne(cascade = CascadeType.ALL)
    private Feedback feedback;

    public Guess(String attempt, Feedback feedback) {
        this.attempt = attempt;
        this.feedback = feedback;
    }
}
