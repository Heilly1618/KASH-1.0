package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.EmailService;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioControlador {

    @Autowired
    private Usuario2Repositorio usuarioRepository;

    @Autowired
    private EmailService emailService; // Inyección del servicio de correo

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("nuevoUsuario", new Usuario());
        return "registrar"; // Asegúrate de que el archivo HTML se llama registrar.html
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(
            @RequestParam String rolSeleccionado,
            @RequestParam String nombres,
            @RequestParam String primerA,
            @RequestParam String segundoA,
            @RequestParam LocalDate fNacimiento,
            @RequestParam String tDocumento,
            @RequestParam String idUsuario,
            @RequestParam String trimestre,
            @RequestParam String correo,
            @RequestParam String numero,
            @RequestParam String usuario,
            @RequestParam String pass,
            RedirectAttributes redirectAttributes
    ) {
        // **1. VALIDACIONES**
        
        //Validar que no exista otro usuario
        if (usuarioRepository.existsByUsuario(usuario)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está registrado. Intente con otro.");
            return "redirect:/registrar";
        }
        // Validar que ID usuario solo contenga números
        if (!idUsuario.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El ID de usuario debe contener solo números.");
            return "redirect:/registrar";
        }

        // Validar que la contraseña tenga letras y números
        if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$").matcher(pass).matches()) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe contener letras y números y al menos 6 caracteres.");
            return "redirect:/registrar";
        }

        // Convertir ID usuario y trimestre a números
        Long idUsuarioNum = Long.parseLong(idUsuario);
        int trimestreNum = Integer.parseInt(trimestre);

        // Validar edad mínima de 14 años
        int edad = Period.between(fNacimiento, LocalDate.now()).getYears();
        if (edad < 14) {
            redirectAttributes.addFlashAttribute("error", "No puedes registrarte si eres menor de 14 años.");
            return "redirect:/registrar";
        }

        // Validar edad según el tipo de documento
        if ((tDocumento.equals("Cedula de Ciudadania") || tDocumento.equals("Cedula de Extranjeria")) && edad < 18) {
            redirectAttributes.addFlashAttribute("error", "Debes tener al menos 18 años para usar este tipo de documento.");
            return "redirect:/registrar";
        }

        // Validar que el correo no esté vacío
        if (correo == null || correo.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico es obligatorio.");
            return "redirect:/registrar";
        }

        // Validar que si es asesor, el trimestre sea mayor a 2
        if (rolSeleccionado.equals("Asesor") && trimestreNum <= 3) {
            redirectAttributes.addFlashAttribute("error", "Para ser asesor debes estar en un trimestre mayor a 2.");
            return "redirect:/registrar";
        }

        // **2. GUARDAR USUARIO SI TODO ESTÁ BIEN**
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRolSeleccionado(rolSeleccionado);
        nuevoUsuario.setNombres(nombres);
        nuevoUsuario.setPrimerA(primerA);
        nuevoUsuario.setSegundoA(segundoA);
        nuevoUsuario.setFNacimiento(fNacimiento);
        nuevoUsuario.setTDocumento(tDocumento);
        nuevoUsuario.setIdUsuario(idUsuarioNum);
        nuevoUsuario.setTrimestre(trimestre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setNumero(numero);
        nuevoUsuario.setUsuario(usuario);
        nuevoUsuario.setPass(pass);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);  // Cambiado a Usuario, no boolean
        System.out.println("Usuario guardado: " + usuarioGuardado);
        if (usuarioGuardado != null) { // Verificamos si el usuario fue guardado correctamente
            // **3. ENVIAR CORREO DE BIENVENIDA**
            String asunto = "Bienvenido a KASH, " + usuario + "!";
            String mensaje = "Hola " + usuario + ",\n\n"
                    + "Tu rol en KASH es: " + rolSeleccionado + ".\n\n"
                    + "En este espacio tendras exreperiencia en las asesorias, un asesor las impartira y los aprendices la tomaran"
                    + "\n\n"
                    + "También podrás disfrutar del foro y participar en otras actividades.\n\n"
                    + "¡Bienvenido a la comunidad de KASH!";

            try {
                emailService.sendEmail(usuarioGuardado.getCorreo(), asunto, mensaje);
                redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente. Revisa tu correo.");
            } catch (Exception e) {
                System.out.println("Error al enviar el correo: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Error al enviar el correo, pero el usuario fue registrado.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al registrar el usuario.");
        }

        return "redirect:/index";  // Redirige a index con mensaje de éxito o error

    }

}
