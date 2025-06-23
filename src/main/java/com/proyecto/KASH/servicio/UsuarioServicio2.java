package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Usuario;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;


public interface UsuarioServicio2 {
    Optional<Usuario> findByUsuarioAndPass(String usuario, String pass);
    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) throws Exception;

    public Usuario buscarPorId(Long id);

    public ByteArrayInputStream generarReporteUsuarioIndividual(Usuario usuario) throws Exception;
}
