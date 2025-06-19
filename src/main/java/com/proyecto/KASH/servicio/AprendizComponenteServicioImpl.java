package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AprendizComponenteRepositorio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AprendizComponenteServicioImpl implements AprendizComponenteServicio {

    @Autowired
    private AprendizComponenteRepositorio componenteRepositorio;

    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    @Override
    public List<Componente> obtenerComponentePorUsuario(Long idUsuario) {
        return componenteRepositorio.findByUsuario_IdUsuario(idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<String> obtenerNombresComponentesUnicos() {
        return componenteRepositorio.findDistinctComponentNames(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public List<String> obtenerComponentesNoRegistradosPorUsuario(Long idUsuario) {
        // Obtener todos los nombres de componentes únicos
        List<String> todosLosComponentes = obtenerNombresComponentesUnicos();
        
        // Obtener los componentes que el usuario ya tiene
        List<String> componentesDelUsuario = obtenerComponentePorUsuario(idUsuario)
                .stream()
                .map(Componente::getNombre)
                .collect(Collectors.toList());
        
        // Filtrar para obtener solo los componentes que el usuario no tiene
        return todosLosComponentes.stream()
                .filter(nombre -> !componentesDelUsuario.contains(nombre))
                .collect(Collectors.toList());
    }

    @Override
    public Componente guardarComponente(Componente componente) {
        return componenteRepositorio.save(componente); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminarComponente(Long idComponente, Long idUsuario) {
        componenteRepositorio.deleteByIdAndUsuario_IdUsuario(idComponente, idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean eliminarComponenteSiNoTieneAsesoriasActivas(Long idComponente) {
        Componente componente = componenteRepositorio.findById(idComponente).orElse(null);
        if (componente == null) {
            return false;
        }

        Usuario aprendiz = componente.getUsuario();

        // Buscar asesorías activas donde el aprendiz está inscrito
        List<Asesoria> asesoriasActivas = asesoriaRepositorio.findAsesoriasActivasPorAprendiz(aprendiz);

        if (!asesoriasActivas.isEmpty()) {
            // Tiene asesorías activas, no eliminar
            return false;
        }

        componenteRepositorio.delete(componente);
        return true;
    }

}
