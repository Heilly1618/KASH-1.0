package com.proyecto.KASH.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
public class AsesorControlador {
    @RequestMapping("/asesor")
    public String Aprendiz() {
        return "asesor/asesor";
    }

    @RequestMapping("/asesor/asesorias")
    public String Aprendiz2() {
        return "asesor/asesorias";
    }

    @RequestMapping("/asesor/asistencia")
    public String Aprendiz3() {
        return "asesor/asistencia";
    }

    @RequestMapping("/asesor/confirmar")
    public String Aprendiz4() {
        return "asesor/confirmar";
    }

    @RequestMapping("/asesor/componente")
    public String Aprendiz5() {
        return "asesor/componente";
    }

    @RequestMapping("/asesor/prueba")
    public String Aprendiz6() {
        return "asesor/prueba";
    }

}
