package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.ChatMensajeDTO;
import com.proyecto.Learnia.dto.ConversacionDTO;
import com.proyecto.Learnia.dto.MensajeDTO;
import com.proyecto.Learnia.entity.Mensaje;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import com.proyecto.Learnia.repository.MensajeRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MensajeServiceImp implements MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeminiService geminiService;

    public MensajeServiceImp(MensajeRepository mensajeRepository, UsuarioRepository usuarioRepository, GeminiService geminiService) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.geminiService = geminiService;
    }

    @Override
    public MensajeDTO enviarMensaje(Long idEmisor, Long idReceptor, String contenido) {
        if (idEmisor.equals(idReceptor)) {
            throw new IllegalArgumentException("No puedes enviarte un mensaje a ti mismo");
        }
        Usuario emisor = usuarioRepository.findById(idEmisor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado"));
        Usuario receptor = usuarioRepository.findById(idReceptor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario receptor no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setLeido(false);
        mensaje = mensajeRepository.save(mensaje);

        if (receptor.isEsBot()) {
            responderComoBot(emisor, receptor, contenido);
        }

        return aDTO(mensaje, idEmisor);
    }

    private void responderComoBot(Usuario usuarioHumano, Usuario bot, String contenido) {
        try {
            List<Mensaje> historialPrevio = mensajeRepository.findConversacion(usuarioHumano.getIdUsuario(), bot.getIdUsuario());
            List<ChatMensajeDTO> historial = new ArrayList<>();
            for (int i = 0; i < historialPrevio.size() - 1; i++) {
                Mensaje m = historialPrevio.get(i);
                ChatMensajeDTO turno = new ChatMensajeDTO();
                turno.setRole(m.getEmisor().getIdUsuario().equals(bot.getIdUsuario()) ? "model" : "user");
                turno.setTexto(m.getContenido());
                historial.add(turno);
            }

            String respuesta;
            if (geminiService.contieneContenidoProhibido(contenido)) {
                respuesta = "Prefiero no responder a ese tipo de contenido. ¿Tienes alguna duda académica en la que pueda ayudarte?";
            } else {
                respuesta = geminiService.generarRespuesta(contenido, historial);
            }

            Mensaje respuestaBot = new Mensaje();
            respuestaBot.setContenido(respuesta);
            respuestaBot.setEmisor(bot);
            respuestaBot.setReceptor(usuarioHumano);
            respuestaBot.setLeido(false);
            mensajeRepository.save(respuestaBot);
        } catch (Exception e) {
            e.printStackTrace();
            Mensaje respuestaError = new Mensaje();
            respuestaError.setContenido("Lo siento, no pude procesar tu mensaje en este momento. Intenta de nuevo en unos segundos.");
            respuestaError.setEmisor(bot);
            respuestaError.setReceptor(usuarioHumano);
            respuestaError.setLeido(false);
            mensajeRepository.save(respuestaError);
        }
    }

    @Override
    public List<MensajeDTO> obtenerConversacion(Long idUsuarioActual, Long idOtroUsuario) {
        List<Mensaje> mensajes = mensajeRepository.findConversacion(idUsuarioActual, idOtroUsuario);

        List<Mensaje> noLeidos = mensajeRepository.findNoLeidos(idUsuarioActual, idOtroUsuario);
        if (!noLeidos.isEmpty()) {
            noLeidos.forEach(m -> m.setLeido(true));
            mensajeRepository.saveAll(noLeidos);
        }

        return mensajes.stream()
                .map(m -> aDTO(m, idUsuarioActual))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversacionDTO> listarConversaciones(Long idUsuarioActual) {
        List<Mensaje> mensajes = mensajeRepository.findTodosLosMensajesDe(idUsuarioActual);

        LinkedHashMap<Long, Usuario> interlocutores = new LinkedHashMap<>();
        Map<Long, Mensaje> ultimoMensajePorUsuario = new HashMap<>();

        for (Mensaje m : mensajes) {
            Usuario otro = m.getEmisor().getIdUsuario().equals(idUsuarioActual) ? m.getReceptor() : m.getEmisor();
            Long idOtro = otro.getIdUsuario();
            if (!interlocutores.containsKey(idOtro)) {
                interlocutores.put(idOtro, otro);
                ultimoMensajePorUsuario.put(idOtro, m);
            }
        }

        List<ConversacionDTO> resultado = new ArrayList<>();
        for (Long idOtro : interlocutores.keySet()) {
            Usuario otro = interlocutores.get(idOtro);
            Mensaje ultimo = ultimoMensajePorUsuario.get(idOtro);
            long noLeidos = mensajeRepository.countByReceptor_IdUsuarioAndEmisor_IdUsuarioAndLeidoFalse(idUsuarioActual, idOtro);

            resultado.add(new ConversacionDTO(
                    otro.getIdUsuario(),
                    otro.getNombreUsuario(),
                    otro.getFotoUsuario(),
                    otro.isEnLinea(),
                    ultimo.getContenido(),
                    ultimo.getFechaEnvio(),
                    noLeidos
            ));
        }
        return resultado;
    }

    @Override
    public long contarNoLeidos(Long idUsuarioActual) {
        return mensajeRepository.countByReceptor_IdUsuarioAndLeidoFalse(idUsuarioActual);
    }

    private MensajeDTO aDTO(Mensaje m, Long idUsuarioActual) {
        boolean propio = m.getEmisor().getIdUsuario().equals(idUsuarioActual);
        return new MensajeDTO(
                m.getIdMensaje(),
                m.getContenido(),
                m.getFechaEnvio(),
                propio,
                m.getEmisor().getIdUsuario(),
                m.getEmisor().getNombreUsuario(),
                m.getEmisor().getFotoUsuario()
        );
    }
}
