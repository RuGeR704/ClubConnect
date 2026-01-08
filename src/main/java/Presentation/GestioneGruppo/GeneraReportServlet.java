package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/generaReportServlet")
public class GeneraReportServlet extends HttpServlet {

    // 1. Dependency Injection
    private GruppoService gruppoService = new GruppoService();
    private EventoService eventoService = new EventoService();

    // 2. Setters per i Test
    public void setGruppoService(GruppoService gs) { this.gruppoService = gs; }
    public void setEventoService(EventoService es) { this.eventoService = es; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("idGruppo");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(400, "ID Gruppo mancante");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);

            // 3. Recupero Dati tramite SERVICE (Niente più DAO/ConPool qui)
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);
            List<UtenteBean> soci = gruppoService.recuperaSociDelGruppo(idGruppo);

            // Recupero eventi e filtro per gruppo
            List<EventoBean> eventi = eventoService.retrieveAllEventi().stream()
                    .filter(e -> e.getId_gruppo() == idGruppo)
                    .collect(Collectors.toList());

            // --- GENERAZIONE PDF (Logica invariata, usa i dati recuperati sopra) ---
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Report_" + gruppo.getNome().replace(" ", "_") + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            Paragraph titolo = new Paragraph("Report Attività Gruppo", titleFont);
            titolo.setAlignment(Element.ALIGN_CENTER);
            document.add(titolo);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Nome Gruppo: " + gruppo.getNome(), subTitleFont));
            document.add(new Paragraph("Settore: " + gruppo.getSettore(), normalFont));
            document.add(new Paragraph("Totale Soci: " + (soci != null ? soci.size() : 0), normalFont));
            document.add(new Paragraph("-----------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            // Tabella Eventi
            document.add(new Paragraph("Lista Eventi Organizzati", subTitleFont));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            addTableHeader(table, "Data");
            addTableHeader(table, "Evento");

            if (eventi != null && !eventi.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (EventoBean ev : eventi) {
                    table.addCell(ev.getData_ora() != null ? ev.getData_ora().format(formatter) : "N/D");
                    table.addCell(ev.getNome());
                }
            } else {
                PdfPCell cell = new PdfPCell(new Phrase("Nessun evento registrato"));
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            document.add(table);

            // Tabella Soci
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Lista Soci Iscritti", subTitleFont));
            document.add(new Paragraph(" "));
            PdfPTable tableSoci = new PdfPTable(3);
            tableSoci.setWidthPercentage(100);
            addTableHeader(tableSoci, "Nome");
            addTableHeader(tableSoci, "Cognome");
            addTableHeader(tableSoci, "Email");

            if (soci != null && !soci.isEmpty()) {
                for (UtenteBean socio : soci) {
                    tableSoci.addCell(socio.getNome());
                    tableSoci.addCell(socio.getCognome());
                    tableSoci.addCell(socio.getEmail());
                }
            } else {
                PdfPCell cell = new PdfPCell(new Phrase("Nessun socio iscritto"));
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableSoci.addCell(cell);
            }
            document.add(tableSoci);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Errore generazione PDF");
        }
    }

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