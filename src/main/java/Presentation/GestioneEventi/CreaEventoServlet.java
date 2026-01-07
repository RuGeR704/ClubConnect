package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@WebServlet("/CreaEventoServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2)
public class CreaEventoServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images" + File.separator + "eventi";
    private EventoService service = new EventoService();

    public void setService(EventoService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idGruppo = -1;
        try {
            String idGruppoStr = request.getParameter("idGruppo");
            if (idGruppoStr == null || idGruppoStr.isEmpty()) throw new IllegalArgumentException("ID Gruppo mancante.");
            idGruppo = Integer.parseInt(idGruppoStr);

            EventoBean evento = new EventoBean();
            evento.setId_gruppo(idGruppo);
            evento.setNome(request.getParameter("nomeEvento"));
            evento.setDescrizione(request.getParameter("descrizione"));

            String dataOraStr = request.getParameter("dataOraEvento");
            if (dataOraStr != null && !dataOraStr.isEmpty()) {
                evento.setData_ora(LocalDateTime.parse(dataOraStr));
            } else {
                throw new IllegalArgumentException("Data mancante.");
            }

            String costoStr = request.getParameter("costo");
            evento.setCosto((costoStr != null && !costoStr.isEmpty()) ? Double.parseDouble(costoStr) : 0.0);

            // GESTIONE CAPIENZA E POSTI
            String capienzaStr = request.getParameter("capienza");
            int capienza = (capienzaStr != null && !capienzaStr.isEmpty()) ? Integer.parseInt(capienzaStr) : 0;

            evento.setCapienza_massima(capienza);
            // FIX: All'inizio i posti disponibili sono uguali alla capienza totale!
            evento.setPosti_disponibili(capienza);

            // Upload Gestito
            String fotoPath = null;
            Part part = request.getPart("foto");
            if (part != null && part.getSize() > 0) {
                String appPath = getServletContext().getRealPath("");
                String savePath = appPath + File.separator + UPLOAD_DIR;
                File fileSaveDir = new File(savePath);
                if (!fileSaveDir.exists()) fileSaveDir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                part.write(savePath + File.separator + fileName);
                fotoPath = "./" + UPLOAD_DIR.replace(File.separator, "/") + "/" + fileName;
            }
            evento.setFoto(fotoPath);

            service.creaEvento(evento);

            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&msg=evento_creato");

        } catch (Exception e) {
            String errorMsg = (e.getMessage() != null) ? URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8) : "Errore";
            if (idGruppo != -1) response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo + "&errore=" + errorMsg);
            else response.sendRedirect("feedServlet?errore=" + errorMsg);
        }
    }
}