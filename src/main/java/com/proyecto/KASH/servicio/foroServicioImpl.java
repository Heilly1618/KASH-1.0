package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.foro;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class foroServicioImpl implements foroServicio{
    
    @Autowired
    private foroRepositorio repositorio;
    
    @Override
    public List<foro> listarForos() {
        return repositorio.findAll();
    }

    @Override
    public void guardarForo(foro nuevoForo) {
        repositorio.save(nuevoForo);
    }
    
}
