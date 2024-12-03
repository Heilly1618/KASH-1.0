package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.foro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface foroRepositorio extends JpaRepository<foro,Long>{
    
}
