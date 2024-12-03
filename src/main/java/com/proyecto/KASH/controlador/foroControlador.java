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
import jakarta.servlet.http.HttpSession;

@Controller
public class foroControlador {

    @Autowired
    private foroServicio servicio;

    @GetMapping("/foro")
    public String foro(Model modelo) {
        modelo.addAttribute("foro", servicio.listarForos());
        return "foro";
    }

    @Autowired
    private UsuarioServicio2 Servicio;

    @GetMapping("/ingresar/foro")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, Model model, HttpSession session) {
        Usuario usuarioValidado = Servicio.findByUsuarioAndPass(usuario, pass).orElse(null);
        if (usuarioValidado != null) {
            // Guarda los datos del usuario en la sesión
            session.setAttribute("usuarioSesion", usuarioValidado);

            // Redirige según el rol del usuario
            String rol = usuarioValidado.getRolSeleccionado();

            switch (rol) {
                case "coordinador":
                    return "foro"; // Redirige al coordinador
                case "aprendiz":
                    return "foro"; // Redirige al aprendiz
                case "asesor":
                    model.addAttribute("usuarios", usuarioValidado);
                    return "ForoFormulario"; // Redirige al asesor
                default:
                    model.addAttribute("error", "Rol no válido");
                    return "foro"; // Si no es un rol válido, regresa a la vista de login
            }
        } else {
            // Si las credenciales no son válidas
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "index"; // Regresa a la vista de login con mensaje de error
        }
    }

}
