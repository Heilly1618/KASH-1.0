package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IngresoControlador {

    @Autowired
    private UsuarioServicio2 servicio;

    @GetMapping("/index")
    public String mostrarIndex() {
        return "index"; // Asegúrate de que este método existe
    }

    @GetMapping("/ingresar")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioValidado = servicio.findByUsuarioAndPass(usuario, pass).orElse(null);

        if (usuarioValidado != null) {
            String rol = usuarioValidado.getRolSeleccionado();
            session.setAttribute("usuario", usuarioValidado);

            System.out.println("Usuario encontrado: " + usuarioValidado);
            System.out.println("Rol del usuario: " + rol);

            session.setAttribute("usuario", usuarioValidado);

            switch (rol.trim().toLowerCase()) {
                case "coordinador":
                    return "redirect:/coordinador";
                case "aprendiz":
                    return "redirect:/aprendiz";
                case "asesor":
                    return "redirect:/asesor";
                default:
                    redirectAttributes.addFlashAttribute("mensajeError", "Rol no válido.");
                    return "redirect:/";
            }

        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Usuario o contraseña incorrectos.");
            return "redirect:/";
        }
    }

    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Eliminar la sesión
        return "redirect:/"; // Redirigir al inicio de sesión
    }

}
