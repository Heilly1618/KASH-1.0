package com.proyecto.KASH.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.foro;
import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReporteForosControlador {

    @Autowired
    private foroRepositorio foroRepositorio;

    @GetMapping("/report-foros")
    public ResponseEntity<byte[]> generarReporteForos(HttpServletResponse response) {
        try {
            List<foro> foros = foroRepositorio.findAll();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // ======= HEADER CON LOGO Y TÍTULO ========
            PdfPTable header = new PdfPTable(3);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{1f, 3f, 2f});

            BaseColor azul = new BaseColor(3, 49, 75);

            // LOGO (actualiza la ruta si es necesario)
            Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
            logo.scaleToFit(50, 50);
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setBackgroundColor(azul);
            logoCell.setPadding(5);
            header.addCell(logoCell);

            // TÍTULO CENTRADO
            PdfPCell titulo = new PdfPCell(new Phrase("KASH", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE)));
            titulo.setBorder(Rectangle.NO_BORDER);
            titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            titulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titulo.setBackgroundColor(azul);
            header.addCell(titulo);

            // SUBTÍTULO A LA DERECHA
            PdfPCell sub = new PdfPCell(new Phrase("FOROS", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE)));
            sub.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sub.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sub.setBorder(Rectangle.NO_BORDER);
            sub.setBackgroundColor(azul);
            sub.setPaddingRight(10);
            header.addCell(sub);

            doc.add(header);
            doc.add(new Paragraph("\n"));

            // ========== TABLA DE FOROS CON CONTENIDO ==========
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 1.8f, 1.8f, 1.8f, 1.5f, 3f});

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            String[] headers = {"ID", "Nombre", "Tema", "Fecha", "Usuario", "Contenido"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setBackgroundColor(azul);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tabla.addCell(cell);
            }

            for (foro f : foros) {
                tabla.addCell(String.valueOf(f.getId()));
                tabla.addCell(f.getNombre());
                tabla.addCell(f.getTema());
                tabla.addCell(String.valueOf(f.getFecha()));
                tabla.addCell(String.valueOf(f.getIdUsuario()));

                PdfPCell contenidoCell = new PdfPCell(new Phrase(f.getContenido()));
                contenidoCell.setPadding(5);
                tabla.addCell(contenidoCell);
            }

            doc.add(tabla);
            doc.add(new Paragraph("\n"));

            // ========== GRÁFICO DE TEMAS CON PORCENTAJE ==========
            Map<String, Integer> conteo = new HashMap<>();
            int total = foros.size();

            for (foro f : foros) {
                conteo.put(f.getTema(), conteo.getOrDefault(f.getTema(), 0) + 1);
            }

            DefaultPieDataset dataset = new DefaultPieDataset();
            for (Map.Entry<String, Integer> e : conteo.entrySet()) {
                double porcentaje = (e.getValue() * 100.0) / total;
                String etiqueta = e.getKey() + String.format(" (%.1f%%)", porcentaje);
                dataset.setValue(etiqueta, e.getValue());
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "Porcentaje de Foros por Tema",
                    dataset,
                    true, true, false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(Color.WHITE);
            plot.setSectionOutlinesVisible(false);
            plot.setSimpleLabels(true);

            ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(chartBaos, chart, 500, 300);
            Image grafico = Image.getInstance(chartBaos.toByteArray());
            grafico.setAlignment(Element.ALIGN_CENTER);
            doc.add(grafico);

            doc.close();

            byte[] pdfBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=reporte_foros.pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
