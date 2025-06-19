package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;

public interface GrupoAprendizServicio {

    List<Grupo> obtenerGruposPorAprendiz(Long idAprendiz);
    
    List<Grupo> obtenerGruposActivosPorAprendiz(Long idAprendiz);

    List<Usuario> obtenerAprendicesPorGrupo(int idGrupo);

    List<GrupoAprendiz> obtenerGrupoAprendizPorGrupo(int idGrupo);

    boolean verificarAprendizEnGrupo(Long idAprendiz, int idGrupo);
    
    int contarAprendicesEnGrupo(int idGrupo);
    
    void guardarGrupoAprendiz(GrupoAprendiz grupoAprendiz);
}
