package nl.hu.cisq1.lingo.guess.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.words.domain.Word;

@AllArgsConstructor
public class Guess {
    private final Word attempt;
    @Getter
    private Feedback feedback;


}
