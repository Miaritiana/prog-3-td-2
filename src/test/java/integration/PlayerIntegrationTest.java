package integration;

import app.foot.FootApi;
import app.foot.controller.rest.Match;
import app.foot.controller.rest.Player;
import app.foot.controller.rest.PlayerScorer;
import app.foot.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.assertThrowsExceptionMessage;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
class PlayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    Player player1() {
        return Player.builder()
                .id(1)
                .name("J1")
                .isGuardian(false)
                .build();
    }

    Player player2() {
        return Player.builder()
                .id(2)
                .name("J2")
                .isGuardian(false)
                .build();
    }

    Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .build();
    }

    Player player5() {
        return Player.builder()
                .id(5)
                .name("J5")
                .isGuardian(false)
                .teamName("E3")
                .build();
    }



    @Test
    void read_players_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/players"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(9, actual.size());
        assertTrue(actual.containsAll(List.of(
                player1(),
                player2(),
                player3())));
    }

    @Test
    void create_players_ok() throws Exception {
        Player toCreate = Player.builder()
                .name("Joe Doe")
                .isGuardian(false)
                .teamName("E1")
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .content(objectMapper.writeValueAsString(List.of(toCreate)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(1, actual.size());
        assertEquals(toCreate, actual.get(0).toBuilder().id(null).build());
    }

    @Test
    void update_players_ok() throws Exception {
        Player toUpdate = Player.builder()
                .id(1)
                .name("Joe Doe")
                .isGuardian(false)
                .teamName("E1")
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .content(objectMapper.writeValueAsString(List.of(toUpdate)))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();

        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(toUpdate.getId(),actual.get(0).getId());
        assertEquals(toUpdate,actual.get(0));
    }

    @Test
    void add_goals_where_match_id_3_ok () throws Exception{

        PlayerScorer scorer = PlayerScorer.builder()
                .player(player5())
                .scoreTime(30)
                .isOG(false)
                .build();
        MockHttpServletResponse response = mockMvc
                .perform(post("/matches/3/goals")
                        .content(objectMapper.writeValueAsString(List.of(scorer)))
                        .contentType("application/json"))
                .andReturn()
                .getResponse();

        Match actual = objectMapper.readValue(response.getContentAsString(), Match.class);

        assertEquals(scorer,actual.getTeamB().getScorers().get(0));
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    void add_goals_where_match_id_3_ko () throws Exception{

        PlayerScorer scorer = PlayerScorer.builder()
                .player(player5())
                .scoreTime(92)
                .isOG(false)
                .build();
        assertThrowsExceptionMessage("Request processing failed: app.foot.exception.BadRequestException: 400 BAD_REQUEST : Player#J5 cannot score before after minute 90.",
                ServletException.class,
                () -> mockMvc.perform(post("/matches/3/goals")
                                .content(objectMapper.writeValueAsString(List.of(scorer)))
                                .contentType("application/json")
                        )
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                );
    }

    private List<Player> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }


}
