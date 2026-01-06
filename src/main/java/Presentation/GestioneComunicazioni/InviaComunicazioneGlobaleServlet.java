package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet(name = "InviaComunicazioneGlobaleServlet", urlPatterns = {"/InviaComunicazioneGlobaleServlet"})
public class InviaComunicazioneGlobaleServlet extends HttpServlet {

    // 1. Service come campo di istanza
    private ComunicazioneService service = new ComunicazioneService();

    // 2. Setter per i Test (Dependency Injection)
    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Sicurezza: Solo l'ADMIN può inviare comunicazioni globali
        if (utente == null || !utente.isAdmin()) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero parametri dal form (titolo, contenuto, eventuale foto)
        String titolo = request.getParameter("titolo");
        String contenuto = request.getParameter("contenuto");
        String foto = request.getParameter("foto"); //qui assumo sia una stringa/url

        // Validazione minima
        if (titolo == null || titolo.trim().isEmpty() || contenuto == null || contenuto.trim().isEmpty()) {
            request.setAttribute("errore", "Titolo e contenuto sono obbligatori.");
            request.getRequestDispatcher("formComunicazioneGlobale.jsp").forward(request, response);
            return;
        }

        try {
            // Creazione del Bean
            ComunicazioniBean nuovaComunicazione = new ComunicazioniBean();
            nuovaComunicazione.setTitolo(titolo);
            nuovaComunicazione.setContenuto(contenuto);
            nuovaComunicazione.setFoto(foto);

            // Impostiamo l'autore (l'admin loggato)
            nuovaComunicazione.setId_autore(utente.getId_utente());

            // IMPOSTAZIONE FONDAMENTALE PER "GLOBALE"
            nuovaComunicazione.setIsglobal(true);
            nuovaComunicazione.setId_gruppo(-1); // -1 indica "Nessun Gruppo specifico" (Globale)

            // Impostiamo la data corrente
            nuovaComunicazione.setDataPubblicazione(LocalDateTime.now());

            // 3. Uso del service iniettato (invece di 'new')
            service.creaComunicazione(nuovaComunicazione);

            // Successo
            response.sendRedirect("VisualizzaComunicazioniGlobaliServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel salvataggio della comunicazione.");
            request.getRequestDispatcher("formComunicazioneGlobale.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}