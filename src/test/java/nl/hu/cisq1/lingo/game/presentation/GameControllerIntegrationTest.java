package nl.hu.cisq1.lingo.game.presentation;

import com.jayway.jsonpath.JsonPath;
import nl.hu.cisq1.lingo.game.data.GameRepository;
import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private WordRepository wordRepository;

    private static final String WORD_5 = "words";
    private static final String WORD_6 = "before";
    private static final String WORD_7 = "private";

    @BeforeEach
    void loadTestWords() {
        // Fill database with test fixtures
        wordRepository.save(new Word(WORD_5));
        wordRepository.save(new Word(WORD_6));
        wordRepository.save(new Word(WORD_7));
    }

    @AfterEach
    void clearData() {
        // Clear database of test fixtures for both Word and Game table
        wordRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    @DisplayName("startGame returns fresh game with initialized round")
    void newGameRequest() throws Exception {
        RequestBuilder request = get("/game/start");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(1)));
    }

    @Test
    @DisplayName("can have multiple games")
    void allowMultipleGames() throws Exception {
        RequestBuilder request = get("/game/start");

        // Make 2 new games without finishing any to ensure it doesn't throw any exceptions
        mockMvc.perform(request)
                .andExpect(status().isOk());
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("list all games returns all games with correct progress")
    void getAllGamesTest() throws Exception {
        RequestBuilder request = get("/game/start");

        // Create 2 games
        String game1 = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String game2 = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        // Get a list of games
        RequestBuilder allGamesRequest = get("/game/list");

        // Ensure list of games contains 2 created games
        mockMvc.perform(allGamesRequest)
                .andExpect(content().string(containsString(game1)))
                .andExpect(content().string(containsString(game2)));
    }

    @Test
    @DisplayName("guess updates game with correct data")
    void guessGameTest() throws Exception {
        RequestBuilder request = get("/game/start");

        MvcResult result = mockMvc.perform(request).andReturn();

        // Get game id from result of new game call
        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        RequestBuilder guessRequest = get("/game/guess?game=" + id + "&guess=hello");

        // Make wrong guess
        mockMvc.perform(guessRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(1)));

        RequestBuilder correctGuessRequest = get("/game/guess?game=" + id + "&guess=" + WORD_5);

        // Ensure guess was correct and handled right
        mockMvc.perform(correctGuessRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(2)))
                .andExpect(jsonPath("$.points", greaterThan(0)));


        // Make 5 wrong requests to force end of game
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(guessRequest).andExpect(status().isOk());
        }

        // Ensure exception is thrown for attempting to guess on ended game
        mockMvc.perform(correctGuessRequest).andExpect(status().isForbidden());

        RequestBuilder wrongGameRequest = get("/game/guess?game=" + UUID.randomUUID() + "&guess=test");

        mockMvc.perform(wrongGameRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("progress check returns progress for right game with correct information")
    void progressGameTest() throws Exception {
        RequestBuilder request = get("/game/start");

        MvcResult result = mockMvc.perform(request).andReturn();

        // Get game id from result of new game call
        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        RequestBuilder gameProgressRequest = get("/game/progress?game=" + id);

        // Make call and ensure game is fresh
        mockMvc.perform(gameProgressRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(1)));

        // Make call, disregard response
        mockMvc.perform(get("/game/guess?game=" + id + "&guess=" + WORD_5));

        // Request progress for game, make sure guess was correct
        mockMvc.perform(gameProgressRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(2)))
                .andExpect(jsonPath("$.points", greaterThan(0)));

        // Make another request for the progress of the game, ensure the progress is still the same after the call
        mockMvc.perform(gameProgressRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds", hasSize(2)))
                .andExpect(jsonPath("$.points", greaterThan(0)));

        // Make progress request with non-existing game UUID, expect 404
        RequestBuilder wrongGameRequestProgress = get("/game/progress?game=" + UUID.randomUUID());
        mockMvc.perform(wrongGameRequestProgress)
                .andExpect(status().isNotFound());
    }
}