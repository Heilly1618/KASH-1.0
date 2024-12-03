package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.ComentarioRepositorio;
import com.proyecto.KASH.entidad.Comentario;
import com.proyecto.KASH.servicio.ComentarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/comentarios")
public class ComentarioControlador {

    @Autowired
    private ComentarioServicio servicio;

    @Autowired
    private ComentarioRepositorio comentarioRepositorio;


    @GetMapping("/{idForo}")
    @ResponseBody
    public List<Comentario> obtenerComentarios(@PathVariable("idForo") int idForo) {
        return servicio.ObtenerComentario(idForo);
    }
    
  
  
  
}
