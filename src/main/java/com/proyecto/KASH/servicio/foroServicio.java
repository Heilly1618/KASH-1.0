package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.foro;
import java.util.List;

public interface foroServicio {
    public List<foro> listarForos();

    public void guardarForo(foro nuevoForo);
}
