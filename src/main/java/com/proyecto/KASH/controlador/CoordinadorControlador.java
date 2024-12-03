package com.proyecto.KASH.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoordinadorControlador {

    @RequestMapping("/coordinador")
    public String Coordinador() {
        return "coordinador/coordinador";
    }

    @RequestMapping("/coordinador/foro")
    public String Coordinador1() {
        return "coordinador/foro";
    }

    @RequestMapping("/coordinador/PQRS")
    public String Coordinador2() {
        return "coordinador/PQRS";
    }

    @RequestMapping("/coordinador/usuarios")
    public String Coordinador3() {
        return "coordinador/usuarios";
    }
}
