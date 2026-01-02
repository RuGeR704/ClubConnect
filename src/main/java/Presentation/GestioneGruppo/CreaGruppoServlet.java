package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.UtenteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/CreazioneGruppoServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB: se supera questa dim., scrive su disco temporaneo
        maxFileSize = 1024 * 1024 * 10,      // 10MB: dimensione massima per singolo file
        maxRequestSize = 1024 * 1024 * 50    // 50MB: dimensione massima totale della richiesta
)
public class CreaGruppoServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "gruppi";

    @Override
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String nome = request.getParameter("nome");
        String settore = request.getParameter("settore");
        String slogan = request.getParameter("slogan");
        String descrizione = request.getParameter("descrizione");
        String sede = request.getParameter("sede");
        String regole = request.getParameter("regole");
        String tipoGruppoStr = request.getParameter("tipoGruppo");

        String logoPath = null;
        try {
            Part part = request.getPart("logo");
            if (part != null && part.getSize() > 0) {
                // Percorso assoluto dove salvare (nella cartella del server)
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                // Crea la cartella se non esiste
                File uploadDir = new File(uploadFilePath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = part.getSubmittedFileName();
                // Salviamo il file
                part.write(uploadFilePath + File.separator + fileName);

                // Salviamo il percorso relativo per il DB
                logoPath = "./" + UPLOAD_DIR.replace(File.separator, "/") + "/" + fileName;
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Gestione errore upload
            logoPath = null; // Se fallisce, il logo resta null
        }

        GruppoBean gruppo = null;

        if ("Club".equalsIgnoreCase(tipoGruppoStr)) {
            ClubBean club = new ClubBean();

            String importoStr = request.getParameter("importo");
            int frequenza = Integer.parseInt(request.getParameter("frequenza"));

            double importo = (importoStr != null && !importoStr.isEmpty()) ? Double.parseDouble(importoStr) : 0.0;
            club.setImporto_retta(importo);
            club.setFrequenza(frequenza);
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

        GruppoDAO gruppoDAO = new GruppoDAO();
        try {
            gruppoDAO.doSave(ConPool.getConnection(), gruppo, utente.getId_utente());
            response.sendRedirect("feedServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante la creazione del gruppo: " + e.getMessage());
            request.getRequestDispatcher("crea_gruppo.jsp").forward(request, response);
        }
    }
}
