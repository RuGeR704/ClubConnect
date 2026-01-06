package Presentation.GestioneComunicazioni;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.GestioneComunicazioniBean;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Recupero l'ID del gruppo di cui voglio vedere le news
        String idGruppoStr = request.getParameter("idGruppo");

        if (idGruppoStr == null || idGruppoStr.isEmpty()) {
            // Se non c'è l'ID, non so cosa mostrare -> torno indietro o home
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            GestioneComunicazioniBean service = new GestioneComunicazioniBean();

            // Recupero tutte le comunicazioni
            List<ComunicazioniBean> tutteLeComunicazioni = service.recuperaTutteLeComunicazioni();

            // FILTRO: Tengo solo le comunicazioni che hanno id_gruppo uguale a quello richiesto
            List<ComunicazioniBean> comunicazioniDelGruppo = tutteLeComunicazioni.stream()
                    .filter(c -> c.getId_gruppo() == idGruppo)
                    .collect(Collectors.toList());

            // Passo i dati alla JSP
            request.setAttribute("listaComunicazioniGruppo", comunicazioniDelGruppo);
            request.setAttribute("idGruppo", idGruppo); // Utile per link "Torna al gruppo" o per i form di invio

            // Visualizzazione
            jakarta.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("paginaGruppo.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("feedServlet"); // ID non valido
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Impossibile recuperare le notizie del gruppo.");
            request.getRequestDispatcher("feedServlet").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}