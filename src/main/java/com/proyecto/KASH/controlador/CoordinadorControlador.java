package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.Comentario;
import com.proyecto.KASH.entidad.PQRS;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.foro;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.ComentarioServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.PQRSServicioImpl;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CoordinadorControlador {

    @Autowired
    private FotoServicio fotoServicio;
    
    @Autowired
    private ComentarioServicio comentarioServicio;

    @Autowired
    private PQRSServicioImpl pqrsServicio;

    @Autowired
    private foroRepositorio foroRepositorio;

    //vista coordinador perfil
    @RequestMapping("/coordinador")
    public String Coordinador(HttpSession session, Model model) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");

        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(coordinador.getIdUsuario());
        model.addAttribute("foto", foto);
        model.addAttribute("coordinador", coordinador);

        return "coordinador/coordinador";
    }

    @GetMapping("/fotoCoordinador/{id}")
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

    @PostMapping("/coordinador/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(coordinador.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, coordinador.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, coordinador.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:/coordinador"; // Redirige al perfil después de subir la foto
    }

    //vista del coordinador del foro
    @GetMapping("/coordinador/foro")
    public String mostrarForos(@RequestParam(required = false) String filtro, Model model) {
        List<foro> foros;

        if (filtro != null && !filtro.trim().isEmpty()) {
            foros = foroRepositorio.buscarPorFiltro(filtro.trim());
        } else {
            foros = foroRepositorio.findAll();
        }

        model.addAttribute("foros", foros);
        model.addAttribute("filtro", filtro);
        return "coordinador/foro";
    }

    @GetMapping("/coordinador/comentarios/{idForo}")
    @ResponseBody
    public List<Comentario> obtenerComentarios(@PathVariable int idForo) {
        return comentarioServicio.ObtenerComentario(idForo);
    }

    @DeleteMapping("/coordinador/comentarios/eliminar/{id}")
    @ResponseBody
    public void eliminarComentario(@PathVariable Long id) {
        comentarioServicio.eliminarComentario(id);
    }

    //vista del coordiandor de las PQRS
    @GetMapping("/coordinador/PQRS")
    public String mostrarPQRS(Model model) {
        List<PQRS> listaPQRS = pqrsServicio.obtenerTodasLasPQRS();
        System.out.println("Lista de PQRS: " + listaPQRS);
        model.addAttribute("pqrsList", listaPQRS);
        return "coordinador/PQRS"; // Asegúrate de que el nombre del archivo es correcto
    }

    //vista del coordinador de los usuarios
    @RequestMapping("/coordinador/usuarios")
    public String Coordinador3() {
        return "coordinador/usuarios";
    }
}
