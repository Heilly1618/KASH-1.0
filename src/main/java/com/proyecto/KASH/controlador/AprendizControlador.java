package com.proyecto.KASH.controlador;

import static com.itextpdf.text.html.HtmlTags.A;
import static com.itextpdf.text.pdf.Barcode128.Barcode128CodeSet.A;
import com.proyecto.KASH.Repository.AprendizAsistenciaRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.ResultadoPruebaRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AprendizAsesoriaServicio;
import com.proyecto.KASH.servicio.AprendizAsistenciaServicio;
import com.proyecto.KASH.servicio.AprendizComponenteServicio;
import com.proyecto.KASH.servicio.AprendizPruebaServicio;
import com.proyecto.KASH.servicio.AsesorAsistenciaServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.GrupoAprendizServicio;
import com.proyecto.KASH.servicio.GrupoServicio;
import com.proyecto.KASH.servicio.ResultadoPruebaServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static javax.swing.text.html.HTML.Tag.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.format.DateTimeFormatter;

@Controller
public class AprendizControlador {

    //Perfil
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @Autowired
    private GrupoServicio grupoServicio;

   

    @GetMapping("/fotoAprendiz/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable("id") Long idUsuario) throws IOException, SQLException {
        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(idUsuario);
        if (foto != null) {
            // Convierte el Blob a un arreglo de bytes
            byte[] fotoBytes = foto.getFoto().getBytes(1, (int) foto.getFoto().length());

            // Devuelve la imagen como un ResponseEntity con el tipo de contenido adecuado
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // O usa el tipo adecuado para tu imagen (image/png, etc.)
                    .body(fotoBytes);
        }
        // Si no se encuentra la foto, retorna una respuesta 404 (No encontrado)
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/aprendiz/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto,
            @RequestParam("redirectUrl") String redirectUrl,
            HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";
        }

        if (!foto.isEmpty()) {
            try {
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(aprendiz.getIdUsuario());

                if (fotoExistente != null) {
                    fotoServicio.actualizarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    fotoServicio.guardarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }   
        }

        // Redirige a la misma URL de donde se subió la foto
        return "redirect:" + redirectUrl;
    }

    //Grupos
    @Autowired
    private GrupoAprendizServicio grupoAprendizServicio;

    @Autowired
    private GrupoRepositorio grupoRepositorio;

    @GetMapping("/aprendiz/grupo")
    public String mostrarGruposDelAprendiz(HttpSession session, Model model, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();

        // 1. Grupos actuales del aprendiz
        List<Grupo> gruposDelAprendiz = grupoAprendizServicio.obtenerGruposPorAprendiz(idAprendiz);

        // 2. Asesorías activas
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // 3. Grupos disponibles ya filtrados desde la BD
        List<Grupo> gruposDisponibles = grupoRepositorio.findGruposDisponiblesParaAprendiz(idAprendiz);

        // Enviar al modelo
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("grupos", gruposDelAprendiz);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposDisponibles);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "aprendiz/grupo";
    }

    @GetMapping("/aprendiz/grupos/disponibles")
    public String mostrarGruposDisponibles(
            @RequestParam(required = false) String filtro,
            HttpSession session,
            Model model, HttpServletRequest request) {

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();

        // Buscar grupos que el aprendiz no tenga y que no tengan el mismo nombre que los grupos a los que ya pertenece
        List<Grupo> gruposDisponibles = grupoRepositorio.findGruposNoAsignadosYSinNombreDuplicado(idAprendiz);
        
        // Aplicar filtro si existe
        if (filtro != null && !filtro.isEmpty()) {
            String filtroLower = filtro.toLowerCase();
            gruposDisponibles = gruposDisponibles.stream()
                    .filter(g -> g.getNombre().toLowerCase().contains(filtroLower) || 
                           (g.getAsesor() != null && g.getAsesor().getNombres().toLowerCase().contains(filtroLower)))
                    .collect(Collectors.toList());
        }

        // Asesorías activas (si la vista necesita marcar algo)
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // Enviar al modelo SOLO lo necesario
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposDisponibles);
        model.addAttribute("filtro", filtro);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "aprendiz/grupo";
    }

    // Eliminamos la referencia a JdbcTemplate ya que usaremos JPA

    @PostMapping("/aprendiz/entrarGrupo")
    public String entrarGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Long idUsuario = usuario.getIdUsuario();
        
        // Obtener grupos activos del aprendiz
        List<Grupo> gruposActivos = grupoAprendizServicio.obtenerGruposActivosPorAprendiz(idUsuario);
        
        // Verificar límite de grupos activos (máximo 3)
        if (gruposActivos.size() >= 3) {
            redirectAttributes.addFlashAttribute("error", "No puedes unirte a más grupos, ya tienes varios grupos activos.");
            return "redirect:/aprendiz/grupo";
        }

        try {
            // Crear una nueva entrada directamente en la tabla grupo_aprendiz
            GrupoAprendiz grupoAprendiz = new GrupoAprendiz();
            Grupo grupo = new Grupo();
            grupo.setId(idGrupo.intValue());
            grupoAprendiz.setGrupo(grupo);
            grupoAprendiz.setUsuario(usuario);
            
            grupoAprendizServicio.guardarGrupoAprendiz(grupoAprendiz);
            
            redirectAttributes.addFlashAttribute("mensaje", "Te has unido al grupo exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al unirse al grupo: " + e.getMessage());
        }
        
        return "redirect:/aprendiz/grupo";
    }

    //Asistencia
    @Autowired
    private AprendizAsistenciaServicio asistenciaServicio;

    @Autowired
    private AprendizAsistenciaRepositorio asistenciaRepositorio;

    @RequestMapping("/aprendiz/asistencia")
    public String Aprendiz4(@RequestParam(name = "fecha", required = false) String fechaStr,
            Model model, HttpSession session, HttpServletRequest request) {

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        String nombre = aprendiz.getNombres(); // Agregar el nombre
        
        // Formatear fechas según localización
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Establecer la fecha actual si no se proporciona
        LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty()) 
                ? LocalDate.parse(fechaStr, formato) 
                : LocalDate.now();
        
        // Obtener asistencias para el aprendiz y la fecha especificada
        List<Asistencia> asistencias = asistenciaServicio.getAsistenciaByFechaAndAprendiz(fecha, aprendiz.getIdUsuario());
        
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("nombre", nombre); // Agregar el nombre al modelo
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fecha", fecha.format(formato));
        model.addAttribute("redirectUrl", request.getRequestURI());
        
        return "aprendiz/asistencia";
    }

    //asesorias
    @Autowired
    private AprendizAsesoriaServicio asesoriaServicio;

    @RequestMapping("/aprendiz/asesorias")
    public String Aprendiz5(Model model, HttpSession session, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();
        String nombre = aprendiz.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAprendiz(idAprendiz);

        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }

            int idGrupo = a.getGrupo().getId();
            Asesoria actual = asesoriasPorGrupo.get(idGrupo);

            // Si no hay una asesoría registrada aún para este grupo
            if (actual == null) {
                asesoriasPorGrupo.put(idGrupo, a);
            } else {
                // Elegir la asesoría más cercana a hoy (en adelante si existe, si no la más reciente)
                long distanciaActual = Math.abs(ChronoUnit.DAYS.between(hoy, actual.getFecha()));
                long distanciaNueva = Math.abs(ChronoUnit.DAYS.between(hoy, a.getFecha()));

                if (distanciaNueva < distanciaActual
                        || (distanciaNueva == distanciaActual && a.getFecha().isBefore(actual.getFecha()))) {
                    asesoriasPorGrupo.put(idGrupo, a);
                }
            }
        }

        List<Asesoria> asesoriasFiltradas = new ArrayList<>(asesoriasPorGrupo.values());
        asesoriasFiltradas.sort((a1, a2) -> {
            boolean a1EsFutura = !a1.getFecha().isBefore(hoy);
            boolean a2EsFutura = !a2.getFecha().isBefore(hoy);

            // Primero las asesorías futuras (o de hoy), luego las pasadas
            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            // Dentro de cada grupo, ordenarlas por fecha ascendente
            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy)) // Solo futuras o de hoy
                .min(Comparator.comparing(Asesoria::getFecha)) // La más cercana
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);

        // Contadores por grupo único
        Set<Integer> gruposTotales = new HashSet<>();
        Set<Integer> gruposActivos = new HashSet<>();
        Set<Integer> gruposCompletados = new HashSet<>();

        for (Asesoria a : asesoriasFiltradas) {
            int idGrupo = a.getGrupo().getId();
            gruposTotales.add(idGrupo);

            if ("Activa".equalsIgnoreCase(a.getEstado())) {
                gruposActivos.add(idGrupo);
            }

            if (a.isCompletada()) {
                gruposCompletados.add(idGrupo);
            }
        }

        model.addAttribute("totalAsesorias", gruposTotales.size());
        model.addAttribute("asesoriasActivas", gruposActivos.size());
        model.addAttribute("asesoriasCompletadas", gruposCompletados.size());

        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "aprendiz/asesorias";
    }

    //Componente
    @Autowired
    private AprendizComponenteServicio ComponenteServicio;

    @GetMapping("/aprendiz/componente")
    public String RegistrarComponente(
            @RequestParam(required = false) String buscar,
            HttpSession session, Model model, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }
        
        String nombre = aprendiz.getNombres();
        
        // Obtener solo los componentes que el aprendiz no ha registrado
        List<String> componentesDisponibles = ComponenteServicio.obtenerComponentesNoRegistradosPorUsuario(aprendiz.getIdUsuario());
        
        // Filtrar por búsqueda si se proporciona
        if (buscar != null && !buscar.isEmpty()) {
            String buscarLower = buscar.toLowerCase();
            componentesDisponibles = componentesDisponibles.stream()
                    .filter(componente -> componente.toLowerCase().contains(buscarLower))
                    .collect(Collectors.toList());
        }

        // Obtener los componentes elegidos por el usuario
        List<Componente> componentesElegidos = ComponenteServicio.obtenerComponentePorUsuario(aprendiz.getIdUsuario());

        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("componentesElegidos", componentesElegidos);
        model.addAttribute("buscar", buscar);
        model.addAttribute("nombre", nombre);
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "aprendiz/componente";
    }

    @PostMapping("/aprendiz/componente")
    public String registrarComponente(@RequestParam("componente") String componenteNombre,
            HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (ComponenteServicio.obtenerComponentePorUsuario(aprendiz.getIdUsuario()).size() >= 3) {
            redirectAttributes.addFlashAttribute("error", "No puedes registrar más de 3 componentes.");
            return "redirect:/aprendiz/componente";
        }

        // Verificar que el componente seleccionado existe en la lista de disponibles
        List<String> componentesDisponibles = ComponenteServicio.obtenerNombresComponentesUnicos();
        if (!componentesDisponibles.contains(componenteNombre)) {
            redirectAttributes.addFlashAttribute("error", "El componente seleccionado no es válido.");
            return "redirect:/aprendiz/componente";
        }

        Componente nuevoComponente = new Componente();
        nuevoComponente.setNombre(componenteNombre);
        nuevoComponente.setUsuario(aprendiz);
        ComponenteServicio.guardarComponente(nuevoComponente);

        // Registrar al aprendiz en el componente (esto ejecutará el procedimiento almacenado)
        grupoServicio.registrarAprendizEnComponente(aprendiz.getIdUsuario(), componenteNombre);
        
        redirectAttributes.addFlashAttribute("mensaje", "Componente registrado exitosamente.");
        return "redirect:/aprendiz/componente";
    }

    @PostMapping("/aprendiz/eliminarComponente")
    @Transactional
    public String eliminarComponente(@RequestParam("id") Long componenteId, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";
        }

        boolean eliminado = ComponenteServicio.eliminarComponenteSiNoTieneAsesoriasActivas(componenteId);

        if (!eliminado) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el componente porque tienes asesorías activas.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Componente eliminado exitosamente.");
        }

        return "redirect:/aprendiz/componente";
    }

    //Prueba
    @Autowired
    private AprendizPruebaServicio pruebaServicio;

    @Autowired
    private ResultadoPruebaServicio resultadoPruebaServicio;

    @Autowired
    private ResultadoPruebaRepositorio resultadoPruebaRepo;

    @RequestMapping("/aprendiz/prueba")
    public String Aprendiz7(HttpSession session, Model model, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }
        List<Prueba> pruebas = pruebaServicio.obtenerPruebasPorAprendiz(aprendiz.getIdUsuario());
        model.addAttribute("pruebas", pruebas);

        Long idAprendiz = aprendiz.getIdUsuario();
        String nombre = aprendiz.getNombres(); // Agregar el nombre

        // Obtener todos los grupos del aprendiz
        List<Grupo> todosGrupos = grupoAprendizServicio.obtenerGruposPorAprendiz(idAprendiz);

        // Mapa para guardar resultados de las pruebas respondidas
        Map<Long, Double> resultados = new HashMap<>();

        for (Prueba prueba : pruebas) {
            Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaServicio.obtenerResultadoPorAprendizYPrueba(idAprendiz, prueba.getId());
            resultadoOpt.ifPresent(resultado -> resultados.put(prueba.getId(), resultado.getPuntuacion()));
        }

        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre); // Agregar el nombre al modelo
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("grupos", todosGrupos);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "aprendiz/prueba";
    }

    @PostMapping("/resolverPrueba/{id}")
    public String procesarRespuestas(@PathVariable Long id,
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        
        if (aprendiz == null) {
            return "redirect:/index";
        }
        
        Map<Long, Long> respuestasSeleccionadas = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("pregunta_")) {
                Long idPregunta = Long.parseLong(entry.getKey().replace("pregunta_", ""));
                Long idRespuesta = Long.parseLong(entry.getValue());
                respuestasSeleccionadas.put(idPregunta, idRespuesta);
            }
        }

        Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaRepo.findByAprendizIdAndPruebaId(aprendiz.getIdUsuario(), id);

        double puntuacion;
        if (resultadoOpt.isPresent()) {
            puntuacion = pruebaServicio.actualizarPrueba(aprendiz.getIdUsuario(), id, respuestasSeleccionadas, resultadoOpt.get());
            redirectAttributes.addFlashAttribute("mensaje", "Prueba actualizada correctamente. Tu puntuación es: " + puntuacion);
        } else {
            puntuacion = pruebaServicio.resolverPrueba(aprendiz.getIdUsuario(), id, respuestasSeleccionadas);
            redirectAttributes.addFlashAttribute("mensaje", "Prueba completada correctamente. Tu puntuación es: " + puntuacion);
        }

        return "redirect:/aprendiz/prueba";
    }

    @GetMapping("/aprendiz/grupo/{idGrupo}/asesorias")
    @ResponseBody
    public Map<String, Object> obtenerAsesoriasGrupo(@PathVariable int idGrupo, HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();
        
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            respuesta.put("error", "Sesión no válida");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }
        
        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            respuesta.put("error", "Grupo no encontrado");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }
        
        // Verificar que el aprendiz pertenece a este grupo
        boolean perteneceAlGrupo = grupoAprendizServicio.verificarAprendizEnGrupo(aprendiz.getIdUsuario(), idGrupo);
        if (!perteneceAlGrupo) {
            respuesta.put("error", "No tienes acceso a este grupo");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }
        
        // Añadir el nombre del grupo a la respuesta
        respuesta.put("nombreGrupo", grupo.getNombre());
        
        List<Asesoria> asesorias = asesoriaServicio.obtenerAsesoriasPorGrupo(grupo);
        
        // Convertir a formato JSON amigable
        List<Map<String, Object>> asesoriasMap = new ArrayList<>();
        for (Asesoria asesoria : asesorias) {
            Map<String, Object> asesoriaMap = new HashMap<>();
            asesoriaMap.put("id", asesoria.getId());
            asesoriaMap.put("fecha", asesoria.getFecha().toString());
            asesoriaMap.put("fechaFormateada", asesoria.getFechaFormateada());
            asesoriaMap.put("horaFormateada", asesoria.getHoraFormateada());
            asesoriaMap.put("estado", asesoria.getEstado());
            asesoriaMap.put("completada", asesoria.isCompletada());
            asesoriaMap.put("link", asesoria.getLink());
            asesoriasMap.add(asesoriaMap);
        }
        
        respuesta.put("asesorias", asesoriasMap);
        return respuesta;
    }

}
