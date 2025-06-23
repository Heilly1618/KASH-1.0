package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.GrupoAprendizRepositorio;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrupoAprendizServicioImpl implements GrupoAprendizServicio {

    @Autowired
    private GrupoAprendizRepositorio grupoAprendizRepositorio;

    @Override
    public List<Grupo> obtenerGruposPorAprendiz(Long idAprendiz) {
        return grupoAprendizRepositorio.findGruposByAprendiz(idAprendiz);
    }
    
    @Override
    public List<Grupo> obtenerGruposActivosPorAprendiz(Long idAprendiz) {
        // Obtener todos los grupos del aprendiz
        List<Grupo> todosLosGrupos = obtenerGruposPorAprendiz(idAprendiz);
        
        // Filtrar solo los grupos con estado "Activo"
        return todosLosGrupos.stream()
                .filter(grupo -> "Activo".equalsIgnoreCase(grupo.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> obtenerAprendicesPorGrupo(int idGrupo) {
        return grupoAprendizRepositorio.findUsuariosByGrupoId(idGrupo);
    }

    @Override
    public List<GrupoAprendiz> obtenerGrupoAprendizPorGrupo(int idGrupo) {
        return grupoAprendizRepositorio.findByGrupo_Id(idGrupo);
    }

    @Override
    public boolean verificarAprendizEnGrupo(Long idAprendiz, int idGrupo) {
        return grupoAprendizRepositorio.existsByUsuarioIdUsuarioAndGrupoId(idAprendiz, idGrupo);
    }
    
    @Override
    public int contarAprendicesEnGrupo(int idGrupo) {
        return grupoAprendizRepositorio.findByGrupo_Id(idGrupo).size();
    }
    
    @Override
    public void guardarGrupoAprendiz(GrupoAprendiz grupoAprendiz) {
        grupoAprendizRepositorio.save(grupoAprendiz);
    }
    
    @Override
    public boolean eliminarAprendizDeGrupo(Long idGrupo, Long idAprendiz) {
        try {
            grupoAprendizRepositorio.deleteByGrupo_IdAndUsuario_IdUsuario(idGrupo.intValue(), idAprendiz);
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar aprendiz del grupo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeAprendizEnGrupo(Long idAprendiz, int idGrupo) {
        return verificarAprendizEnGrupo(idAprendiz, idGrupo);
    }

    @Override
    public boolean registrarAprendizEnGrupo(Long idAprendiz, int idGrupo) {
        try {
            // Verificar si ya existe la relación
            if (existeAprendizEnGrupo(idAprendiz, idGrupo)) {
                return false;
            }
            
            // Crear la relación grupo-aprendiz
            GrupoAprendiz grupoAprendiz = new GrupoAprendiz();
            
            // Crear y asignar el grupo
            Grupo grupo = new Grupo();
            grupo.setId(idGrupo);
            grupoAprendiz.setGrupo(grupo);
            
            // Crear y asignar el usuario
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idAprendiz);
            grupoAprendiz.setUsuario(usuario);
            
            // Guardar la relación
            guardarGrupoAprendiz(grupoAprendiz);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error al registrar aprendiz en grupo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
