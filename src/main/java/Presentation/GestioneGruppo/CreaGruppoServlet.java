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

@WebServlet("/CreazioneGruppoServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class CreaGruppoServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "gruppi";

    // Iniezione del Service
    private GruppoService service = new GruppoService();

    // Setter per i Test
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

        String nome = request.getParameter("nome");
        String settore = request.getParameter("settore");
        String slogan = request.getParameter("slogan");
        String descrizione = request.getParameter("descrizione");
        String sede = request.getParameter("sede");
        String regole = request.getParameter("regole");
        String tipoGruppoStr = request.getParameter("tipoGruppo");

        // Gestione Upload Immagine
        String logoPath = null;
        try {
            Part part = request.getPart("logo");
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

        // Creazione Bean
        GruppoBean gruppo;
        if ("Club".equalsIgnoreCase(tipoGruppoStr)) {
            ClubBean club = new ClubBean();
            String importoStr = request.getParameter("importo");
            String frequenzaStr = request.getParameter("frequenza");

            club.setImporto_retta((importoStr != null && !importoStr.isEmpty()) ? Double.parseDouble(importoStr) : 0.0);
            club.setFrequenza((frequenzaStr != null && !frequenzaStr.isEmpty()) ? Integer.parseInt(frequenzaStr) : 0);
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
            // Uso del Service iniettato
            service.creaGruppo(gruppo, utente.getId_utente());
            response.sendRedirect("feedServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante la creazione del gruppo: " + e.getMessage());
            request.getRequestDispatcher("crea_gruppo.jsp").forward(request, response);
        }
    }
}