package com.smartagriculture.crop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CropApiIntegrationTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Autowired
    private CropRepository cropRepository;

    @BeforeEach
    void clearCrops() {
        cropRepository.deleteAll();
    }

    @Test
    void createsAndListsCropAndRejectsInvalidInput() throws Exception {
        String requestBody = """
                {
                  "name": "Maize",
                  "variety": "Hybrid",
                  "season": "KHARIF",
                  "soilType": "Loamy",
                  "expectedYieldKgPerHectare": 5200.00,
                  "notes": "Example crop record"
                }
                """;

        HttpResponse<String> created = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, created.statusCode());
        assertTrue(created.headers().firstValue("Location").orElse("").matches(".*/api/v1/crops/\\d+$"));
        assertTrue(created.body().contains("\"name\":\"Maize\""));

        HttpResponse<String> listed = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listed.statusCode());
        assertTrue(listed.body().contains("\"season\":\"KHARIF\""));

        HttpResponse<String> invalid = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {"name":" ","season":"RABI"}
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(400, invalid.statusCode());
        assertTrue(invalid.body().contains("fieldErrors"));
    }

    @Test
    void replacesDeletesAndReturnsNotFoundForDeletedCrop() throws Exception {
        HttpResponse<String> created = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("""
                                {"name":"Wheat","season":"RABI"}
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(201, created.statusCode());
        URI cropUri = URI.create(created.headers().firstValue("Location").orElseThrow());

        HttpResponse<String> updated = httpClient.send(
                HttpRequest.newBuilder(cropUri)
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString("""
                                {"name":"Barley","season":"RABI"}
                                """))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updated.statusCode());
        assertTrue(updated.body().contains("\"name\":\"Barley\""));

        HttpResponse<String> deleted = httpClient.send(
                HttpRequest.newBuilder(cropUri).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(204, deleted.statusCode());

        HttpResponse<String> missing = httpClient.send(
                HttpRequest.newBuilder(cropUri).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(404, missing.statusCode());
        assertTrue(missing.body().contains("was not found"));
    }

    @Test
    void searchesByNameAndSeasonWithBoundedPagination() throws Exception {
        cropRepository.saveAll(List.of(
                new Crop("Maize", "Hybrid", CropSeason.KHARIF, "Loamy", new BigDecimal("5200.00"), null),
                new Crop("Mango", "Alphonso", CropSeason.PERENNIAL, "Laterite", null, null),
                new Crop("Wheat", "HD-2967", CropSeason.RABI, "Loamy", new BigDecimal("4800.00"), null)));

        HttpResponse<String> filtered = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops/search?name=ma&season=KHARIF&page=0&size=1"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, filtered.statusCode());
        assertTrue(filtered.body().contains("\"name\":\"Maize\""));
        assertTrue(filtered.body().contains("\"totalElements\":1"));

        HttpResponse<String> paginated = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops/search?page=0&size=2"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, paginated.statusCode());
        assertTrue(paginated.body().contains("\"size\":2"));
        assertTrue(paginated.body().contains("\"totalElements\":3"));
        assertTrue(paginated.body().contains("\"totalPages\":2"));
        assertTrue(paginated.body().contains("\"hasNext\":true"));

        HttpResponse<String> invalidSize = httpClient.send(
                HttpRequest.newBuilder(uri("/api/v1/crops/search?size=101"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(400, invalidSize.statusCode());
        assertTrue(invalidSize.body().contains("Invalid crop search parameters"));
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
