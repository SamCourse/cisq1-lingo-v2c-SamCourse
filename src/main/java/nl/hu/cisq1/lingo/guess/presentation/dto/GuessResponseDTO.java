package nl.hu.cisq1.lingo.guess.presentation.dto;

import nl.hu.cisq1.lingo.feedback.presentation.dto.FeedbackResponseDTO;
import nl.hu.cisq1.lingo.guess.domain.Guess;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class GuessResponseDTO {
    @NotNull
    public UUID id;
    @NotNull
    public String attempt;
    @NotNull
    public FeedbackResponseDTO feedback;

    public GuessResponseDTO(Guess guess) {
        this.id = guess.getId();
        this.attempt = guess.getAttempt();
        this.feedback = new FeedbackResponseDTO(guess.getFeedback());
    }
}
