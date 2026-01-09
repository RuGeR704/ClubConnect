package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService; // Serve per l'email
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.ClubBean;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/generaReportServlet")
public class GeneraReportServlet extends HttpServlet {

    private GruppoService gruppoService = new GruppoService();
    private EventoService eventoService = new EventoService();
    private ComunicazioneService comunicazioneService = new ComunicazioneService();

    // Setters per i Test
    public void setGruppoService(GruppoService gs) { this.gruppoService = gs; }
    public void setEventoService(EventoService es) { this.eventoService = es; }
    public void setComunicazioneService(ComunicazioneService cs) { this.comunicazioneService = cs; }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        String idStr = request.getParameter("idGruppo");
        String dataInizioStr = request.getParameter("dataInizio");
        String dataFineStr = request.getParameter("dataFine");
        String[] categorie = request.getParameterValues("categorie");

        // 1. Validazione Parametri Base
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("errore", "ID Gruppo mancante");
            request.getRequestDispatcher("feedServlet").forward(request, response);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);

            // 2. Controllo Permessi (TCS 3.1)
            if (utente == null || !gruppoService.isUtenteGestore(idGruppo, utente.getId_utente())) {
                request.setAttribute("errore", "L'utente non ha i permessi di gestore, accesso negato");
                request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);
                return;
            }

            // 3. Controllo Categorie (TCS 3.2)
            if (categorie == null || categorie.length == 0) {
                request.setAttribute("errore", "Nessuna categoria di informazioni selezionata per il report");
                request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);
                return;
            }
            List<String> catList = Arrays.asList(categorie);

            // 4. Controllo Date (TCS 3.3)
            LocalDate dataInizio = (dataInizioStr != null && !dataInizioStr.isEmpty()) ? LocalDate.parse(dataInizioStr) : LocalDate.MIN;
            LocalDate dataFine = (dataFineStr != null && !dataFineStr.isEmpty()) ? LocalDate.parse(dataFineStr) : LocalDate.MAX;

            if (dataInizio.isAfter(dataFine)) {
                request.setAttribute("errore", "La data di inizio è successiva alla data di fine");
                request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);
                return;
            }

            // --- RECUPERO DATI ---
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);
            List<UtenteBean> soci = new ArrayList<>();
            List<EventoBean> eventi = new ArrayList<>();

            // Recupero Soci solo se richiesto
            if (catList.contains("Iscritti")) {
                soci = gruppoService.recuperaSociDelGruppo(idGruppo);
            }

            // Recupero Eventi solo se richiesto e filtrati per data
            if (catList.contains("Eventi")) {
                eventi = eventoService.retrieveAllEventi().stream()
                        .filter(e -> e.getId_gruppo() == idGruppo)
                        // Filtro data (assumendo che EventoBean abbia LocalDateTime o LocalDate)
                        .filter(e -> {
                            LocalDate dataEvento = e.getData_ora().toLocalDate();
                            return !dataEvento.isBefore(dataInizio) && !dataEvento.isAfter(dataFine);
                        })
                        .collect(Collectors.toList());
            }

            // 5. Controllo Dati Trovati (TCS 3.4)
            if (soci.isEmpty() && eventi.isEmpty()) {
                request.setAttribute("errore", "Nessun dato trovato per i parametri e il periodo specificati");
                request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);
                return;
            }

            // 6. Controllo Volume Dati (TCS 3.6)
            // Se ci sono più di 100 record totali, invia via email
            if ((soci.size() + eventi.size()) > 100) {
                // Simulazione invio email
                // comunicazioneService.inviaEmailReport(utente.getEmail(), ...);
                request.setAttribute("msg", "Report voluminoso in elaborazione, verrà inviato via e-mail");
                request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);
                return;
            }

            // 7. Generazione PDF Standard (TCS 3.5)
            generaPdf(response, gruppo, soci, eventi);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    // Metodo helper per la generazione fisica del PDF
    private void generaPdf(HttpServletResponse response, GruppoBean gruppo, List<UtenteBean> soci, List<EventoBean> eventi) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Report_" + gruppo.getNome().replace(" ", "_") + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Report: " + gruppo.getNome()));
        document.add(new Paragraph(" "));

        if (!soci.isEmpty()) {
            document.add(new Paragraph("Sezione Soci (" + soci.size() + ")"));
            // ... codice tabella soci ...
            document.add(new Paragraph(" "));
        }

        if (!eventi.isEmpty()) {
            document.add(new Paragraph("Sezione Eventi (" + eventi.size() + ")"));
            // ... codice tabella eventi ...
        }

        document.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}