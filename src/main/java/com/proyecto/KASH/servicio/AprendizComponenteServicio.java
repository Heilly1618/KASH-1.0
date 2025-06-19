package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import java.util.List;

public interface AprendizComponenteServicio {

    List<Componente> obtenerComponentePorUsuario(Long idUsuario);

    List<String> obtenerNombresComponentesUnicos();

    List<String> obtenerComponentesNoRegistradosPorUsuario(Long idUsuario);

    Componente guardarComponente(Componente componente);

    void eliminarComponente(Long idComponente, Long idUsuario);
    
    public boolean eliminarComponenteSiNoTieneAsesoriasActivas(Long idComponente);
}
