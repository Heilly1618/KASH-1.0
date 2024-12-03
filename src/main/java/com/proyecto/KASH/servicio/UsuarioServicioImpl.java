package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private Usuario2Repositorio Repositorio;

    @Override
    public List<Usuario> ListarUsuario() {
        List<Usuario> usuarios = Repositorio.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios en la base de datos");
        }
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarPorIdUsuario(Long idUsuario) {
        Optional<Usuario> usuario = Repositorio.findById(idUsuario);
        if (usuario.isPresent()) {
            return usuario;  // Usuario encontrado
        } else {
            // En caso de que no se encuentre el usuario, puedes hacer algo aquí
            System.out.println("Usuario no encontrado con ID: " + idUsuario);
            return Optional.empty();  // Usuario no encontrado
        }
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        if (Repositorio.existsById(idUsuario)) {
            Repositorio.deleteById(idUsuario);
            System.out.println("Usuario con ID " + idUsuario + " eliminado exitosamente.");
        } else {
            System.out.println("Usuario con ID " + idUsuario + " no encontrado.");
        }
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
       Repositorio.save(usuario);
    }

}
