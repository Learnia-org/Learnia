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

            IMPORTANTE sobre el formato: tu respuesta se muestra como texto plano, sin ningún
            renderizado de Markdown ni de fórmulas matemáticas (LaTeX). Por lo tanto:
            - NUNCA uses sintaxis LaTeX como \\frac{}{}, \\times, \\div, \\cdot, símbolos $ o {}.
            - NUNCA uses símbolos de Markdown como **negrita**, *cursiva*, `código` o encabezados con #.
            - Para fracciones escribe "1/2", "3/4", etc. Para multiplicación usa "x" o "*", para
              división usa "÷" o "/".
            - Para listas o pasos, usa números seguidos de punto (1. 2. 3.) o guiones simples (-),
              en líneas separadas, sin ningún otro símbolo decorativo.
            - Escribe con texto plano legible, como si fuera un mensaje de chat normal.
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

    private static final List<String> PALABRAS_PROHIBIDAS = List.of(
            "metanfetamina", "metanfeta", "meta anfetamina", "anfetamina",
            "cocaina", "cocaína", "crack", "heroina", "heroína", "fentanilo",
            "droga", "drogas", "narcotico", "narcótico", "lsd", "mdma", "extasis", "éxtasis",
            "marihuana", "marijuana", "cannabis",
            "explosivo", "explosivos", "bomba casera", "fabricar arma", "fabricar bomba",
            "arma de fuego", "municion", "munición",
            "suicidarme", "suicidio", "matarme", "autolesion", "autolesión", "cortarme",
            "porno", "pornografia", "pornografía", "contenido sexual", "sexo explicito", "sexo explícito"
    );

    public boolean contieneContenidoProhibido(String texto) {
        if (texto == null) {
            return false;
        }
        String normalizado = java.text.Normalizer.normalize(texto.toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        for (String palabra : PALABRAS_PROHIBIDAS) {
            String palabraNormalizada = java.text.Normalizer.normalize(palabra.toLowerCase(), java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            if (normalizado.contains(palabraNormalizada)) {
                return true;
            }
        }
        return false;
    }

    private static final String VOCALES = "aeiouáéíóúü";

    public boolean esContenidoCoherente(String texto) {
        if (texto == null) {
            return false;
        }

        String limpio = texto.trim();
        if (limpio.length() < 4) {
            return false;
        }

        String soloLetras = limpio.toLowerCase().replaceAll("[^a-záéíóúñü]", "");
        long digitos = limpio.chars().filter(Character::isDigit).count();

        // Contenido mayormente numérico/simbólico (operaciones, fórmulas): se acepta sin más análisis
        if (digitos >= 2 && soloLetras.length() <= limpio.length() / 2) {
            return true;
        }

        if (soloLetras.length() < 3) {
            return false;
        }

        long vocales = soloLetras.chars().filter(c -> VOCALES.indexOf(c) >= 0).count();
        double proporcionVocales = (double) vocales / soloLetras.length();

        // Un texto real en español suele tener una proporción razonable de vocales
        if (proporcionVocales < 0.15) {
            return false;
        }

        // Secuencias largas de la misma letra repetida (ej. "aaaaaa")
        if (limpio.matches(".*(.)\\1{3,}.*")) {
            return false;
        }

        // Muchas consonantes seguidas suelen indicar texto aleatorio (teclado presionado al azar)
        int consecutivas = 0;
        int maxConsecutivas = 0;
        for (char c : soloLetras.toCharArray()) {
            if (VOCALES.indexOf(c) < 0) {
                consecutivas++;
                maxConsecutivas = Math.max(maxConsecutivas, consecutivas);
            } else {
                consecutivas = 0;
            }
        }

        return maxConsecutivas <= 5;
    }

    public record ResultadoPregunta(boolean valida, String respuesta) {}

    public ResultadoPregunta evaluarPregunta(String titulo, String descripcion, String categoria) {
        String prompt = """
                Eres el moderador de contenido del foro educativo Learnia, donde estudiantes hacen
                preguntas académicas sobre Matemática, Física, Inglés e Informática.

                Un estudiante envió esta pregunta:
                Categoría seleccionada: %s
                Título: %s
                Descripción: %s

                Evalúa si es una duda académica válida: coherente, relacionada con la categoría
                indicada o con temas educativos en general, y apropiada para una comunidad estudiantil
                (sin contenido ofensivo, sexual, discriminatorio, de odio, personal o completamente
                ajeno al ámbito educativo).

                Si CUMPLE con todo lo anterior, responde EXACTAMENTE en este formato, sin nada más
                antes ni después:
                VALIDA
                <aquí la respuesta educativa completa, clara y en texto plano, sin markdown ni LaTeX>

                Si NO cumple (no tiene sentido, es spam, no es un tema educativo, no corresponde con la
                categoría, o es inapropiada), responde EXACTAMENTE esta única palabra, sin nada más:
                INVALIDA
                """.formatted(categoria, titulo, descripcion);

        String respuesta = generarRespuesta(prompt, null);
        String limpio = respuesta == null ? "" : respuesta.trim();

        if (limpio.toUpperCase().startsWith("INVALIDA")) {
            return new ResultadoPregunta(false, null);
        }

        if (limpio.toUpperCase().startsWith("VALIDA")) {
            String textoRespuesta = limpio.substring(6).trim();
            return new ResultadoPregunta(true, textoRespuesta.isBlank() ? null : textoRespuesta);
        }

        // Si no se pudo interpretar el formato (p. ej. error de la API o cuota agotada),
        // se marca como pendiente de revisión en lugar de publicarla sin moderar
        return new ResultadoPregunta(false, null);
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
