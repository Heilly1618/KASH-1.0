package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.Repository.GrupoAprendizRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.PreguntaRepositorio;
import com.proyecto.KASH.Repository.PruebaRepositorio;
import com.proyecto.KASH.Repository.RespuestaRepositorio;
import com.proyecto.KASH.Repository.RespuestaSeleccionadaRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.Respuesta;
import com.proyecto.KASH.entidad.RespuestaSeleccionada;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AsesorAsistenciaServicio;
import com.proyecto.KASH.servicio.AsesorComponenteServicio;
import com.proyecto.KASH.servicio.AsesorPruebaServicio;
import com.proyecto.KASH.servicio.AsesoriaServicio;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.GrupoServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.TemporalQueries.localDate;
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
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.server.ServerWebExchangeExtensionsKt.principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AsesorControlador {

    // foto
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @GetMapping("/foto/{id}")
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

    @GetMapping("/fotoAsesor/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerFotoAsesor(@PathVariable("id") Long idUsuario) throws IOException, SQLException {
        try {
            fotos foto = fotoServicio.obtenerFotoPorIdUsuario(idUsuario);
            if (foto != null && foto.getFoto() != null) {
                // Convierte el Blob a un arreglo de bytes
                byte[] fotoBytes = foto.getFoto().getBytes(1, (int) foto.getFoto().length());

                // Devuelve la imagen como un ResponseEntity con el tipo de contenido adecuado
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(fotoBytes);
            } else {
                // Si no se encuentra la foto, devolvemos una respuesta exitosa pero vacía
                // El cliente mostrará la imagen por defecto definida en el atributo alt del HTML
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new byte[0]);
            }
        } catch (Exception e) {
            // En caso de cualquier error, simplemente devolvemos una respuesta exitosa pero vacía
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new byte[0]);
        }
    }

    @PostMapping("/asesor/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model, @RequestParam("redirectUrl") String redirectUrl) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(asesor.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:" + redirectUrl; // Redirige al perfil después de subir la foto
    }

    @Autowired
    private AsesoriaServicio asesoriaServicio;

    @RequestMapping("/asesor/asesorias")
    public String AsesorAsesorias(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();
        String nombre = asesor.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAsesor(idAsesor); // <- importante cambiar método

        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }

            int idGrupo = a.getGrupo().getId();
            Asesoria actual = asesoriasPorGrupo.get(idGrupo);

            if (actual == null) {
                asesoriasPorGrupo.put(idGrupo, a);
            } else {
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

            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy))
                .min(Comparator.comparing(Asesoria::getFecha))
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);

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

        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/asesorias";
    }

    @PostMapping("/asesor/asesoria/{id}/completar")
    public String marcarComoCompletada(@PathVariable("id") int id, RedirectAttributes redirect) {
        Optional<Asesoria> opt = asesoriaRepositorio.findById(id);
        if (opt.isPresent()) {
            Asesoria asesoria = opt.get();
            
            // Verificar si la fecha y hora de la asesoría ya pasaron
            LocalDate fechaActual = LocalDate.now();
            LocalTime horaActual = LocalTime.now();
            
            boolean fechaPasada = asesoria.getFecha().isBefore(fechaActual);
            boolean mismaFechaHoraPasada = asesoria.getFecha().isEqual(fechaActual) && 
                                          asesoria.getHora().isBefore(horaActual);
            
            if (fechaPasada || mismaFechaHoraPasada) {
                asesoria.setEstado("Inactiva");
                asesoria.setCompletada(true);
                asesoriaRepositorio.save(asesoria);
                redirect.addFlashAttribute("mensaje", "Asesoría marcada como completada.");
            } else {
                redirect.addFlashAttribute("error", "No se puede marcar como completada una asesoría que aún no ha ocurrido.");
            }
        }
        return "redirect:/asesor/asesorias";
    }

    //Vista crearAsesorias
    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailService correoServicio;

    @Autowired
    private GrupoServicio grupoServicio;

    @Autowired
    private AsesoriaRepositorio asesoriasRepositorio;

    @GetMapping("/asesor/crearAsesorias")
    public String crearAsesorias(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        if (asesor != null) {
            // 1. Grupos del asesor
            List<Grupo> gruposAsignados = grupoServicio.obtenerGruposAceptadosPorAsesor(asesor.getIdUsuario());

            // 2. Grupos con asesorías ya creadas
            List<Grupo> gruposConAsesoria = asesoriasRepositorio.findDistinctGrupoByAsesorId(asesor.getIdUsuario());

            // 3. Filtrar grupos sin asesoría
            List<Grupo> gruposSinAsesoria = gruposAsignados.stream()
                    .filter(grupo -> !gruposConAsesoria.contains(grupo))
                    .collect(Collectors.toList());

            model.addAttribute("grupos", gruposSinAsesoria);
        }

        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("fechaManana", manana);

        return "asesor/crearAsesorias";
    }

    @PostMapping("/asesor/guardarAsesoria")
    public String guardarAsesoria(
            @RequestParam("grupoId") int idGrupo,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam("link") String link,
            @RequestParam("diasAsesoria") int diasAsesoria,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            RedirectAttributes redirectAttributes
    ) {

        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            redirectAttributes.addFlashAttribute("error", "El grupo no existe.");
            return "redirect:/asesor/crearAsesorias";
        }
        
        // Validar que la hora no esté en el rango de 10 PM a 6 AM
        LocalTime horaInicio = LocalTime.of(22, 0); // 10 PM
        LocalTime horaFin = LocalTime.of(6, 0); // 6 AM
        
        if ((hora.isAfter(horaInicio) || hora.equals(horaInicio)) || (hora.isBefore(horaFin) || hora.equals(horaFin))) {
            redirectAttributes.addFlashAttribute("error", "No se pueden programar asesorías entre las 10:00 PM y las 6:00 AM.");
            return "redirect:/asesor/crearAsesorias";
        }
        
        // Validar que la fecha de inicio sea al menos del día siguiente
        LocalDate mañana = LocalDate.now().plusDays(1);
        if (fechaInicio.isBefore(mañana)) {
            redirectAttributes.addFlashAttribute("error", "La fecha de inicio debe ser al menos a partir de mañana.");
            return "redirect:/asesor/crearAsesorias";
        }

        // Aquí haces la validación de conflicto, por ejemplo llamando al repositorio o servicio
        if (asesoriaServicio.existeConflicto(grupo, fecha, hora)) {
            redirectAttributes.addFlashAttribute("error", "El grupo o asesor ya tienen una asesoría en esa fecha y hora.");
            return "redirect:/asesor/crearAsesorias";
        }

        Asesoria asesoria = new Asesoria();
        asesoria.setGrupo(grupo);
        asesoria.setFecha(fecha);
        asesoria.setHora(hora);
        asesoria.setLink(link);
        asesoria.setDias_asesoria(diasAsesoria);
        asesoria.setFecha_inicio(fechaInicio);
        asesoria.setEstado("Activa");

        // Solo aquí llamas al método void que guarda y envía correos
        asesoriaServicio.crearAsesoria(asesoria);

        redirectAttributes.addFlashAttribute("mensaje", "Asesoría creada y notificada correctamente.");
        return "redirect:/asesor/crearAsesorias";
    }

    @Autowired
    private AsesorAsistenciaServicio asistenciaServicio;

    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    //Vista Asistencia
    @RequestMapping("/asesor/asistencia")
    public String AsesorAsistencia(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();
        String nombre = asesor.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAsesor(idAsesor);

        LocalDate hoy = LocalDate.now();
        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }
            int idGrupo = a.getGrupo().getId();
            
            // Si ya existe una asesoría para este grupo, verificar si ya tiene asistencia registrada
            if (asesoriasPorGrupo.containsKey(idGrupo)) {
                Asesoria actual = asesoriasPorGrupo.get(idGrupo);
                
                // Si la asesoría actual ya tiene asistencia registrada, buscar la siguiente
                if (asistenciaServicio.existeAsistenciaRegistrada((long)actual.getId())) {
                    // Si la asesoría es de hoy, mantenerla
                    if (actual.getFecha().equals(hoy)) {
                        continue;
                    }
                    
                    // Si la nueva asesoría es de hoy, reemplazar
                    if (a.getFecha().equals(hoy)) {
                        asesoriasPorGrupo.put(idGrupo, a);
                        continue;
                    }
                    
                    // Si ninguna es de hoy, elegir la más cercana a futuro
                    if (a.getFecha().isAfter(hoy) && 
                        (actual.getFecha().isBefore(hoy) || a.getFecha().isBefore(actual.getFecha()))) {
                        asesoriasPorGrupo.put(idGrupo, a);
                    }
                }
                // Si no tiene asistencia registrada, mantener la más cercana a hoy
                else {
                    long distanciaActual = Math.abs(ChronoUnit.DAYS.between(hoy, actual.getFecha()));
                    long distanciaNueva = Math.abs(ChronoUnit.DAYS.between(hoy, a.getFecha()));

                    if (distanciaNueva < distanciaActual
                            || (distanciaNueva == distanciaActual && a.getFecha().isBefore(actual.getFecha()))) {
                        asesoriasPorGrupo.put(idGrupo, a);
                    }
                }
            } else {
                // Si es la primera asesoría para este grupo, guardarla
                asesoriasPorGrupo.put(idGrupo, a);
            }
        }

        List<Asesoria> asesoriasFiltradas = new ArrayList<>(asesoriasPorGrupo.values());

        asesoriasFiltradas.sort((a1, a2) -> {
            if (a1.isCompletada() && !a2.isCompletada()) {
                return 1;
            }
            if (!a1.isCompletada() && a2.isCompletada()) {
                return -1;
            }

            boolean a1EsFutura = !a1.getFecha().isBefore(hoy);
            boolean a2EsFutura = !a2.getFecha().isBefore(hoy);

            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy))
                .min(Comparator.comparing(Asesoria::getFecha))
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        Set<Integer> gruposTotales = new HashSet<>();
        Set<Integer> gruposActivos = new HashSet<>();
        Set<Integer> gruposCompletados = new HashSet<>();

        Map<Integer, List<Usuario>> aprendicesPorAsesoria = new HashMap<>();
        Map<Integer, Boolean> asistenciaRegistrada = new HashMap<>();

        for (Asesoria a : asesoriasFiltradas) {
            int idGrupo = a.getGrupo().getId();
            gruposTotales.add(idGrupo);

            if ("Activa".equalsIgnoreCase(a.getEstado())) {
                gruposActivos.add(idGrupo);
            }

            if (a.isCompletada()) {
                gruposCompletados.add(idGrupo);
            }

            // Llenar aprendices del grupo de esta asesoría
            List<Usuario> usuarioAprendiz = new ArrayList<>();
            for (GrupoAprendiz ga : a.getGrupo().getAprendices()) {
                usuarioAprendiz.add(ga.getUsuario());
            }

            aprendicesPorAsesoria.put(a.getId(), usuarioAprendiz);
            
            // Verificar si ya se registró asistencia para esta asesoría
            boolean tieneAsistencia = asistenciaServicio.existeAsistenciaRegistrada((long)a.getId());
            asistenciaRegistrada.put(a.getId(), tieneAsistencia);
        }

        model.addAttribute("usuarioAprendiz", aprendicesPorAsesoria);
        model.addAttribute("asistenciaRegistrada", asistenciaRegistrada);
        model.addAttribute("fechaActual", hoy);
        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);
        model.addAttribute("asesor", asesor);
        model.addAttribute("totalAsesorias", gruposTotales.size());
        model.addAttribute("asesoriasActivas", gruposActivos.size());
        model.addAttribute("asesoriasCompletadas", gruposCompletados.size());
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/asistencia";
    }

    @Autowired
    private GrupoAprendizRepositorio grupoAprendizRepositorio;

    @GetMapping("/asesor/aprendicesPorAsesoria/{id}")
    @ResponseBody
    public List<Map<String, Object>> obtenerAprendicesPorAsesoria(@PathVariable("id") Long idAsesoria) {
        List<GrupoAprendiz> grupoAprendices = grupoAprendizRepositorio
                .findByGrupo_Id(asesoriaRepositorio.findById(idAsesoria).get().getGrupo().getIdGrupo());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (GrupoAprendiz ga : grupoAprendices) {
            Usuario aprendiz = ga.getUsuario();
            if (aprendiz != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("idUsuario", aprendiz.getIdUsuario());
                map.put("nombreCompleto", aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA());
                resultado.add(map);
            }
        }

        return resultado;
    }

    @PostMapping("/asesor/registrarAsistencia")
    public String registrarAsistencias(
            @RequestParam("idAsesoria") Long idAsesoria,
            @RequestParam("idUsuarios") List<Long> idUsuarios,
            @RequestParam("asistencias") List<Boolean> asistencias,
            RedirectAttributes redirectAttributes
    ) {
        Asesoria asesoria = asesoriaServicio.obtenerPorId(idAsesoria);
        if (asesoria == null) {
            redirectAttributes.addFlashAttribute("error", "La asesoría no fue encontrada.");
            return "redirect:/asesor/asistencia";
        }

        if (idUsuarios.size() != asistencias.size()) {
            redirectAttributes.addFlashAttribute("error", "El número de aprendices y asistencias no coincide.");
            return "redirect:/asesor/asistencia";
        }
        
        // Verificar que la asistencia se registre el mismo día de la asesoría
        LocalDate fechaActual = LocalDate.now();
        if (!asesoria.getFecha().equals(fechaActual)) {
            redirectAttributes.addFlashAttribute("error", "La asistencia solo puede registrarse el mismo día de la asesoría.");
            return "redirect:/asesor/asistencia";
        }

        for (int i = 0; i < idUsuarios.size(); i++) {
            Long idUsuario = idUsuarios.get(i);
            Boolean asistio = asistencias.get(i);

            usuarioServicio.buscarPorIdUsuario(idUsuario).ifPresent(usuario -> {
                Asistencia asistencia = new Asistencia();
                asistencia.setAsesoria(asesoria);
                asistencia.setUsuario(usuario);
                asistencia.setFecha(LocalDate.now());
                asistencia.setAsistio(asistio);
                asistenciaServicio.guardar(asistencia);
            });
        }

        redirectAttributes.addFlashAttribute("success", "Asistencias registradas correctamente.");
        return "redirect:/asesor/asistencia";
    }

    //Vista grupos
    @GetMapping("/asesor/grupo")
    public String mostrarGruposDelAsesor(HttpSession session, Model model, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();

        // 1. Grupos asignados al asesor
        List<Grupo> gruposAsignados = grupoServicio.obtenerGruposPorAsesor(idAsesor);

        // 2. Obtener todas las asesorías activas
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // 3. Grupos disponibles (sin asesor y sin asesoría activa)
        List<Grupo> gruposSinAsesorNiAsesoria = grupoRepositorio.findGruposSinAsesor(); // debe traer grupos sin asesor asignado

        List<Grupo> gruposDisponibles = gruposSinAsesorNiAsesoria.stream()
                .filter(grupo -> !idsGruposConAsesoria.contains(grupo.getIdGrupo()))
                .collect(Collectors.toList());

        // 4. Agregar al modelo
        model.addAttribute("asesor", asesor);
        model.addAttribute("gruposAsignados", gruposAsignados);
        model.addAttribute("gruposDisponibles", gruposDisponibles);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/grupo";
    }

    @GetMapping("/asesor/grupos/disponibles")
    public String mostrarGruposDisponibles(
            @RequestParam(required = false) String filtro,
            HttpSession session,
            Model model, HttpServletRequest request) {

        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAprendiz = asesor.getIdUsuario();

        // Buscar solo los grupos que coincidan con el filtro
        List<Grupo> gruposFiltrados = grupoRepositorio.findGruposDisponiblesPorFiltro(filtro);

        // Asesorías activas (si la vista necesita marcar algo)
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // Enviar al modelo SOLO lo necesario
        model.addAttribute("asesor", asesor);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposFiltrados); // esto es lo único que te interesa
        model.addAttribute("filtro", filtro);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/grupo";
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/asesor/entrarGrupo")
    public String entrarGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        Long idAsesor = asesor.getIdUsuario();

        // Verificar límite de grupos activos para asesor (máximo 5)
        List<Grupo> gruposActivos = grupoServicio.obtenerGruposActivosPorAsesor(idAsesor);
        if (gruposActivos.size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "No puedes unirte a más de 5 grupos activos.");
            return "redirect:/asesor/grupos/disponibles";
        }
        
        // 1. Asignar asesor al grupo (procedimiento)
        jdbcTemplate.update("CALL asignar_asesor_a_grupo(?, ?)", idAsesor, idGrupo);

        // 2. Obtener grupo
        Grupo grupo = grupoRepositorio.findById(idGrupo.intValue()).orElse(null); // Ajusta tipo si usas Integer

        if (grupo != null) {
            // 3. Obtener aprendices del grupo
            List<GrupoAprendiz> grupoAprendices = grupoAprendizRepositorio.findByGrupo_Id(grupo.getIdGrupo());

            List<String> correosAprendices = new ArrayList<>();
            for (GrupoAprendiz ga : grupoAprendices) {
                Usuario aprendiz = ga.getUsuario();
                if (aprendiz != null && aprendiz.getCorreo() != null) {
                    correosAprendices.add(aprendiz.getCorreo());
                }
            }

            // 4. Enviar correo masivo
            if (!correosAprendices.isEmpty()) {
                String asunto = "Un asesor se ha unido a tu grupo";
                String cuerpo = "Hola, \n\n"
                        + "Te informamos que el asesor " + asesor.getNombres() + " "
                        + asesor.getPrimerA() + " " + asesor.getSegundoA()
                        + " se ha unido al grupo '" + grupo.getNombre() + "'.\n\n"
                        + "Gracias por usar KASH.";

                correoServicio.sendMassEmail(correosAprendices, asunto, cuerpo);
            }
            
            redirectAttributes.addFlashAttribute("mensaje", "Te has unido al grupo exitosamente.");
        }

        return "redirect:/asesor/grupo";
    }

    //Vista Componente
    @Autowired
    private AsesorComponenteServicio componenteServicio;

    @GetMapping("/asesor/componente")
    public String registrarComponentes(HttpSession session, Model model, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Obtener la lista de componentes disponibles
        List<String> componentesDisponibles = componenteServicio.obtenerNombresComponentesUnicos();

        // Obtener los componentes elegidos por el asesor
        List<Componente> componentesElegidos = componenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario());

        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("componentesElegidos", componentesElegidos);
        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());
        return "asesor/componente"; // Retorna la vista para registrar componentes
    }

    @PostMapping("/asesor/componente")
    public String registrarComponente(@RequestParam("componente") String componenteNombre, HttpSession session,
            Model model
    ) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Comprobar que el asesor no haya elegido ya 3 componentes
        if (componenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario()).size() >= 3) {
            model.addAttribute("mensaje", "No puedes registrar más de 3 componentes.");
            return "redirect:/asesor/componente";  // Redirige al formulario
        }

        // Crear y guardar el nuevo componente
        Componente nuevoComponente = new Componente();
        nuevoComponente.setNombre(componenteNombre);
        nuevoComponente.setUsuario(asesor);

        componenteServicio.guardarComponente(nuevoComponente);

        return "redirect:/asesor/componente";  // Redirige de nuevo para actualizar la lista
    }

    @PostMapping("/asesor/eliminarComponente")
    @Transactional
    public String eliminarComponente(@RequestParam("id") Long componenteId, HttpSession session,
            Model model
    ) {
        // Obtener el asesor de la sesión
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";  // Si no hay sesión, redirige al index
        }

        // Llamamos al servicio para eliminar la relación
        componenteServicio.eliminarComponente(componenteId, asesor.getIdUsuario());

        // Mostrar mensaje de éxito
        model.addAttribute("mensaje", "Componente eliminado exitosamente.");
        return "redirect:/asesor/componente";  // Redirige a la vista donde se muestran los componentes
    }

    // Vista Prueba
    @Autowired
    private AsesorPruebaServicio pruebaServicio;

    @Autowired
    private GrupoRepositorio grupoRepositorio;

// Mostrar formulario inicial para crear una nueva prueba
    @GetMapping("/asesor/prueba")
    public String asignarPruebas(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        if (asesor == null) {
            return "redirect:/index";
        }

        List<Grupo> gruposAsesor = grupoRepositorio.findGruposByAsesor(asesor);
        List<Prueba> todasLasPruebas = pruebaRepositorio.findAll();

        // Agrupar pruebas por id del grupo
        Map<Integer, List<Prueba>> pruebasPorGrupo = new HashMap<>();
        for (Prueba prueba : todasLasPruebas) {
            int idGrupo = prueba.getGrupo().getId(); // ID es int
            pruebasPorGrupo.computeIfAbsent(idGrupo, k -> new ArrayList<>()).add(prueba);
        }

        // Declarar el mapa principal una sola vez, fuera del bucle
        Map<Long, Map<Usuario, List<RespuestaSeleccionada>>> respuestasPorPrueba = new HashMap<>();

        for (Prueba prueba : todasLasPruebas) {
            // Este mapa es para agrupar las respuestas de esta prueba por aprendiz
            Map<Usuario, List<RespuestaSeleccionada>> mapa = new HashMap<>();

            // Obtener todas las respuestas de esta prueba
            List<RespuestaSeleccionada> todas = respuestaSeleccionadaRepositorio.findByPrueba(prueba);

            // Agrupar por aprendiz
            for (RespuestaSeleccionada r : todas) {
                Usuario aprendiz = r.getAprendiz();
                mapa.computeIfAbsent(aprendiz, k -> new ArrayList<>()).add(r);

                System.out.println("Aprendiz: " + aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA()
                        + " | Pregunta: " + r.getPregunta().getEnunciado()
                        + " | Respuesta: " + r.getRespuestaSeleccionada().getTexto());
            }

            // Asociar las respuestas agrupadas al ID de la prueba
            respuestasPorPrueba.put(prueba.getId(), mapa);
        }

// Agregar al modelo
        model.addAttribute("respuestasPorPrueba", respuestasPorPrueba);

        model.addAttribute("asesor", asesor);
        model.addAttribute("grupos", gruposAsesor);
        model.addAttribute("pruebasPorGrupo", pruebasPorGrupo);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/prueba";
    }

    @Autowired
    private PruebaRepositorio pruebaRepositorio;

    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    @Autowired
    private RespuestaSeleccionadaRepositorio respuestaSeleccionadaRepositorio;

    @GetMapping("/asesor/prueba/{pruebaId}/aprendiz/{aprendizId}/respuestas")
    public String verRespuestasAprendiz(@PathVariable Long pruebaId, @PathVariable Long aprendizId, Model model) {
        Prueba prueba = pruebaRepositorio.findById(pruebaId).orElse(null);
        Usuario aprendiz = usuarioRepositorio.findById(aprendizId).orElse(null);

        if (prueba == null || aprendiz == null) {
            return "redirect:/asesor/prueba";
        }

        List<RespuestaSeleccionada> respuestas = respuestaSeleccionadaRepositorio.findByAprendizAndPrueba(aprendiz, prueba);

        model.addAttribute("respuestas", respuestas);
        model.addAttribute("prueba", prueba);
        model.addAttribute("aprendiz", aprendiz);

        return "asesor/prueba"; // Volver a la vista principal de pruebas
    }

    @GetMapping("/asesor/prueba/{pruebaId}/aprendices")
    public String verAprendicesPorPrueba(@PathVariable Long pruebaId, Model model) {
        Prueba prueba = pruebaRepositorio.findById(pruebaId).orElse(null);

        if (prueba == null) {
            return "redirect:/asesor/prueba";
        }

        Grupo grupo = prueba.getGrupo(); // Ya asociado a la prueba

        // Obtener los aprendizajes desde grupoAprendiz
        List<GrupoAprendiz> relaciones = grupo.getAprendices(); // asegúrate que esta sea la lista en la entidad
        List<Usuario> aprendices = relaciones.stream()
                .map(GrupoAprendiz::getUsuario)
                .collect(Collectors.toList());

        model.addAttribute("prueba", prueba);
        model.addAttribute("aprendices", aprendices);

        return "asesor/prueba"; // Vista para mostrar aprendices
    }

    @PostMapping("/guardar")
    public String guardarPrueba(@ModelAttribute Prueba prueba, HttpServletRequest request, HttpSession session) {
        // Obtener asesor de la sesión
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        List<Pregunta> preguntas = new ArrayList<>();

        int totalPreguntas = Integer.parseInt(request.getParameter("totalPreguntas"));

        for (int i = 0; i < totalPreguntas; i++) {
            Pregunta pregunta = new Pregunta();
            pregunta.setEnunciado(request.getParameter("Pregunta_" + i));

            int respuestaCorrecta = Integer.parseInt(request.getParameter("Correcta_" + i));
            List<Respuesta> respuestas = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                Respuesta respuesta = new Respuesta();
                respuesta.setTexto(request.getParameter("respuesta_" + i + "_" + j));
                respuesta.setEsCorrecta(j == respuestaCorrecta);
                respuestas.add(respuesta);
            }

            pregunta.setRespuestas(respuestas);
            preguntas.add(pregunta);
        }

        // Guardar la prueba y sus preguntas
        pruebaServicio.guardarPrueba(prueba, preguntas);

        // Obtener grupo desde la prueba
        Grupo grupo = prueba.getGrupo();

        // Enviar correo a los aprendices del grupo
        if (grupo != null && grupo.getAprendices()!= null) {
            List<String> correosAprendices = new ArrayList<>();
            
            // Obtener correos de los aprendices
            for (GrupoAprendiz ga : grupo.getAprendices()) {
                Usuario aprendiz = ga.getUsuario();
                if (aprendiz != null && aprendiz.getCorreo() != null && !aprendiz.getCorreo().isEmpty()) {
                    correosAprendices.add(aprendiz.getCorreo());
                }
            }

            if (!correosAprendices.isEmpty()) {
                String asunto = "Nueva prueba asignada";
                String cuerpo = "Hola,\n\n"
                        + "El asesor " + asesor.getNombres() + " " + asesor.getPrimerA() + " " + asesor.getSegundoA()
                        + " ha creado una nueva prueba para el grupo '" + grupo.getNombre() + "'.\n\n"
                        + "Gracias por usar KASH.";

                correoServicio.sendMassEmail(correosAprendices, asunto, cuerpo);
            }
        }

        return "redirect:/asesor/prueba";
    }

    @GetMapping("/asesor/grupo/{idGrupo}/asesorias")
    @ResponseBody
    public Map<String, Object> obtenerAsesoriasGrupo(@PathVariable int idGrupo) {
        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        Map<String, Object> resultado = new HashMap<>();
        
        if (grupo == null) {
            resultado.put("nombreGrupo", "Grupo no encontrado");
            resultado.put("asesorias", Collections.emptyList());
            return resultado;
        }
        
        List<Asesoria> asesorias = asesoriaServicio.obtenerAsesoriasPorGrupo(grupo);
        
        // Convertir a formato JSON amigable
        List<Map<String, Object>> asesoriasJSON = new ArrayList<>();
        for (Asesoria asesoria : asesorias) {
            Map<String, Object> asesoriaMap = new HashMap<>();
            asesoriaMap.put("id", asesoria.getId());
            asesoriaMap.put("fechaFormateada", asesoria.getFechaFormateada());
            asesoriaMap.put("horaFormateada", asesoria.getHoraFormateada());
            asesoriaMap.put("estado", asesoria.getEstado());
            asesoriaMap.put("completada", asesoria.isCompletada());
            asesoriaMap.put("link", asesoria.getLink());
            asesoriasJSON.add(asesoriaMap);
        }
        
        resultado.put("nombreGrupo", grupo.getNombre());
        resultado.put("asesorias", asesoriasJSON);
        
        return resultado;
    }

    @PostMapping("/asesor/asesoria/{id}/editar")
    public String editarAsesoria(
            @PathVariable("id") int id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam("link") String link,
            RedirectAttributes redirect) {
        
        Optional<Asesoria> opt = asesoriaRepositorio.findById(id);
        if (opt.isPresent()) {
            Asesoria asesoria = opt.get();
            
            // Validar que la hora no esté en el rango de 10 PM a 6 AM
            LocalTime horaInicio = LocalTime.of(22, 0); // 10 PM
            LocalTime horaFin = LocalTime.of(6, 0); // 6 AM
            
            if ((hora.isAfter(horaInicio) || hora.equals(horaInicio)) || (hora.isBefore(horaFin) || hora.equals(horaFin))) {
                redirect.addFlashAttribute("error", "No se pueden programar asesorías entre las 10:00 PM y las 6:00 AM.");
                return "redirect:/asesor/asesorias";
            }
            
            // Verificar que no haya conflicto con otras asesorías
            if (asesoriaServicio.existeConflicto(asesoria.getGrupo(), fecha, hora)) {
                redirect.addFlashAttribute("error", "Ya existe otra asesoría programada en esa fecha y hora.");
                return "redirect:/asesor/asesorias";
            }
            
            // Actualizar solo esta asesoría específica
            asesoria.setFecha(fecha);
            asesoria.setHora(hora);
            asesoria.setLink(link);
            asesoriaRepositorio.save(asesoria);
            
            redirect.addFlashAttribute("mensaje", "Asesoría actualizada correctamente.");
        } else {
            redirect.addFlashAttribute("error", "No se encontró la asesoría.");
        }
        return "redirect:/asesor/asesorias";
    }

}
