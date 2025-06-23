package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AsesorComponenteServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AsesorControlador {

    //Vista Perfil
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @GetMapping("/asesor")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(asesor.getIdUsuario());
        model.addAttribute("foto", foto);
        model.addAttribute("asesor", asesor);

        return "asesor/asesor";  // Retorna la vista donde se mostrará la foto
    }

    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable("id") Long idUsuario) throws IOException, SQLException {
        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(idUsuario);
        if (foto != null) {
            // Convierte el Blob a un arreglo de bytes
            byte[] fotoBytes = foto.getFoto().getBytes(1, (int) foto.getFoto().length());

            // Devuelve la imagen como un ResponseEntity con el tipo de contenido adecuado
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // O usa el tipo adecuado para tu imagen (image/png, etc.)
                    .body(fotoBytes);
        }
        // Si no se encuentra la foto, retorna una respuesta 404 (No encontrado)
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/asesor/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(asesor.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:/asesor"; // Redirige al perfil después de subir la foto
    }

    //Vista Asesorias
    @GetMapping("/asesor/asesorias")
    public String mostrarAsesorias() {
        return "asesor/asesorias"; // Retorna la vista para asesorías
    }

    //Vista Asistencia
    @GetMapping("/asesor/asistencia")
    public String mostrarAsistencia() {
        return "asesor/asistencia"; // Retorna la vista para asistencia
    }

    //Vista Confirmar
    @GetMapping("/asesor/confirmar")
    public String confirmarAsesorias() {
        return "asesor/confirmar"; // Retorna la vista para confirmar asesorías
    }

    //Vista Componente
    @Autowired
    private AsesorComponenteServicio componenteServicio;

    @GetMapping("/asesor/componente")
    public String registrarComponentes(HttpSession session, Model model) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Obtener la lista de componentes disponibles
        List<String> componentesDisponibles = componenteServicio.obtenerNombresComponentesUnicos();

        // Obtener los componentes elegidos por el asesor
        List<Componente> componentesElegidos = componenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario());

        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("componentesElegidos", componentesElegidos);

        return "asesor/componente"; // Retorna la vista para registrar componentes
    }

    @PostMapping("/asesor/componente")
    public String registrarComponente(@RequestParam("componente") String componenteNombre, HttpSession session, Model model) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Comprobar que el asesor no haya elegido ya 3 componentes
        if (componenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario()).size() >= 3) {
            model.addAttribute("mensaje", "No puedes registrar más de 3 componentes.");
            return "redirect:/asesor/componente";  // Redirige al formulario
        }

        // Crear y guardar el nuevo componente
        Componente nuevoComponente = new Componente();
        nuevoComponente.setNombre(componenteNombre);
        nuevoComponente.setUsuario(asesor);

        componenteServicio.guardarComponente(nuevoComponente);

        return "redirect:/asesor/componente";  // Redirige de nuevo para actualizar la lista
    }

    @PostMapping("/asesor/eliminarComponente")
    @Transactional
    public String eliminarComponente(@RequestParam("id") Long componenteId, HttpSession session, Model model) {
        // Obtener el asesor de la sesión
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";  // Si no hay sesión, redirige al index
        }

        // Llamamos al servicio para eliminar la relación
        componenteServicio.eliminarComponente(componenteId, asesor.getIdUsuario());

        // Mostrar mensaje de éxito
        model.addAttribute("mensaje", "Componente eliminado exitosamente.");
        return "redirect:/asesor/componente";  // Redirige a la vista donde se muestran los componentes
    }

    //Vista Prueba
    @GetMapping("/asesor/prueba")
    public String asignarPruebas() {
        return "asesor/prueba"; // Retorna la vista para asignar pruebas
    }
}
