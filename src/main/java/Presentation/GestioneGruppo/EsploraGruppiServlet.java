package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.UtenteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/EsploraGruppiServlet")
public class EsploraGruppiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UtenteDAO dao = new UtenteDAO();
        try {
            // Recuperiamo i gruppi a cui NON è iscritto
            List<GruppoBean> gruppiEsplora = dao.doRetrieveGruppiNonIscritto(ConPool.getConnection(), utente.getId_utente());

            request.setAttribute("gruppiEsplora", gruppiEsplora);
            request.getRequestDispatcher("esploraGruppi.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
