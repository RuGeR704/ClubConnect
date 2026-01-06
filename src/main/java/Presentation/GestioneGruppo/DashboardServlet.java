package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.EventoDAO;
import Storage.GruppoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

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

        int idGruppo = Integer.parseInt(idGruppoStr);

        GruppoDAO gruppoDAO = new GruppoDAO();
        try {
            GruppoBean gruppo = gruppoDAO.doRetrieveByid(ConPool.getConnection(), idGruppo);
            request.setAttribute("gruppo", gruppo);

            //placeholder: per vedere se funziona
            int membri = gruppoDAO.contaMembri(ConPool.getConnection(), idGruppo);
            int nuoviMembri = 1;
            double entrateMensili = 0.0;

            if(gruppo instanceof ClubBean) {
                entrateMensili = membri * ((ClubBean)gruppo).getImporto_retta(); // Esempio semplice
            }

            EventoDAO eventoDAO = new EventoDAO();
            List<EventoBean> eventi = eventoDAO.doRetrievebyGroup(ConPool.getConnection(), idGruppo);
            List<UtenteBean> membriGruppo = gruppoDAO.doRetrieveSoci(ConPool.getConnection(), idGruppo);

            request.setAttribute("totaleMembri", membri);
            request.setAttribute("nuoviIscritti", nuoviMembri);
            request.setAttribute("entrateMensili", entrateMensili);
            request.setAttribute("membri", membriGruppo);
            request.setAttribute("eventi", eventi);

            request.getRequestDispatcher("gestioneGruppo.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
