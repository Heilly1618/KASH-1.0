package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import org.springframework.ui.Model;
import com.proyecto.KASH.servicio.foroServicio;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.foro;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class foroControlador {

    @Autowired
    private foroServicio servicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/foro")
    public String foro(Model modelo) {
        modelo.addAttribute("foro", servicio.listarForos());
        return "foro";
    }

    @Autowired
    private UsuarioServicio2 Servicio;

    @GetMapping("/ingresar/foro")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioValidado = Servicio.findByUsuarioAndPass(usuario, pass).orElse(null);

        if (usuarioValidado != null) {

            System.out.println("Usuario encontrado: " + usuarioValidado);
            System.out.println("Nombres: " + usuarioValidado.getNombres());
            System.out.println("Rol: " + usuarioValidado.getRolSeleccionado());

            session.setAttribute("usuarioSesion", usuarioValidado);
            String rol = usuarioValidado.getRolSeleccionado();

            switch (rol) {
                case "coordinador":
                case "aprendiz":
                    return "redirect:/foro";
                case "asesor":
                    session.setAttribute("usuario", usuarioValidado);
                    return "redirect:/ForoFormulario";
                default:
                    redirectAttributes.addFlashAttribute("error", "Rol no válido");
                    return "redirect:/foro";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/foro";
        }
    }

    @GetMapping("/foro/imagen/{id}")
    public void obtenerFoto(@PathVariable Long id, HttpServletResponse response) throws IOException, SQLException {
        // Recuperamos el foro por ID
        foro foro = servicio.obtenerFotoId(id);

        if (foro != null && foro.getFoto() != null) {
            // Establecemos el tipo de contenido como imagen para mostrarla en la respuesta
            response.setContentType("image/jpeg");
            response.getOutputStream().write(foro.getFoto().getBytes(1, (int) foro.getFoto().length()));
            response.getOutputStream().close();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
