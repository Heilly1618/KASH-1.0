package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.Repository.Usuario3Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioServicioImpl2 implements UsuarioServicio2 {

    @Autowired
    private Usuario3Repositorio Repositorio;

    @Override
    public Optional<Usuario> findByUsuarioAndPass(String usuario, String pass) {
        return Repositorio.findByUsuarioAndPass(usuario, pass);
    }
}
