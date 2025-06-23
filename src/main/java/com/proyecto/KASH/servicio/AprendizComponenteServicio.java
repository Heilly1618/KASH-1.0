package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import java.util.List;

public interface AprendizComponenteServicio {

    List<Componente> obtenerComponentePorUsuario(Long idUsuario);

    List<String> obtenerNombresComponentesUnicos();

    Componente guardarComponente(Componente componente);

    void eliminarComponente(Long idComponente, Long idUsuario);
}
