package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/CreaEventoServlet")
public class CreaEventoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // --- 1. PREPARAZIONE DELL'ENTITY (SCATOLA DATI) ---
        EventoBean evento = new EventoBean();

        // Recupero parametri e setto i valori usando i TUOI nomi esatti
        evento.setNome(request.getParameter("nome"));
        evento.setDescrizione(request.getParameter("descrizione"));
        evento.setFoto(request.getParameter("foto"));

        // Gestione dei numeri con parsing
        try {
            evento.setCosto(Double.parseDouble(request.getParameter("costo")));
            evento.setPosti_disponibili(Integer.parseInt(request.getParameter("posti_disponibili"))); // Nota il nome con _
            evento.setCapienza_massima(Integer.parseInt(request.getParameter("capienza_massima")));   // Nota il nome con _

            // L'id_gruppo dovrebbe arrivare dalla sessione (es. gestore loggato) o da un campo hidden
            // Per ora lo prendiamo dai parametri come esempio:
            String idGruppoStr = request.getParameter("id_gruppo");
            if(idGruppoStr != null) {
                evento.setId_gruppo(Integer.parseInt(idGruppoStr));
            }

            // Gestione Data (assumendo input type="datetime-local" che restituisce "yyyy-MM-ddTHH:mm")
            String dataStr = request.getParameter("data_ora");
            if(dataStr != null && !dataStr.isEmpty()) {
                evento.setData_ora(LocalDateTime.parse(dataStr));
            }

        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dati numerici o data non validi");
            return;
        }

        // --- 2. CHIAMATA AL SERVICE (LOGICA) ---
        GestioneEventiBean service = new GestioneEventiBean();

        try {
            service.creaEvento(evento);
            // Successo
            response.sendRedirect("visualizzaCalendario.jsp?msg=success");
        } catch (Exception e) {
            e.printStackTrace();

            // Errore
            request.setAttribute("errore", "Errore server: " + e.getMessage());
            request.getRequestDispatcher("creaEvento.jsp").forward(request, response);
        }
    }
}