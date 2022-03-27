package nl.hu.cisq1.lingo.feedback.presentation.dto;

import nl.hu.cisq1.lingo.feedback.domain.Feedback;
import nl.hu.cisq1.lingo.feedback.domain.Mark;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class FeedbackResponseDTO {
    @NotNull
    public UUID id;
    @NotNull
    public String attempt;
    @NotNull
    public List<Mark> marks;
    @NotNull
    public List<Character> hint;

    public FeedbackResponseDTO(Feedback feedback) {
        this.id = feedback.getId();
        this.attempt = feedback.getAttempt();
        this.marks = feedback.getMarks();
        this.hint = feedback.getLastHint();
    }
}
