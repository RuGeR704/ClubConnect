package Presentation.GestioneGruppo;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import Application.GestioneEventi.EventoBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.EventoDAO;
import Storage.GruppoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itextpdf.text.Element.ALIGN_CENTER;

@WebServlet("/generaReportServlet")
public class GeneraReportServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Recupero ID Gruppo
        String idStr = request.getParameter("idGruppo");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(400, "ID Gruppo mancante");
            return;
        }
        int idGruppo = Integer.parseInt(idStr);

        try (Connection con = ConPool.getConnection()) {

            // 2. Recupero Dati dal DB
            GruppoDAO gruppoDAO = new GruppoDAO();
            GruppoBean gruppo = gruppoDAO.doRetrieveByid(con, idGruppo);

            EventoDAO eventoDAO = new EventoDAO();
            List<EventoBean> eventi = eventoDAO.doRetrievebyGroup(con, idGruppo);

            // 3. Impostazione Response per PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Report_" + gruppo.getNome().replace(" ", "_") + ".pdf");

            // 4. Creazione Documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // --- STILE FONT ---
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // --- HEADER REPORT ---
            Paragraph titolo = new Paragraph("Report Attività Gruppo", titleFont);
            titolo.setAlignment(Element.ALIGN_CENTER);
            document.add(titolo);
            document.add(new Paragraph(" ")); // Spazio vuoto

            // --- INFO GRUPPO ---
            document.add(new Paragraph("Nome Gruppo: " + gruppo.getNome(), subTitleFont));
            document.add(new Paragraph("Settore: " + gruppo.getSettore(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("-----------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            // --- TABELLA EVENTI ---
            document.add(new Paragraph("Lista Eventi Organizzati", subTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Intestazione Tabella
            addTableHeader(table, "Data");
            addTableHeader(table, "Evento");

            // Dati Tabella
            if (eventi != null && !eventi.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (EventoBean ev : eventi) {
                    table.addCell(ev.getData_ora() != null ? ev.getData_ora().format(formatter) : "N/D");
                    table.addCell(ev.getNome());
                }
            } else {
                PdfPCell cell = new PdfPCell(new Phrase("Nessun evento registrato"));
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            document.add(table);

            // --- FOOTER ---
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Generato da ClubConnect il " + java.time.LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Errore generazione PDF: " + e.getMessage());
        }
    }

    // Metodo helper per le celle header
    private void addTableHeader(PdfPTable table, String title) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        header.setPadding(5);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
