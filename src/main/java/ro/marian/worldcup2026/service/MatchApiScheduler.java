package ro.marian.worldcup2026.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MatchApiScheduler {

    private static final Long WORLD_CUP_2026_TOURNAMENT_ID = 1L;
    private static final Long UCL_2026_2027_TOURNAMENT_ID = 4L;

    private static final String WORLD_CUP_COMPETITION_CODE = "WC";
    private static final String UCL_COMPETITION_CODE = "CL";

    private static final Integer WORLD_CUP_SEASON = 2026;
    private static final Integer UCL_SEASON = 2026;

    private static final DateTimeFormatter LOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MatchApiSyncService matchApiSyncService;

    public MatchApiScheduler(MatchApiSyncService matchApiSyncService) {
        this.matchApiSyncService = matchApiSyncService;
    }

    @Scheduled(fixedRate = 30_000)
    public void syncFootballData() {

        syncTournament(
                "WORLD CUP 2026",
                WORLD_CUP_2026_TOURNAMENT_ID,
                WORLD_CUP_COMPETITION_CODE,
                WORLD_CUP_SEASON
        );

        syncTournament(
                "UCL 2026/27",
                UCL_2026_2027_TOURNAMENT_ID,
                UCL_COMPETITION_CODE,
                UCL_SEASON
        );
    }

    private void syncTournament(String tournamentName,
                                Long tournamentId,
                                String competitionCode,
                                Integer season) {

        try {

            int updated = matchApiSyncService.syncFootballDataMatches(
                    tournamentId,
                    competitionCode,
                    season
            );

            System.out.println(
                    "["
                    + LocalDateTime.now().format(LOG_DATE_FORMAT)
                    + "] [FOOTBALL-DATA SCHEDULER] "
                    + tournamentName
                    + " updated="
                    + updated
            );

        } catch (Exception e) {

            System.out.println(
                    "["
                    + LocalDateTime.now().format(LOG_DATE_FORMAT)
                    + "] [FOOTBALL-DATA SCHEDULER] "
                    + tournamentName
                    + " ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}