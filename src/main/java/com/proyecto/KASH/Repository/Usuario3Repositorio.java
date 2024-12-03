package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface Usuario3Repositorio extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByUsuarioAndPass(String usuario, String pass);
}
