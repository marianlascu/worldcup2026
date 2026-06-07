package ro.marian.worldcup2026;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ro.marian.worldcup2026.model.AppUser;
import ro.marian.worldcup2026.model.MatchGame;
import ro.marian.worldcup2026.model.Tournament;
import ro.marian.worldcup2026.repository.AppUserRepository;
import ro.marian.worldcup2026.repository.MatchGameRepository;
import ro.marian.worldcup2026.repository.TournamentRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final MatchGameRepository matchGameRepository;
    private final AppUserRepository appUserRepository;
    private final TournamentRepository tournamentRepository;

    public DataLoader(MatchGameRepository matchGameRepository,
                      AppUserRepository appUserRepository,
                      TournamentRepository tournamentRepository) {
        this.matchGameRepository = matchGameRepository;
        this.appUserRepository = appUserRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUsersIfMissing();
        loadTournamentsIfMissing();
        loadMatchesIfEmpty();
    }

    private void loadUsersIfMissing() {
        createUserIfMissing("admin", "admin", "Administrator", "ADMIN");
        createUserIfMissing("marian", "admin", "Marian", "PLAYER");
    }

    private void createUserIfMissing(String username, String password, String fullName, String role) {

        if (appUserRepository.findByUsername(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setRole(role);

        appUserRepository.save(user);
    }

    private void loadTournamentsIfMissing() {
        createTournamentIfMissing("WC2026", "World Cup 2026", "2026", "Y");
        createTournamentIfMissing("EURO2028", "Euro 2028", "2028", "Y");
        createTournamentIfMissing("EPL2627", "Premier League", "2026/2027", "Y");
    }

    private void createTournamentIfMissing(String code, String name, String season, String activeYn) {

        if (tournamentRepository.findByCode(code).isPresent()) {
            return;
        }

        Tournament tournament = new Tournament();
        tournament.setCode(code);
        tournament.setName(name);
        tournament.setSeason(season);
        tournament.setActiveYn(activeYn);

        tournamentRepository.save(tournament);
    }

    private void loadMatchesIfEmpty() throws Exception {

        if (matchGameRepository.count() > 0) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/worldcup2026-matches.csv");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {

                if (first) {
                    first = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] p = line.split(";", -1);

                MatchGame m = new MatchGame();

                m.setMatchNo(Integer.parseInt(p[0].trim()));
                m.setKickoffAt(LocalDateTime.of(
                        LocalDate.parse(p[1].trim()),
                        LocalTime.parse(p[2].trim())
                ));
                m.setStage(p[3].trim());
                m.setGroupName(p[4].trim().isEmpty() ? null : p[4].trim());
                m.setTeamA(p[5].trim());
                m.setTeamB(p[6].trim());
                m.setVenueCity(p[7].trim());
                m.setVenueCountry(p[8].trim());

                matchGameRepository.save(m);
            }
        }
    }
}