package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.PreguntaRepositorio;
import com.proyecto.KASH.Repository.PruebaRepositorio;
import com.proyecto.KASH.Repository.RespuestaRepositorio;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.Respuesta;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsesorPruebaServicioImpl implements AsesorPruebaServicio {

    @Autowired
    private PruebaRepositorio pruebaRepositorio;

    @Autowired
    private PreguntaRepositorio preguntaRepositorio;

    @Autowired
    private RespuestaRepositorio respuestaRepositorio;

    @Override
    @Transactional
    public void guardarPrueba(Prueba prueba, List<Pregunta> preguntas) {

        Prueba pruebaGuardada = pruebaRepositorio.save(prueba);

        for (Pregunta pregunta : preguntas) {
            pregunta.setPrueba(pruebaGuardada);

            Pregunta preguntaGuardada = preguntaRepositorio.save(pregunta);

            for (Respuesta respuesta : pregunta.getRespuestas()) {
                respuesta.setPregunta(preguntaGuardada);
                respuestaRepositorio.save(respuesta);
            }

        }
    }

}
