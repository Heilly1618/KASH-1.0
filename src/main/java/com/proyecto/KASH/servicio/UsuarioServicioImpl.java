package com.proyecto.KASH.servicio;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private Usuario2Repositorio Repositorio;

    @Override
    public List<Usuario> ListarUsuario() {
        List<Usuario> usuarios = Repositorio.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios en la base de datos");
        }
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarPorIdUsuario(Long idUsuario) {
        Optional<Usuario> usuario = Repositorio.findById(idUsuario);
        if (usuario.isPresent()) {
            return usuario;  // Usuario encontrado
        } else {
            // En caso de que no se encuentre el usuario, puedes hacer algo aquí
            System.out.println("Usuario no encontrado con ID: " + idUsuario);
            return Optional.empty();  // Usuario no encontrado
        }
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        if (Repositorio.existsById(idUsuario)) {
            Repositorio.deleteById(idUsuario);
            System.out.println("Usuario con ID " + idUsuario + " eliminado exitosamente.");
        } else {
            System.out.println("Usuario con ID " + idUsuario + " no encontrado.");
        }
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    public List<Usuario> buscarCoordinador() {
        return usuarioRepositorio.findByRolSeleccionado("coordinador");
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepositorio.findAll();// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void cambiarContrasena(String correo, String nuevaContrasena) {
        usuarioRepositorio.findByCorreo(correo).ifPresent(usuario -> {
            usuario.setPass(nuevaContrasena); // Guarda la contraseña sin encriptar
            usuarioRepositorio.save(usuario);
        });
    }

    @Override
    public Optional<Usuario> buscarPorRol(String rol) {
        return usuarioRepositorio.findByRolSeleccionado(rol).stream().findFirst();
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return Repositorio.findByCorreo(correo);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo);
    }

    public ByteArrayInputStream generarPdfPersonalizado(List<Usuario> usuarios, List<String> campos) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph title = new Paragraph("Reporte de Usuarios Personalizado", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(campos.size());
        table.setWidthPercentage(100);

        // Encabezados
        for (String campo : campos) {
            PdfPCell header = new PdfPCell(new Phrase(nombreBonitoCampo(campo)));
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(header);
        }

        // Contenido
        for (Usuario u : usuarios) {
            for (String campo : campos) {
                String valor = obtenerValorCampo(u, campo);
                table.addCell(valor != null ? valor : "");
            }
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public String nombreBonitoCampo(String campo) {
        switch (campo) {
            case "idUsuario":
                return "Número de Documento";
            case "rolSeleccionado":
                return "Rol";
            case "nombres":
                return "Nombres";
            case "primerA":
                return "Primer Apellido";
            case "segundoA":
                return "Segundo Apellido";
            case "fNacimiento":
                return "Fecha de Nacimiento";
            case "tDocumento":
                return "Tipo de Documento";
            case "trimestre":
                return "Trimestre";
            case "correo":
                return "Correo";
            case "numero":
                return "Número";
            case "usuario":
                return "Usuario";
            default:
                return campo;
        }
    }

    public String obtenerValorCampo(Usuario u, String campo) {
        switch (campo) {
            case "idUsuario":
                return String.valueOf(u.getIdUsuario());
            case "rolSeleccionado":
                return u.getRolSeleccionado();
            case "nombres":
                return u.getNombres();
            case "primerA":
                return u.getPrimerA();
            case "segundoA":
                return u.getSegundoA();
            case "fNacimiento":
                return u.getFNacimiento() != null ? u.getFNacimiento().toString() : "";
            case "tDocumento":
                return u.getTDocumento();
            case "trimestre":
                return u.getTrimestre();
            case "correo":
                return u.getCorreo();
            case "numero":
                return u.getNumero();
            case "usuario":
                return u.getUsuario();
            default:
                return "";
        }
    }

}
