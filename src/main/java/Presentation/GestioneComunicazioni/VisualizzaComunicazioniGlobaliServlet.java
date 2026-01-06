package Presentation.GestioneComunicazioni;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "VisualizzaComunicazioniGlobaliServlet", urlPatterns = {"/VisualizzaComunicazioniGlobaliServlet"})
public class VisualizzaComunicazioniGlobaliServlet extends HttpServlet {

    private ComunicazioneService service = new ComunicazioneService();

    public void setService(ComunicazioneService service) {
        this.service = service;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Nota: Qui recuperiamo TUTTE e filtriamo. Ottimo per testare la logica.
            List<ComunicazioniBean> tutte = service.recuperaTutteLeComunicazioni();
            List<ComunicazioniBean> globali = tutte.stream()
                    .filter(ComunicazioniBean::isIsglobal)
                    .collect(Collectors.toList());

            request.setAttribute("listaComunicazioni", globali);
            request.getRequestDispatcher("bachecaGlobale.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("index.jsp");
        }
    }
}