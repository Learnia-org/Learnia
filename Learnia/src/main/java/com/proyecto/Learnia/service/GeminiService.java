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
        if (digitos >= 2 && soloLetras.length() <= limpio.length() / 2) {
            return true;
        }

        if (soloLetras.length() < 3) {
            return false;
        }

        long vocales = soloLetras.chars().filter(c -> VOCALES.indexOf(c) >= 0).count();
        double proporcionVocales = (double) vocales / soloLetras.length();
        if (proporcionVocales < 0.15) {
            return false;
        }
        if (limpio.matches(".*(.)\\1{3,}.*")) {
            return false;
        }
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

    public boolean esRespuestaApropiada(String contextoPregunta, String respuesta) {
        String prompt = """
                Eres el moderador de contenido del foro educativo Learnia, donde estudiantes
                responden dudas académicas de otros estudiantes.

                Pregunta original: %s
                Respuesta enviada por un estudiante: %s

                Evalúa si la respuesta es apropiada para una comunidad educativa respetuosa:
                no debe contener insultos, lenguaje discriminatorio u ofensivo, burlas hacia quien
                pregunta, contenido de odio o sexual, ni ser spam o publicidad. NO hace falta que
                sea la respuesta correcta o completa al tema: alcanza con que sea un aporte de
                buena fe y respetuoso (aunque sea breve o poco útil).

                Responde EXACTAMENTE una única palabra, sin nada más antes ni después:
                APROPIADA
                o
                INAPROPIADA
                """.formatted(contextoPregunta, respuesta);

        String resultado = generarRespuesta(prompt, null);
        String limpio = resultado == null ? "" : resultado.trim().toUpperCase();

        if (limpio.startsWith("INAPROPIADA")) {
            return false;
        }
        return true;
    }

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
        return new ResultadoPregunta(false, null);
    }

    /**
     * Resultado de evaluar la respuesta de un estudiante a un ejercicio o duda puntual.
     * Permite detectar si hubo un error conceptual y explicar al estudiante por qué
     * se equivocó, con una sugerencia concreta para corregirlo.
     */
    public record ResultadoEvaluacion(boolean correcta, String tipoError, String explicacion, String sugerencia) {}

    public ResultadoEvaluacion evaluarRespuestaEjercicio(String enunciado, String respuestaEstudiante) {
        String prompt = """
                Eres un tutor de IA experto de Learnia, especializado en detectar errores
                conceptuales de estudiantes de Matemática, Física, Inglés e Informática.

                Enunciado o tema planteado: %s
                Respuesta del estudiante: %s

                Evalúa si la respuesta es conceptualmente correcta. Si el enunciado no es una
                pregunta con respuesta verificable sino un tema abierto, evalúa si el estudiante
                demuestra una comprensión correcta del concepto.

                Responde EXACTAMENTE en este formato, con cada etiqueta en su propia línea,
                sin nada más antes ni después, sin markdown ni LaTeX:
                RESULTADO: CORRECTA o INCORRECTA
                TIPO_ERROR: <nombre breve del error conceptual, o "NINGUNO" si es correcta>
                EXPLICACION: <por qué está bien o por qué está mal, en 2 a 4 oraciones claras>
                SUGERENCIA: <un consejo concreto y breve para reforzar o corregir el concepto>
                """.formatted(enunciado, respuestaEstudiante);

        String respuesta = generarRespuesta(prompt, null);
        return parsearResultadoEvaluacion(respuesta);
    }

    private ResultadoEvaluacion parsearResultadoEvaluacion(String texto) {
        Map<String, String> campos = parsearCamposEtiquetados(texto,
                Set.of("RESULTADO", "TIPO_ERROR", "EXPLICACION", "SUGERENCIA"));

        boolean correcta = campos.getOrDefault("RESULTADO", "INCORRECTA").toUpperCase().startsWith("CORRECTA");
        String tipoError = campos.get("TIPO_ERROR");
        if (tipoError != null && tipoError.equalsIgnoreCase("NINGUNO")) {
            tipoError = null;
        }
        String explicacion = campos.getOrDefault("EXPLICACION",
                "No fue posible generar una explicación detallada en este momento.");
        String sugerencia = campos.getOrDefault("SUGERENCIA", "Repasa el tema y vuelve a intentarlo.");

        return new ResultadoEvaluacion(correcta, tipoError, explicacion, sugerencia);
    }

    /**
     * Resultado del análisis del perfil de aprendizaje de un estudiante:
     * fortalezas, debilidades, estilo y ritmo de aprendizaje detectados por la IA.
     */
    public record ResultadoPerfil(String fortalezas, String debilidades, String estilo, String ritmo, String resumen) {}

    public ResultadoPerfil generarPerfilAprendizaje(String contextoEstadisticas, String contextoErrores) {
        String prompt = """
                Eres el sistema de IA de Learnia encargado de construir el perfil de
                aprendizaje de un estudiante a partir de su desempeño real en la plataforma.

                Estadísticas de desempeño del estudiante:
                %s

                Errores conceptuales recientes detectados:
                %s

                A partir de estos datos, infiere el perfil de aprendizaje del estudiante.
                Responde EXACTAMENTE en este formato, con cada etiqueta en su propia línea,
                sin nada más antes ni después, sin markdown ni LaTeX:
                FORTALEZAS: <temas o habilidades donde el estudiante muestra buen dominio, breve>
                DEBILIDADES: <temas o habilidades donde el estudiante tiene dificultades, breve>
                ESTILO: <uno de VISUAL, AUDITIVO, LECTOESCRITURA o KINESTESICO, el más probable>
                RITMO: <uno de LENTO, MODERADO o RAPIDO>
                RESUMEN: <2 a 3 oraciones resumiendo el momento de aprendizaje del estudiante y
                una recomendación general para avanzar>
                """.formatted(contextoEstadisticas, contextoErrores);

        String respuesta = generarRespuesta(prompt, null);
        Map<String, String> campos = parsearCamposEtiquetados(respuesta,
                Set.of("FORTALEZAS", "DEBILIDADES", "ESTILO", "RITMO", "RESUMEN"));

        return new ResultadoPerfil(
                campos.getOrDefault("FORTALEZAS", "Aún no hay suficientes datos para identificar fortalezas."),
                campos.getOrDefault("DEBILIDADES", "Aún no hay suficientes datos para identificar debilidades."),
                campos.getOrDefault("ESTILO", "LECTOESCRITURA"),
                campos.getOrDefault("RITMO", "MODERADO"),
                campos.getOrDefault("RESUMEN", "Todavía estamos conociendo tu forma de aprender.")
        );
    }

    /**
     * Parsea una respuesta de texto plano con formato ETIQUETA: valor (una por línea),
     * tolerando que el valor de una etiqueta continúe en las líneas siguientes hasta
     * encontrar la próxima etiqueta conocida.
     */
    private Map<String, String> parsearCamposEtiquetados(String texto, Set<String> etiquetas) {
        Map<String, String> resultado = new java.util.HashMap<>();
        if (texto == null || texto.isBlank()) {
            return resultado;
        }

        String etiquetaActual = null;
        StringBuilder valorActual = new StringBuilder();

        for (String linea : texto.split("\\R")) {
            String lineaTrim = linea.trim();
            String etiquetaEncontrada = null;
            for (String etiqueta : etiquetas) {
                if (lineaTrim.toUpperCase().startsWith(etiqueta + ":")) {
                    etiquetaEncontrada = etiqueta;
                    break;
                }
            }

            if (etiquetaEncontrada != null) {
                if (etiquetaActual != null) {
                    resultado.put(etiquetaActual, valorActual.toString().trim());
                }
                etiquetaActual = etiquetaEncontrada;
                valorActual = new StringBuilder(lineaTrim.substring(etiquetaEncontrada.length() + 1).trim());
            } else if (etiquetaActual != null && !lineaTrim.isBlank()) {
                valorActual.append(" ").append(lineaTrim);
            }
        }
        if (etiquetaActual != null) {
            resultado.put(etiquetaActual, valorActual.toString().trim());
        }
        return resultado;
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
