package com.proyecto.KASH.controlador;


import com.proyecto.KASH.entidad.foro;
import com.proyecto.KASH.servicio.foroServicio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ForoFormularioControlador {
    
    @Autowired
    private foroServicio foroServicio;

    @PostMapping("/foroNuevo")
    public String foroNuevo(
            @RequestParam String nombre,
            @RequestParam int idUsuario,
            @RequestParam LocalDate fecha,
            @RequestParam int trimestre,
            @RequestParam String tema,
            @RequestParam String contenido,
            Model model) {

        // Crear un nuevo objeto Foro
        foro nuevoForo = new foro();
        nuevoForo.setNombre(nombre);
        nuevoForo.setIdUsuario(idUsuario);
        nuevoForo.setFecha(LocalDate.EPOCH);
        nuevoForo.setTrimestre(trimestre);
        nuevoForo.setTema(tema);
        nuevoForo.setContenido(contenido);

        // Guardar el foro a través del servicio
        foroServicio.guardarForo(nuevoForo);

        // Agregar mensaje de éxito al modelo
        model.addAttribute("mensaje", "El foro se ha creado correctamente");

        // Redirigir o renderizar la página de confirmación
        return "redirect:/foro"; // Nombre de la vista a mostrar tras guardar
    }
}
