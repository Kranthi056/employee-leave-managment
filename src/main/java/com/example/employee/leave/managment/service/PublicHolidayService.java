package com.example.employee.leave.managment.service;



import com.example.employee.leave.managment.model.PublicHoliday;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublicHolidayService {

    private static final String API_BASE    = "https://date.nager.at/api/v3/PublicHolidays";
    private static final String COUNTRY     = "IN";   // India — fixed
    private final ObjectMapper  objectMapper = new ObjectMapper();
    private final HttpClient    httpClient   = HttpClient.newHttpClient();

    /**
     * Returns upcoming Indian public holidays for the current year.
     * Falls back to an empty list if the API is unreachable.
     */
    public List<PublicHoliday> getUpcomingIndianHolidays() {
        int year = LocalDate.now().getYear();
        String url = API_BASE + "/" + year + "/" + COUNTRY;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return new ArrayList<>();
            }

            JsonNode root      = objectMapper.readTree(response.body());
            LocalDate today    = LocalDate.now();
            List<PublicHoliday> holidays = new ArrayList<>();

            for (JsonNode node : root) {
                LocalDate date = LocalDate.parse(node.get("date").asText());
                if (date.isBefore(today)) continue;          // skip past holidays

                String name      = node.get("name").asText("");
                String localName = node.has("localName")
                        ? node.get("localName").asText(name)
                        : name;

                String type = "Public";
                if (node.has("types") && node.get("types").isArray()
                        && node.get("types").size() > 0) {
                    type = node.get("types").get(0).asText("Public");
                }

                holidays.add(new PublicHoliday(date, name, localName, type));
            }

            return holidays;

        } catch (Exception e) {
            // API unreachable — return empty list, template handles gracefully
            return new ArrayList<>();
        }
    }
}