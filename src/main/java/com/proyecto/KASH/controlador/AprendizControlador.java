package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AprendizComponenteServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AprendizControlador {

    //Perfil
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @GetMapping("/aprendiz")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(aprendiz.getIdUsuario());
        model.addAttribute("foto", foto);
        model.addAttribute("aprendiz", aprendiz);

        return "aprendiz/aprendiz";  // Retorna la vista donde se mostrará la foto
    }

    @GetMapping("/fotoAprendiz/{id}")
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

    @PostMapping("/aprendiz/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(aprendiz.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:/asesor"; // Redirige al perfil después de subir la foto
    }

    //Asesorias
    //Asistencia
    @RequestMapping("/aprendiz/asesorias")
    public String Aprendiz3() {
        return "aprendiz/asesorias";
    }

    //Asistencia
    @RequestMapping("/aprendiz/asistencia")
    public String Aprendiz4() {
        return "aprendiz/asistencia";
    }

    //Confirmar
    @RequestMapping("/aprendiz/confirmar")
    public String Aprendiz5() {
        return "aprendiz/confirmar";
    }

    //Componente
    @Autowired
    private AprendizComponenteServicio ComponenteServicio;

    @GetMapping("/aprendiz/componente")
    public String RegistrarComponente(HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }
        List<String> componentesDisponibles = ComponenteServicio.obtenerNombresComponentesUnicos();

        // Obtener los componentes elegidos por el asesor
        List<Componente> componentesElegidos = ComponenteServicio.obtenerComponentePorUsuario(aprendiz.getIdUsuario());

        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("componentesElegidos", componentesElegidos);

        return "aprendiz/componente";

    }
    
    @PostMapping("/aprendiz/componente")
    public String registrarComponente(@RequestParam("componente") String componenteNombre, HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Comprobar que el asesor no haya elegido ya 3 componentes
        if (ComponenteServicio.obtenerComponentePorUsuario(aprendiz.getIdUsuario()).size() >= 3) {
            model.addAttribute("mensaje", "No puedes registrar más de 3 componentes.");
            return "redirect:/asesor/componente";  // Redirige al formulario
        }

        // Crear y guardar el nuevo componente
        Componente nuevoComponente = new Componente();
        nuevoComponente.setNombre(componenteNombre);
        nuevoComponente.setUsuario(aprendiz);

        ComponenteServicio.guardarComponente(nuevoComponente);

        return "redirect:/aprendiz/componente";  // Redirige de nuevo para actualizar la lista
    }
    
    @PostMapping("/aprendiz/eliminarComponente")
    @Transactional
    public String eliminarComponente(@RequestParam("id") Long componenteId, HttpSession session, Model model) {

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";  // Si no hay sesión, redirige al index
        }

        // Llamamos al servicio para eliminar la relación
        ComponenteServicio.eliminarComponente(componenteId, aprendiz    .getIdUsuario());

        // Mostrar mensaje de éxito
        model.addAttribute("mensaje", "Componente eliminado exitosamente.");
        return "redirect:/aprendiz/componente";  // Redirige a la vista donde se muestran los componentes
    }
    
    //Prueba
    @RequestMapping("/aprendiz/prueba")
    public String Aprendiz7() {
        return "aprendiz/prueba";
    }

}
