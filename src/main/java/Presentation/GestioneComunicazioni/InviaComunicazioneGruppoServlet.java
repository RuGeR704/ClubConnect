package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.GestioneComunicazioniBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@WebServlet(name = "InviaComunicazioneGruppoServlet", urlPatterns = {"/InviaComunicazioneGruppoServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class InviaComunicazioneGruppoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        // Controllo Login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        String titolo = request.getParameter("titolo");
        String contenuto = request.getParameter("contenuto");
        String foto = request.getParameter("foto");

        // Validazione base
        if (idGruppoStr == null || idGruppoStr.isEmpty() || titolo == null || titolo.trim().isEmpty()) {
            request.setAttribute("errore", "Dati mancanti (ID Gruppo o Titolo).");
            // In caso di errore, si torna alla pagina del gruppo
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppoStr);
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            Part filePart = request.getPart("foto"); // Recupera il file
            String nomeFoto = "";

            if (filePart != null && filePart.getSize() > 0) {
                // Definizione cartella di salvataggio (es. /images/uploads/)
                String uploadPath = getServletContext().getRealPath("") + File.separator + "images" + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                // Genera nome file unico per evitare sovrascritture
                String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();

                // Salva il file
                filePart.write(uploadPath + File.separator + fileName);

                // Salva il percorso relativo per il DB
                nomeFoto = "images/uploads/" + fileName;
            }

            // Creazione Bean
            ComunicazioniBean nuovaComunicazione = new ComunicazioniBean();
            nuovaComunicazione.setTitolo(titolo);
            nuovaComunicazione.setContenuto(contenuto);
            nuovaComunicazione.setFoto(nomeFoto);
            nuovaComunicazione.setId_autore(utente.getId_utente());

            // IMPOSTAZIONE PER GRUPPO
            nuovaComunicazione.setId_gruppo(idGruppo);
            nuovaComunicazione.setIsglobal(false); // È una news privata del gruppo

            nuovaComunicazione.setDataPubblicazione(LocalDateTime.now());

            // Salvataggio
            GestioneComunicazioniBean service = new GestioneComunicazioniBean();
            service.creaComunicazione(nuovaComunicazione);

            // Redirect
            // Torniamo alla pagina che visualizza le news di quel gruppo specifico
            response.sendRedirect("VisualizzaComunicazioniGruppoServlet?idGruppo=" + idGruppo);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("feedServlet"); // ID Gruppo non valido
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel salvataggio della comunicazione.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("feedServlet");
    }
}