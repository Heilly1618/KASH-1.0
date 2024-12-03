package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface Usuario2Repositorio extends JpaRepository<Usuario, Long> {

   public Optional<Usuario> findByIdUsuario(Long idUsuario);
   
    
}   
