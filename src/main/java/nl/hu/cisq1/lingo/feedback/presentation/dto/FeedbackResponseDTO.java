package nl.hu.cisq1.lingo.feedback.presentation.dto;

import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.feedback.domain.Mark;

import javax.validation.constraints.NotNull;
import java.util.List;

public class FeedbackResponseDTO {
    @NotNull
    public String attempt;
    @NotNull
    public List<Mark> marks;
    @NotNull
    public List<Character> hint;

    public FeedbackResponseDTO(Feedback feedback) {
        this.attempt = feedback.getAttempt();
        this.marks = feedback.getMarks();
        this.hint = feedback.getLastHint();
    }
}
