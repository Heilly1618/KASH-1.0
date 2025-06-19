package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AprendizAsesoriaRepositorio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Grupo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AprendizAsesoriaServicioImpl implements AprendizAsesoriaServicio{
    
    @Autowired
    private AprendizAsesoriaRepositorio repositorio;
    
    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    @Override
    public List<Asesoria> obtenerAsesoriasPorAprendiz(Long idAprendiz) {
        return repositorio.findAsesoriasPorAprendiz(idAprendiz);
    }


    @Override
    public List<Asesoria> obtenerAsesoriasActivas() {
         return asesoriaRepositorio.findByEstado("Activa");
    }
    
    @Override
    public List<Asesoria> obtenerAsesoriasPorGrupo(Grupo grupo) {
        return asesoriaRepositorio.findByGrupo(grupo);
    }
}
