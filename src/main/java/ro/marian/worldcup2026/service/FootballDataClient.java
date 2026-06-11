package ro.marian.worldcup2026.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ro.marian.worldcup2026.config.FootballDataProperties;
import ro.marian.worldcup2026.dto.FootballDataMatchDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
public class FootballDataClient {

    private final FootballDataProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public FootballDataClient(FootballDataProperties properties,
                              ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<FootballDataMatchDto> fetchCompetitionMatches() {

        if (!properties.isEnabled()) {
            return List.of();
        }

        if (properties.getApiToken() == null || properties.getApiToken().isBlank()) {
            throw new IllegalStateException("football-data.api-token is missing");
        }

        String url = properties.getBaseUrl()
                + "/competitions/"
                + properties.getCompetitionCode()
                + "/matches";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Auth-Token", properties.getApiToken())
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Football-data API error: HTTP "
                                + response.statusCode()
                                + " - "
                                + response.body()
                );
            }

            return parseMatches(response.body());

        } catch (IOException e) {
            throw new IllegalStateException("Football-data API IO error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Football-data API request interrupted", e);
        }
    }

    private List<FootballDataMatchDto> parseMatches(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode matchesNode = root.path("matches");

        List<FootballDataMatchDto> result = new ArrayList<>();

        if (!matchesNode.isArray()) {
            return result;
        }

        for (JsonNode m : matchesNode) {
            FootballDataMatchDto dto = new FootballDataMatchDto();

            dto.setExternalId(readLong(m, "id"));
            dto.setStatus(readText(m, "status"));

            dto.setUtcKickoffAt(parseUtcDateTime(readText(m, "utcDate")));

            dto.setHomeTeamName(m.path("homeTeam").path("name").asText(null));
            dto.setAwayTeamName(m.path("awayTeam").path("name").asText(null));

            JsonNode fullTime = m.path("score").path("fullTime");

            dto.setHomeScore(readInteger(fullTime, "home"));
            dto.setAwayScore(readInteger(fullTime, "away"));

            result.add(dto);
        }

        return result;
    }

    private LocalDateTime parseUtcDateTime(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return OffsetDateTime
                .parse(value)
                .atZoneSameInstant(ZoneId.of("Europe/Bucharest"))
                .toLocalDateTime();
    }

    private String readText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private Long readLong(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    private Integer readInteger(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }
}