package com.proyecto.KASH.servicio;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

@Service
public class UsuarioServicioImpl2 implements UsuarioServicio2 {

    @Autowired
    private Usuario2Repositorio Repositorio;

    @Override
    public Optional<Usuario> findByUsuarioAndPass(String usuario, String pass) {
        return Repositorio.findByUsuarioAndPass(usuario, pass);
    }

    @Override
    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) throws Exception {
        Document documento = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(documento, out);
        documento.open();

        // HEADER en color #03314b con logo, texto "KASH" y "Usuarios"
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4, 2});

        // Logo (ajusta la ruta según corresponda)
        Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
        logo.scaleToFit(50, 50);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(new BaseColor(3, 49, 75));
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(logoCell);

        // Texto "KASH"
        Font fontKash = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, BaseColor.WHITE);
        PdfPCell kashCell = new PdfPCell(new Phrase("KASH", fontKash));
        kashCell.setBorder(Rectangle.NO_BORDER);
        kashCell.setBackgroundColor(new BaseColor(3, 49, 75));
        kashCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        kashCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(kashCell);

        // Texto "Usuarios"
        PdfPCell usuariosCell = new PdfPCell(new Phrase("Usuarios", fontKash));
        usuariosCell.setBorder(Rectangle.NO_BORDER);
        usuariosCell.setBackgroundColor(new BaseColor(3, 49, 75));
        usuariosCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        usuariosCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(usuariosCell);

        documento.add(headerTable);
        documento.add(new Paragraph(" ")); // espacio

        // Tabla de usuarios
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new int[]{2, 2, 2, 2, 3, 2});

        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);
        BaseColor azul = new BaseColor(3, 49, 75);

        Stream.of("Documento", "Rol", "Nombres", "Primer Apellido", "Correo", "Usuario")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                    cell.setBackgroundColor(azul);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                });

        for (Usuario u : usuarios) {
            tabla.addCell(String.valueOf(u.getIdUsuario()));
            tabla.addCell(u.getRolSeleccionado());
            tabla.addCell(u.getNombres());
            tabla.addCell(u.getPrimerA());
            tabla.addCell(u.getCorreo());
            tabla.addCell(u.getUsuario());
        }

        documento.add(tabla);
        documento.add(new Paragraph(" ")); // espacio

        // Gráfica de pastel (porcentajes)
        int total = usuarios.size();
        long aprendices = usuarios.stream().filter(u -> u.getRolSeleccionado().equalsIgnoreCase("aprendiz")).count();
        long asesores = usuarios.stream().filter(u -> u.getRolSeleccionado().equalsIgnoreCase("asesor")).count();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (aprendices > 0) {
            dataset.setValue("Aprendices", (double) aprendices / total * 100);
        }
        if (asesores > 0) {
            dataset.setValue("Asesores", (double) asesores / total * 100);
        }

        JFreeChart chart = ChartFactory.createPieChart("Distribución de Roles (%)", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 12));
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}%"));
        plot.setSectionPaint("Aprendices", Color.BLUE);
        plot.setSectionPaint("Asesores", Color.GREEN);

        // Convertir la gráfica a imagen PDF
        BufferedImage chartImage = chart.createBufferedImage(500, 300);
        Image img = Image.getInstance(writer, chartImage, 1.0f);
        img.setAlignment(Element.ALIGN_CENTER);
        documento.add(img);

        documento.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return Repositorio.findByIdUsuario(id).orElse(null);
    }

    @Override
    public ByteArrayInputStream generarReporteUsuarioIndividual(Usuario usuario) throws Exception {
        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(documento, out);
        documento.open();

        // Fuente personalizada
        Font fuenteTitulo = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD, BaseColor.WHITE);
        Font fuenteTexto = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
        Font fuenteCeldaAzul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);

        // Header azul con logo y título
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new int[]{1, 4});
        header.setSpacingAfter(20);

        // Logo
        Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png"); // Ajusta ruta si hace falta
        logo.scaleToFit(60, 60);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(logoCell);

        // Título KASH
        PdfPCell tituloCell = new PdfPCell(new Phrase("KASH\nREPORTE INDIVIDUAL DE USUARIO", fuenteTitulo));
        tituloCell.setBorder(Rectangle.NO_BORDER);
        tituloCell.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
        tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tituloCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tituloCell.setPaddingLeft(10f);
        header.addCell(tituloCell);

        documento.add(header);

        // Tabla de datos
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setWidths(new float[]{3, 7});

        // Método auxiliar para crear celdas
        BiConsumer<String, String> agregarFila = (titulo, valor) -> {
            PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteCeldaAzul));
            celdaTitulo.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
            celdaTitulo.setPadding(8);
            celdaTitulo.setBorderWidth(1);
            tabla.addCell(celdaTitulo);

            PdfPCell celdaValor = new PdfPCell(new Phrase(valor != null ? valor : "-", fuenteTexto));
            celdaValor.setPadding(8);
            celdaValor.setBorderWidth(1);
            tabla.addCell(celdaValor);
        };

        agregarFila.accept("Documento:", String.valueOf(usuario.getIdUsuario()));
        agregarFila.accept("Nombre:", usuario.getNombres());
        agregarFila.accept("Primer Apellido:", usuario.getPrimerA());
        agregarFila.accept("Segundo Apellido:", usuario.getSegundoA());
        agregarFila.accept("Correo:", usuario.getCorreo());
        agregarFila.accept("Número:", usuario.getNumero());
        agregarFila.accept("Usuario:", usuario.getUsuario());
        agregarFila.accept("Contraseña:", usuario.getPass());
        agregarFila.accept("Tipo de Documento:", usuario.getTDocumento());
        agregarFila.accept("Fecha de Nacimiento:", String.valueOf(usuario.getFNacimiento()));
        agregarFila.accept("Trimestre:", String.valueOf(usuario.getTrimestre()));
        agregarFila.accept("Rol:", usuario.getRolSeleccionado());

        documento.add(tabla);
        documento.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
