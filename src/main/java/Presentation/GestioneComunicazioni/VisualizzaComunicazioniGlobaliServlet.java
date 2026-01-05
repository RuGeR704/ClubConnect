package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.GestioneComunicazioniBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "VisualizzaComunicazioniGlobaliServlet", urlPatterns = {"/VisualizzaComunicazioniGlobaliServlet"})
public class VisualizzaComunicazioniGlobaliServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // visibile solo ai loggati
        HttpSession session = request.getSession();
        if (session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        GestioneComunicazioniBean service = new GestioneComunicazioniBean();

        try {
            // Recupera tutte le comunicazioni dal DB
            List<ComunicazioniBean> tutteLeComunicazioni = service.recuperaTutteLeComunicazioni();

            // FILTRO: Vogliamo mostrare solo quelle globali
            // uso uno stream per filtrare la lista e tenere solo quelle con isIsglobal() == true
            List<ComunicazioniBean> comunicazioniGlobali = tutteLeComunicazioni.stream()
                    .filter(ComunicazioniBean::isIsglobal)
                    .collect(Collectors.toList());

            // Passiamo la lista filtrata alla JSP
            request.setAttribute("listaComunicazioni", comunicazioniGlobali);

            // Forward alla pagina JSP di visualizzazione
            jakarta.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher("bachecaGlobale.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Impossibile recuperare le notizie.");
            // In caso di errore mandiamo a una pagina di errore o alla home vuota
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
