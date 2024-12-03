package com.proyecto.KASH.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AprendizControlador {

    @RequestMapping("/aprendiz")
    public String Aprendiz() {
        return "aprendiz/aprendiz";
    }

    @RequestMapping("/aprendiz/asesorias")
    public String Aprendiz2() {
        return "aprendiz/asesorias";
    }

    @RequestMapping("/aprendiz/asistencia")
    public String Aprendiz3() {
        return "aprendiz/asistencia";
    }

    @RequestMapping("/aprendiz/confirmar")
    public String Aprendiz4() {
        return "aprendiz/confirmar";
    }

    @RequestMapping("/aprendiz/componente")
    public String Aprendiz5() {
        return "aprendiz/componente";
    }

    @RequestMapping("/aprendiz/prueba")
    public String Aprendiz6() {
        return "aprendiz/prueba";
    }

}
