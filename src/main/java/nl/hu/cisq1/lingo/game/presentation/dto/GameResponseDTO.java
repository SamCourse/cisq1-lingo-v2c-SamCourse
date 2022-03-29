package nl.hu.cisq1.lingo.game.presentation.dto;

import nl.hu.cisq1.lingo.game.domain.Game;
import nl.hu.cisq1.lingo.round.presentation.dto.RoundResponseDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GameResponseDTO {
    @NotNull
    public UUID id;
    @NotNull
    public List<RoundResponseDTO> rounds;
    @NotNull
    public int points;
    @NotNull
    public boolean isOver;

    public GameResponseDTO(Game game) {
        this.id = game.getId();
        this.points = game.getPoints();
        this.isOver = game.isOver();
        rounds = new ArrayList<>();

        game.getRounds().forEach(round -> rounds.add(new RoundResponseDTO(round, round.hasEnded() ? round.getAnswer(): "")));
    }
}
