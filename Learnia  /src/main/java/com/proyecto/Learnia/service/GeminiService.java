package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ChatMensajeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GeminiService {

    private static final Set<Integer> CODIGOS_REINTENTABLES = Set.of(429, 500, 503);
    private static final int MAX_INTENTOS = 3;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final String SYSTEM_INSTRUCTION = """
            Eres el asistente de IA de Learnia, una plataforma educativa donde los estudiantes
            hacen preguntas, comparten recursos y aprenden materias como Matemática, Física,
            Inglés e Informática. Tu objetivo es ayudar a los usuarios a resolver dudas
            académicas, explicar conceptos paso a paso y orientarlos dentro de la plataforma.
            Responde siempre en español, de forma clara, breve, amable y con ejemplos cuando
            ayuden a entender mejor el tema. Si te preguntan algo fuera de temas educativos,
            responde con cordialidad e intenta redirigir la conversación hacia el aprendizaje.
            """;

    public String generarRespuesta(String mensaje, List<ChatMensajeDTO> historial) {
        try {
            List<Map<String, Object>> contents = new ArrayList<>();

            if (historial != null) {
                for (ChatMensajeDTO turno : historial) {
                    if (turno.getTexto() == null || turno.getTexto().isBlank()) continue;
                    String role = "model".equalsIgnoreCase(turno.getRole()) ? "model" : "user";
                    contents.add(Map.of(
                            "role", role,
                            "parts", List.of(Map.of("text", turno.getTexto()))
                    ));
                }
            }

            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", mensaje))
            ));

            Map<String, Object> body = Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(Map.of("text", SYSTEM_INSTRUCTION))
                    ),
                    "contents", contents
            );

            String jsonBody = jsonMapper.writeValueAsString(body);

            HttpResponse<String> response = null;

            for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .timeout(Duration.ofSeconds(30))
                        .header("Content-Type", "application/json")
                        .header("x-goog-api-key", apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return extraerTexto(response.body());
                }

                System.err.println("Gemini API error (intento " + intento + "/" + MAX_INTENTOS + ") "
                        + response.statusCode() + ": " + response.body());

                boolean esReintentable = CODIGOS_REINTENTABLES.contains(response.statusCode());
                if (!esReintentable || intento == MAX_INTENTOS) {
                    break;
                }

                Thread.sleep(1000L * intento);
            }

            return "El asistente de IA está saturado o no disponible en este momento. Intenta de nuevo en unos minutos.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lo siento, no pude conectarme con el asistente de IA en este momento. Intenta de nuevo en unos segundos.";
        }
    }

    public String generarRespuestaParaPregunta(String titulo, String descripcion) {
        String mensaje = "Un estudiante publicó esta duda en el foro de Learnia.\n\n"
                + "Título: " + titulo + "\n"
                + "Descripción: " + descripcion + "\n\n"
                + "Responde la duda de forma clara, completa y educativa, como si fueras un miembro "
                + "de la comunidad ayudando a resolverla.";
        return generarRespuesta(mensaje, null);
    }

    private String extraerTexto(String rawResponse) {
        JsonNode root = jsonMapper.readTree(rawResponse);
        JsonNode candidates = root.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            return "No obtuve una respuesta clara. ¿Podrías reformular tu pregunta?";
        }

        JsonNode parts = candidates.get(0).path("content").path("parts");
        StringBuilder texto = new StringBuilder();
        if (parts.isArray()) {
            for (JsonNode part : parts) {
                if (part.has("text")) {
                    texto.append(part.get("text").asText());
                }
            }
        }

        return !texto.isEmpty()
                ? texto.toString()
                : "No obtuve una respuesta clara. ¿Podrías reformular tu pregunta?";
    }
}
