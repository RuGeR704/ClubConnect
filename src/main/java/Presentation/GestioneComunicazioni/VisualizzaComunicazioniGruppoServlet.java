package Presentation.GestioneComunicazioni;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "VisualizzaComunicazioniGruppoServlet", urlPatterns = {"/VisualizzaComunicazioniGruppoServlet"})
public class VisualizzaComunicazioniGruppoServlet extends HttpServlet {

    // 1. Iniezione del Service
    private ComunicazioneService service = new ComunicazioneService();

    // 2. Setter per i Test
    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Recupero l'ID del gruppo
        String idGruppoStr = request.getParameter("idGruppo");

        if (idGruppoStr == null || idGruppoStr.isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // Recupero tutte le comunicazioni tramite il SERVICE iniettato
            List<ComunicazioniBean> tutteLeComunicazioni = service.recuperaTutteLeComunicazioni();

            // FILTRO: Tengo solo le comunicazioni di quel gruppo specifico
            List<ComunicazioniBean> comunicazioniDelGruppo = tutteLeComunicazioni.stream()
                    .filter(c -> c.getId_gruppo() == idGruppo)
                    .collect(Collectors.toList());

            // Passo i dati alla JSP
            request.setAttribute("listaComunicazioniGruppo", comunicazioniDelGruppo);
            request.setAttribute("idGruppoCorrente", idGruppo);

            // Visualizzazione
            request.getRequestDispatcher("bachecaGruppo.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // ID non valido (es. "abc")
            response.sendRedirect("index.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Impossibile recuperare le notizie del gruppo.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}