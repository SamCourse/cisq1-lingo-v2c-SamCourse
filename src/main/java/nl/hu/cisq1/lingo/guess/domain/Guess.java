package nl.hu.cisq1.lingo.guess.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.words.domain.Word;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Entity
public class Guess {
    @Id
    @GeneratedValue
    private UUID id;
    private String attempt;
    @OneToOne(cascade = CascadeType.ALL)
    @Getter
    private Feedback feedback;

    public Guess(String attempt, Feedback feedback) {
        this.attempt = attempt;
        this.feedback = feedback;
    }
}
