package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import java.util.List;

public interface GrupoServicio {
    public void registrarAprendizEnComponente(Long idUsuario, String nombreComponente);
        
    public List<Grupo> buscarGruposPorNombres(List<String> nombres);
    
    public Grupo buscarPorId(Integer id);
    
    public void guardar(Grupo grupo);

    public void asignarAsesorAGrupo(Integer idGrupo, Long idUsuario);

    public List<Grupo> obtenerGruposAceptadosPorAsesor(Long idUsuario);

    public Grupo obtenerGrupoPorId(int idGrupo);

    public Grupo obtenerPorId(Integer grupoId);

    public List<Grupo> obtenerGruposPorAsesor(Long idAsesor);

    public List<Grupo> obtenerGruposActivosPorAsesor(Long idAsesor);

    public List<Grupo> obtenerGruposPorComponente(String nombreComponente);
    
    public int contarGruposPorComponente(String nombreComponente);
}
