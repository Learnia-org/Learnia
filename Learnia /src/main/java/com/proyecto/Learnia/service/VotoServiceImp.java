package com.proyecto.Learnia.service;

import com.proyecto.Learnia.dto.VotoDTO;
import com.proyecto.Learnia.entity.Respuesta;
import com.proyecto.Learnia.entity.Usuario;
import com.proyecto.Learnia.entity.Voto;
import com.proyecto.Learnia.exception.SuccesException;
import com.proyecto.Learnia.repository.RespuestaRepository;
import com.proyecto.Learnia.repository.UsuarioRepository;
import com.proyecto.Learnia.repository.VotoRepository;
import com.proyecto.Learnia.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotoServiceImp implements VotoService {

    private final VotoRepository votoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RespuestaRepository respuestaRepository;

    public VotoServiceImp(VotoRepository votoRepository,
                          UsuarioRepository usuarioRepository,
                          RespuestaRepository respuestaRepository) {
        this.votoRepository = votoRepository;
        this.usuarioRepository = usuarioRepository;
        this.respuestaRepository = respuestaRepository;
    }
    @Override
    public List<Voto> listar() {
        return votoRepository.findAll();
    }

    @Override
    public Voto crear(VotoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        Respuesta respuesta = respuestaRepository.findById(dto.getIdRespuesta())
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrada con ID: " + dto.getIdRespuesta()));
        Voto voto = new Voto();
        voto.setTipoVoto(dto.getTipoVoto());
        voto.setUsuario(usuario);
        voto.setRespuesta(respuesta);
        return votoRepository.save(voto);
    }

    @Override
    public Voto buscarPorId(Long id) {
        return votoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto no encontrado con ID: " + id));
    }

    @Override
    public Voto actualizar(Long id, VotoDTO dto) {
        Voto existente = buscarPorId(id);
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
        Respuesta respuesta = respuestaRepository.findById(dto.getIdRespuesta())
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrada con ID: " + dto.getIdRespuesta()));
        existente.setTipoVoto(dto.getTipoVoto());
        existente.setUsuario(usuario);
        existente.setRespuesta(respuesta);
        return votoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!votoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Voto no encontrado con ID: " + id);
        }
        votoRepository.deleteById(id);
        throw new SuccesException("Voto eliminado correctamente");
    }
}