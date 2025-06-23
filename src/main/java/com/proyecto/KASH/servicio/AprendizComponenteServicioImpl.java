package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AprendizComponenteRepositorio;
import com.proyecto.KASH.entidad.Componente;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AprendizComponenteServicioImpl implements AprendizComponenteServicio {
    
    @Autowired
    private AprendizComponenteRepositorio componenteRepositorio;

    @Override
    public List<Componente> obtenerComponentePorUsuario(Long idUsuario) {
       return componenteRepositorio.findByUsuario_IdUsuario(idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<String> obtenerNombresComponentesUnicos() {
        return componenteRepositorio.findDistinctComponentNames(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Componente guardarComponente(Componente componente) {
        return componenteRepositorio.save(componente); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminarComponente(Long idComponente, Long idUsuario) {
        componenteRepositorio.deleteByIdAndUsuario_IdUsuario(idComponente, idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
