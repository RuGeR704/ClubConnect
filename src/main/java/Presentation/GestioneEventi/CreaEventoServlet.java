package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/CreaEventoServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class CreaEventoServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "eventi";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int idGruppo = -1;

        try {
            // 1. RECUPERO ID GRUPPO (Hidden field)
            String idGruppoStr = request.getParameter("idGruppo");
            if (idGruppoStr == null || idGruppoStr.isEmpty()) {
                throw new IllegalArgumentException("ID Gruppo mancante.");
            }
            idGruppo = Integer.parseInt(idGruppoStr);

            // 2. CREAZIONE BEAN
            EventoBean evento = new EventoBean();
            evento.setId_gruppo(idGruppo);
            evento.setNome(request.getParameter("nomeEvento"));
            evento.setDescrizione(request.getParameter("descrizione"));

            // 3. GESTIONE DATA E ORA UNIFICATA (datetime-local)
            String dataOraStr = request.getParameter("dataOraEvento");
            if (dataOraStr != null && !dataOraStr.isEmpty()) {
                // LocalDateTime di Java lo parsa nativamente perché è formato ISO standard
                LocalDateTime dataOra = LocalDateTime.parse(dataOraStr);
                evento.setData_ora(dataOra);
            } else {
                throw new IllegalArgumentException("Data e Ora sono obbligatorie.");
            }

            // 4. GESTIONE NUMERI (Costo e Capienza)
            String costoStr = request.getParameter("costo");
            evento.setCosto((costoStr != null && !costoStr.isEmpty()) ? Double.parseDouble(costoStr) : 0.0);

            String capienzaStr = request.getParameter("capienza");
            // Se vuoto o <= 0, gestiscilo come vuoi (es. 0 = illimitato)
            evento.setCapienza_massima((capienzaStr != null && !capienzaStr.isEmpty()) ? Integer.parseInt(capienzaStr) : 0);

            // 5. GESTIONE IMMAGINE (Upload fisico)
            String fotoPath = null;
            Part part = request.getPart("foto"); // name="foto" nel form

            if (part != null && part.getSize() > 0) {
                String appPath = request.getServletContext().getRealPath("");
                String savePath = appPath + File.separator + UPLOAD_DIR;

                File fileSaveDir = new File(savePath);
                if (!fileSaveDir.exists()) fileSaveDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                part.write(savePath + File.separator + fileName);

                // Percorso web relativo
                fotoPath = "./" + UPLOAD_DIR.replace(File.separator, "/") + "/" + fileName;
            }
            evento.setFoto(fotoPath); // O setLocandina, controlla il tuo Bean

            // 6. SALVATAGGIO TRAMITE SERVICE
            GestioneEventiBean service = new GestioneEventiBean();
            service.creaEvento(evento);

            // 7. SUCCESSO: Redirect alla pagina del gruppo
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=evento_creato");

        } catch (Exception e) {
            e.printStackTrace();

            // 8. ERRORE: Redirect con messaggio
            String errorMsg = "Errore durante la creazione";
            if(e.getMessage() != null) {
                errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            }

            if (idGruppo != -1) {
                response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&errore=" + errorMsg);
            } else {
                response.sendRedirect("feedServlet?errore=" + errorMsg);
            }
        }
    }
}