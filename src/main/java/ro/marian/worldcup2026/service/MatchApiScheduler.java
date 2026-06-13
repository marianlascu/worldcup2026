package ro.marian.worldcup2026.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchApiScheduler {

    private static final Long WORLD_CUP_2026_TOURNAMENT_ID = 1L;

    private final MatchApiSyncService matchApiSyncService;

    public MatchApiScheduler(MatchApiSyncService matchApiSyncService) {
        this.matchApiSyncService = matchApiSyncService;
    }

    @Scheduled(fixedRate = 30_000)
    public void syncFootballData() {
        try {
            int updated = matchApiSyncService.syncFootballDataMatches(
                    WORLD_CUP_2026_TOURNAMENT_ID
            );

        System.out.println(
                "["
                + java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + "] [FOOTBALL-DATA SCHEDULER] updated="
                + updated
        );

        } catch (Exception e) {
            System.out.println("[FOOTBALL-DATA SCHEDULER] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}