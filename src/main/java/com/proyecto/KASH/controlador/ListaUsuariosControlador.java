    package com.proyecto.KASH.controlador;

    import com.proyecto.KASH.Repository.Usuario2Repositorio;
    import com.proyecto.KASH.entidad.Usuario;
    import com.proyecto.KASH.servicio.UsuarioServicio;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.ModelAttribute;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    @Controller
    public class ListaUsuariosControlador {

        @Autowired
        private UsuarioServicio Servicio;

        @Autowired
        private Usuario2Repositorio Repositorio;

        @GetMapping("/coordinador/usuarios")
        public String verUsuarios(Model model) {
            List<Usuario> usuarios = Servicio.ListarUsuario(); // Obtener usuarios
            System.out.println("Número de usuarios: " + usuarios.size()); // Verificar cuántos usuarios se recuperaron
            if (usuarios.isEmpty()) {
                System.out.println("No se encontraron usuarios en la base de datos");
            }
            model.addAttribute("usuarios", usuarios); // Pasar la lista al modelo
            return "coordinador/usuarios"; // Nombre de la vista Thymeleaf
        }

        @GetMapping("/coordinador/buscar")
        public String Buscar(@RequestParam(required = false) Long idUsuario, Model model) {
            // Crear la lista de usuarios, siempre inicializada
            List<Usuario> usuarios = new ArrayList<>();

            // Verificar si se proporcionó un idUsuario
            if (idUsuario != null) {
                // Buscar un usuario por su ID
                Optional<Usuario> usuario = Servicio.buscarPorIdUsuario(idUsuario);

                // Si el usuario existe, añadirlo a la lista
                usuario.ifPresent(usuarios::add); // Si el usuario existe, lo agrega a la lista
            } else {
                // Si no se proporcionó idUsuario, listar todos los usuarios
                usuarios.addAll(Servicio.ListarUsuario()); // Usar addAll para agregar todos los usuarios
            }

            // Pasar la lista de usuarios al modelo
            model.addAttribute("usuarios", usuarios);

            // Retornar la vista
            return "coordinador/usuarios";
        }

        @PostMapping("/coordinador/eliminar")
        public String eliminarUsuario(@RequestParam("idUsuario") Long idUsuario) {
            Servicio.eliminarUsuario(idUsuario); // Implementa esta lógica en el servicio.
            return "redirect:/coordinador/usuarios"; // Redirige a la lista de usuarios.
        }

        @GetMapping("/coordinador/editar/{id}")
        public String editarUsuario(@PathVariable("id") Long id, Model model) {
            Optional<Usuario> usuario = Servicio.buscarPorIdUsuario(id);
            if (usuario.isPresent()) {
                model.addAttribute("usuarios", usuario.get());
                return "coordinador/editarUsuario"; // Esto debe corresponder con tu archivo Thymeleaf
            } else {
                return "redirect:/coordinador/usuarios";  // Redirigir si no se encuentra el usuario
            }
        }

        @PostMapping("/coordinador/editar")
        public String editarUsuario(
                @RequestParam Long idUsuario,
                @RequestParam String rolSeleccionado,
                @RequestParam String nombres,
                @RequestParam String primerA,
                @RequestParam(value = "segundoA", required = false) String segundoA,
                @RequestParam(required = false) LocalDate fNacimiento,
                @RequestParam String tDocumento,
                @RequestParam String trimestre,
                @RequestParam String correo,
                @RequestParam String numero,
                @RequestParam String usuario,
                @RequestParam String pass,
                Model model
        ) {
            // Buscar el usuario existente en la base de datos
            Optional<Usuario> usuarioExistente = Repositorio.findById(idUsuario);
            if (!usuarioExistente.isPresent()) {
                model.addAttribute("error", "El usuario no existe.");
                return "coordinador/editarUsuario"; // Retorna con el error si el usuario no se encuentra
            }

            Usuario usuarioParaActualizar = usuarioExistente.get();
            // Actualizar los valores
            usuarioParaActualizar.setRolSeleccionado(rolSeleccionado);
            usuarioParaActualizar.setNombres(nombres);
            usuarioParaActualizar.setPrimerA(primerA);
            usuarioParaActualizar.setSegundoA(segundoA);
            usuarioParaActualizar.setFNacimiento(fNacimiento);
            usuarioParaActualizar.setTDocumento(tDocumento);
            usuarioParaActualizar.setTrimestre(trimestre);
            usuarioParaActualizar.setCorreo(correo);
            usuarioParaActualizar.setNumero(numero);
            usuarioParaActualizar.setUsuario(usuario);
            usuarioParaActualizar.setPass(pass);
            usuarioParaActualizar.setIdUsuario(idUsuario);
            
             model.addAttribute("usuarios", usuarioParaActualizar);

           
            Repositorio.save(usuarioParaActualizar);
          

            // Redirigir a la lista de usuarios después de la edición
            return "redirect:/coordinador/usuarios";
        }

    }
