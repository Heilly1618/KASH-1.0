package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IngresoControlador {
    @Autowired
    private UsuarioServicio2 Servicio;
    
    @GetMapping("/ingresar")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, Model model){
        Usuario usuarioValidado = Servicio.findByUsuarioAndPass(usuario, pass).orElse(null); 
         if (usuarioValidado != null) {
            // Redirigir según el rol del usuario
            String rol = usuarioValidado.getRolSeleccionado();

            switch (rol) {
                case "coordinador":
                    return "redirect:/coordinador"; // Redirige al coordinador
                case "aprendiz":
                    return "redirect:/aprendiz"; // Redirige al aprendiz
                case "asesor":
                    return "redirect:/asesor"; // Redirige al asesor
                default:
                    model.addAttribute("error", "Rol no válido");
                    return "index"; // Si no es un rol válido, regresa a la vista de login
            }
        } else {
            // Si las credenciales no son válidas
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "index"; // Regresa a la vista de login con mensaje de error
        }   
    }
}
