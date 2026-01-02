package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/VisualizzaGruppoServlet")
public class VisualizzaGruppoServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String id = request.getParameter("id");
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (id == null || id.isEmpty() || utente == null ) {
            response.sendRedirect("feedServlet");
            return;
        }

        int idGruppo = Integer.parseInt(id);
        GruppoDAO dao = new GruppoDAO();

        try {
            GruppoBean gruppo = dao.doRetrieveByid(ConPool.getConnection(), idGruppo);

            if (gruppo != null) {
                boolean isAdmin = false;
                boolean isIscritto = false;

                request.setAttribute("gruppo", gruppo);
                request.setAttribute("isAdmin", isAdmin);
                request.setAttribute("isIscritto", isIscritto);

                RequestDispatcher view = request.getRequestDispatcher("paginaGruppo.jsp");
                view.forward(request, response);
            } else {
                System.out.println("DEBUG: Gruppo NON trovato (NULL) -> Redirect Feed");
                response.sendRedirect("feedServlet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
