package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.UsuarioRepositorio;
import com.proyecto.KASH.entidad.Usuario;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrarControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @RequestMapping("/registrar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("nuevoUsuario", new Usuario());
        return "registrar"; 
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(
            @RequestParam String rolSeleccionado,
            @RequestParam String nombres,
            @RequestParam String primerA,
            @RequestParam String segundoA,
            @RequestParam LocalDate fNacimiento,
            @RequestParam String tDocumento,
            @RequestParam Long idUsuario,
            @RequestParam String trimestre,
            @RequestParam String correo,
            @RequestParam String numero,
            @RequestParam String usuario,
            @RequestParam String pass,
            Model model
    ) {
        // Crear una nueva instancia de Usuario
        Usuario nuevoUsuario = new Usuario();

        // Asignar los valores del formulario al nuevo usuario
        nuevoUsuario.setRolSeleccionado(rolSeleccionado);
        nuevoUsuario.setNombres(nombres);
        nuevoUsuario.setPrimerA(primerA);
        nuevoUsuario.setSegundoA(segundoA);
        nuevoUsuario.setFNacimiento(fNacimiento);
        nuevoUsuario.setTDocumento(tDocumento);
        nuevoUsuario.setIdUsuario(idUsuario);
        nuevoUsuario.setTrimestre(trimestre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setNumero(numero);
        nuevoUsuario.setUsuario(usuario);
        nuevoUsuario.setPass(pass);

        // Guardar el nuevo usuario en la base de datos
        usuarioRepository.save(nuevoUsuario);

        // Agregar el usuario al modelo (opcional, dependiendo de la necesidad)
        model.addAttribute("usuario", nuevoUsuario);

        // Redirigir a la página de inicio (o a una página de éxito)
        return "redirect:/index";
    }
}
