package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/CreaGruppoServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class CreaGruppoServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "gruppi";
    private GruppoService service = new GruppoService();

    public void setGruppoService(GruppoService service) {
        this.service = service;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 1. Recupero Parametri (Senza Tag)
        String nome = request.getParameter("nome");
        String settore = request.getParameter("settore");
        String slogan = request.getParameter("slogan");
        String descrizione = request.getParameter("descrizione");
        String sede = request.getParameter("sede");
        String regole = request.getParameter("regole");
        String tipoGruppoStr = request.getParameter("tipoGruppo");
        String importoStr = request.getParameter("importo");
        String frequenzaStr = request.getParameter("frequenza");

        String errore = null;

        // --- 2. VALIDAZIONE (Senza Tag) ---

        // TC2.1_1: Nome vuoto
        if (nome == null || nome.trim().isEmpty()) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè il campo nome è vuoto";
        }
        // TC2.3_1: Descrizione vuota
        else if (descrizione == null || descrizione.trim().isEmpty()) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè il campo descrizione è vuoto";
        }
        // TC2.3_2: Descrizione troppo lunga
        else if (descrizione.length() > 100) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè la descrizione è troppo lunga";
        }
        // TC2.4_1: Tipo vuoto
        else if (tipoGruppoStr == null || tipoGruppoStr.trim().isEmpty()) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè il campo tipo è vuoto";
        }
        // TC2.5: Settore vuoto
        else if (settore == null || settore.trim().isEmpty()) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè il campo settore è vuoto";
        }
        // TC2.6: Slogan troppo lungo
        else if (slogan != null && slogan.length() > 50) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè lo slogan è troppo lungo";
        }
        // TC2.7: Indirizzo Sede formato errato
        else if (sede != null && !sede.matches("^[a-zA-Z\\s]+ \\d+, [a-zA-Z\\s]+ \\([A-Z]{2}\\)$")) {
            errore = "Errore: La creazione del gruppo non è andata a buon fine poichè l'indirizzo non rispetta il formato";
        }

        // Controllo specifico per tipo CLUB
        if (errore == null && "Club".equalsIgnoreCase(tipoGruppoStr)) {
            // TC2.8_1: Retta senza importo
            if (importoStr == null || importoStr.trim().isEmpty()) {
                errore = "Errore: La creazione del gruppo non è andata a buon fine poichè la retta deve necessariamente avere un importo";
            }
            // TC2.8_2: Retta senza frequenza
            else if (frequenzaStr == null || frequenzaStr.trim().isEmpty()) {
                errore = "Errore: La creazione del gruppo non è andata a buon fine poichè la rette deve necessariamente avere informazioni sulla frequenza del pagamento";
            }
        }

        // --- 3. GESTIONE ERRORE ---
        if (errore != null) {
            request.setAttribute("errore", errore);
            request.getRequestDispatcher("crea_gruppo.jsp").forward(request, response);
            return;
        }

        // --- 4. LOGICA UPLOAD FILE ---
        String logoPath = null;
        try {
            Part part = request.getPart("logo");
            if (part != null && part.getSize() > 1024 * 1024 * 10) {
                request.setAttribute("errore", "Errore: La creazione del gruppo non è andata a buon fine poichè il file immagine del logo è troppo grande");
                request.getRequestDispatcher("crea_gruppo.jsp").forward(request, response);
                return;
            }
            if (part != null && part.getSize() > 0) {
                String applicationPath = getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadFilePath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                part.write(uploadFilePath + File.separator + fileName);
                logoPath = "./" + UPLOAD_DIR.replace(File.separator, "/") + "/" + fileName;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // --- 5. CREAZIONE BEAN ---
        GruppoBean gruppo;
        if ("Club".equalsIgnoreCase(tipoGruppoStr)) {
            ClubBean club = new ClubBean();
            club.setImporto_retta(Double.parseDouble(importoStr));
            club.setFrequenza(Integer.parseInt(frequenzaStr));
            club.setTipoGruppo(true);
            gruppo = club;
        } else {
            AssociazioneBean associazione = new AssociazioneBean();
            associazione.setTipoGruppo(false);
            gruppo = associazione;
        }

        gruppo.setNome(nome);
        gruppo.setSettore(settore);
        gruppo.setSlogan(slogan);
        gruppo.setDescrizione(descrizione);
        gruppo.setSede(sede);
        gruppo.setRegole(regole);
        gruppo.setLogo(logoPath);
        gruppo.setStato(true);

        try {
            service.creaGruppo(gruppo, utente.getId_utente());
            response.sendRedirect("feedServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}