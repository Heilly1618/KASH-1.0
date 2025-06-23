package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/coordinador")
public class ReporteUsuariosControlador {

    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    @Autowired
    private UsuarioServicio2 UsuarioService;

    @GetMapping("/reporte-usuarios")
    public ResponseEntity<byte[]> generarReporte() throws Exception {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        ByteArrayInputStream bis = UsuarioService.generarReporteUsuarios(usuarios);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=usuarios.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }

    @GetMapping("/reporte-usuario-individual")
    public ResponseEntity<InputStreamResource> generarReporteIndividual(@RequestParam("id") Long id) throws Exception {
        Usuario usuario = UsuarioService.buscarPorId(id); // Asegúrate que el método exista
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayInputStream bis = UsuarioService.generarReporteUsuarioIndividual(usuario);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=usuario_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
