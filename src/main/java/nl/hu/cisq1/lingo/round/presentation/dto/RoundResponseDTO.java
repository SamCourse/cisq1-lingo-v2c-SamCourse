package nl.hu.cisq1.lingo.round.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import nl.hu.cisq1.lingo.guess.presentation.dto.GuessResponseDTO;
import nl.hu.cisq1.lingo.round.domain.Round;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class RoundResponseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String answer;
    @NotNull
    public List<GuessResponseDTO> guesses;
    @NotNull
    public int tries;
    @NotNull
    public int wordLength;
    @NotNull
    public List<Character> firstHint;

    public RoundResponseDTO(Round round, String answer) {
        if (!answer.isEmpty()) {
            this.answer = answer;
        }

        this.tries = round.getTries();
        this.wordLength = round.getWordLength();
        this.firstHint = round.getFirstHint();
        guesses = new ArrayList<>();

        round.getGuesses().forEach(guess -> guesses.add(new GuessResponseDTO(guess)));
    }
}
