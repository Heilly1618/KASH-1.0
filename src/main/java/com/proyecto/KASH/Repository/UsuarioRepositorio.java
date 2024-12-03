package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
   
}
