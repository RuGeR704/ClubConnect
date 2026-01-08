package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
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

    private GruppoService service = new GruppoService();

    public void setService(GruppoService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Service wrappa UtenteDAO.doRetrieveGruppiNonIscritto
            List<GruppoBean> gruppiEsplora = service.recuperaGruppiNonIscritto(utente.getId_utente());

            request.setAttribute("gruppiEsplora", gruppiEsplora);
            request.getRequestDispatcher("esploraGruppi.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}