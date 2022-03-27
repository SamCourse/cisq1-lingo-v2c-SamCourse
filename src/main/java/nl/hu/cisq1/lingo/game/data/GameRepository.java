package nl.hu.cisq1.lingo.game.data;

import nl.hu.cisq1.lingo.game.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
