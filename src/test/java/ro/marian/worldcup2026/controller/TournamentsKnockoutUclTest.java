package ro.marian.worldcup2026.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.marian.worldcup2026.dto.GroupStanding;
import ro.marian.worldcup2026.dto.GroupStandingRow;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;
import ro.marian.worldcup2026.service.KnockoutService;
import ro.marian.worldcup2026.service.MatchApiSyncService;
import ro.marian.worldcup2026.service.MatchScoreService;
import ro.marian.worldcup2026.service.StandingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(TournamentsController.class)
class TournamentsKnockoutUclTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TournamentRepository tournamentRepository;

    @MockitoBean
    private MatchGameRepository matchGameRepository;

    @MockitoBean
    private StandingService standingService;

    @MockitoBean
    private MatchScoreService matchScoreService;

    @MockitoBean
    private KnockoutService knockoutService;

    @MockitoBean
    private MatchApiSyncService matchApiSyncService;

    @Test
    void uclKnockoutRendersProvisionalBracketFromLeagueStandings() throws Exception {
        when(tournamentRepository.findById(4L)).thenReturn(Optional.of(tournament("UCL2627", "Champions League")));
        when(standingService.buildGroupStandings(4L)).thenReturn(List.of(leagueStandings()));

        mockMvc.perform(get("/tournaments/4/knockout").sessionAttr("USER_ID", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("tournaments/tournament-layout"))
                .andExpect(model().attribute("uclMode", true))
                .andExpect(content().string(containsString("PROVIZORIU")))
                .andExpect(content().string(containsString("Echipa 9")))
                .andExpect(content().string(containsString("Echipa 24")));

        verify(knockoutService, never()).buildKnockoutMatches(4L);
    }

    @Test
    void nonUclTournamentKeepsWorldCupKnockoutPath() throws Exception {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament("WC2026", "World Cup")));
        when(knockoutService.buildKnockoutMatches(1L)).thenReturn(List.of());

        mockMvc.perform(get("/tournaments/1/knockout").sessionAttr("USER_ID", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("uclMode"));

        verify(knockoutService).buildKnockoutMatches(1L);
    }

    private static Tournament tournament(String code, String name) {
        Tournament tournament = new Tournament();
        tournament.setCode(code);
        tournament.setName(name);
        return tournament;
    }

    private static GroupStanding leagueStandings() {
        List<GroupStandingRow> rows = new ArrayList<>();
        for (int position = 1; position <= 36; position++) {
            GroupStandingRow row = new GroupStandingRow("Echipa " + position);
            row.setPosition(position);
            rows.add(row);
        }
        return new GroupStanding("League", rows);
    }
}
