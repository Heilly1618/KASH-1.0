package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Usuario;
import java.util.Optional;


public interface UsuarioServicio2 {
    Optional<Usuario> findByUsuarioAndPass(String usuario, String pass);
}
