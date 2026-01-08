package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    // 1. Dependency Injection
    private GruppoService gruppoService = new GruppoService();
    private EventoService eventoService = new EventoService();

    // 2. Setters per i Test
    public void setGruppoService(GruppoService gs) { this.gruppoService = gs; }
    public void setEventoService(EventoService es) { this.eventoService = es; }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idGruppoStr = request.getParameter("idGruppo");
        if (idGruppoStr == null) {
            response.sendRedirect("VisualizzaIscrizioniGruppiServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // 3. Uso dei Service
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);
            int membri = gruppoService.contaMembri(idGruppo);
            List<UtenteBean> membriGruppo = gruppoService.recuperaSociDelGruppo(idGruppo);

            // Recupero Eventi e filtro
            List<EventoBean> tuttiEventi = eventoService.retrieveAllEventi();
            List<EventoBean> eventiDelGruppo = tuttiEventi.stream()
                    .filter(e -> e.getId_gruppo() == idGruppo)
                    .collect(Collectors.toList());

            double entrateMensili = 0.0;
            if (gruppo instanceof ClubBean) {
                entrateMensili = membri * ((ClubBean) gruppo).getImporto_retta();
            }

            request.setAttribute("gruppo", gruppo);
            request.setAttribute("totaleMembri", membri);
            request.setAttribute("nuoviIscritti", 1);
            request.setAttribute("entrateMensili", entrateMensili);
            request.setAttribute("membri", membriGruppo);
            request.setAttribute("eventi", eventiDelGruppo);

            request.getRequestDispatcher("gestioneGruppo.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("VisualizzaIscrizioniGruppiServlet");
        }
    }
}