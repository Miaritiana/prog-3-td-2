package integration;

import app.foot.FootApi;
import app.foot.controller.rest.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
class MatchIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();  //Allow 'java.time.Instant' mapping

    @Test
    void read_match_by_id_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch2(), actual);
    }
    @Test
    void read_match_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/matches"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Match> actual = objectMapper.readValue(
                response.getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class,Match.class)
        );

        assertEquals(3,actual.size());
        assertEquals(actual,List.of(
                expectedMatch1(),
                expectedMatch2(),
                expectedMatch3()
        ));
    }

    @Test
    void read_match_ko() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/matches"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Match> actual = objectMapper.readValue(
                response.getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class,Match.class)
        );

        assertNotEquals(0, actual.size());
    }

    private static Match expectedMatch1() {
        return Match.builder()
                .id(1)
                .teamA(teamMatchD())
                .teamB(teamMatchE())
                .stadium("S1")
                .datetime(Instant.parse("2023-01-01T10:00:00Z"))
                .build();
    }

    private static Match expectedMatch2() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchB())
                .stadium("S2")
                .datetime(Instant.parse("2023-01-01T14:00:00Z"))
                .build();
    }

    private static Match expectedMatch3() {
        return Match.builder()
                .id(3)
                .teamA(teamMatchC())
                .teamB(teamMatchB())
                .stadium("S3")
                .datetime(Instant.parse("2023-01-01T18:00:00Z"))
                .build();
    }



    private static TeamMatch teamMatchB() {
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchA() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(70)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(80)
                                .isOG(true)
                                .build()))
                .build();
    }

    private static TeamMatch teamMatchC() {
        return TeamMatch.builder()
                .team(team1())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchD() {
        return TeamMatch.builder()
                .team(team1())
                .score(4)
                .scorers(List.of(
                        PlayerScorer.builder()
                                .player(player4())
                                .scoreTime(30)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player4())
                                .scoreTime(20)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player4())
                                .scoreTime(10)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player1())
                                .scoreTime(60)
                                .isOG(true)
                                .build()
                ))
                .build();
    }

    private static TeamMatch teamMatchE() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(
                        PlayerScorer.builder()
                                .player(player2())
                                .scoreTime(40)
                                .isOG(true)
                                .build(),
                        PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(50)
                                .isOG(false)
                                .build()
                ))
                .build();
    }
    private static Team team3() {
        return Team.builder()
                .id(3)
                .name("E3")
                .build();
    }

    private static Player player6() {
        return Player.builder()
                .id(6)
                .name("J6")
                .isGuardian(false)
                .teamName(team3().getName())
                .build();
    }

    private static Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .teamName(team2().getName())
                .build();
    }

    private static Player player1() {
        return Player.builder()
                .id(1)
                .name("Joe Doe")
                .isGuardian(false)
                .teamName(team1().getName())
                .build();
    }

    private static Player player4() {
        return Player.builder()
                .id(4)
                .name("J4")
                .isGuardian(false)
                .teamName(team2().getName())
                .build();
    }

    private static Player player2() {
        return Player.builder()
                .id(2)
                .name("J2")
                .isGuardian(false)
                .teamName(team1().getName())
                .build();
    }

    private static Team team2() {
        return Team.builder()
                .id(2)
                .name("E2")
                .build();
    }

    private static Team team1() {
        return Team.builder()
                .id(1)
                .name("E1")
                .build();
    }
}
