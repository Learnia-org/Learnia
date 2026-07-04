package com.proyecto.Learnia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Guarda una imagen de perfil en {uploadDir}/perfiles y devuelve la ruta
     * pública ("/uploads/perfiles/archivo.jpg") para guardar en la base de datos.
     */
    public String guardarFotoPerfil(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) return null;

        Path dir = Paths.get(uploadDir, "perfiles");
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path destino = dir.resolve(filename);
        Files.write(destino, archivo.getBytes());

        return "/uploads/perfiles/" + filename;
    }
}
