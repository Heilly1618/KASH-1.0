package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;


public interface UsuarioServicio {
    List<Usuario> ListarUsuario();
    Optional<Usuario> buscarPorIdUsuario(Long idUsuario);

    public void eliminarUsuario(Long idUsuario);

    public void actualizarUsuario(Usuario usuario);
}
