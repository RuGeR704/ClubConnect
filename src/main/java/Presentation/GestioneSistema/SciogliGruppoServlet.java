package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "SciogliGruppoServlet", urlPatterns = {"/SciogliGruppoServlet"})
public class SciogliGruppoServlet extends HttpServlet {

    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        String idStr = request.getParameter("idGruppo");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idGruppo = Integer.parseInt(idStr);

                GestioneSistemaInterface sistema = (sistemaTest != null)
                        ? sistemaTest
                        : new GestioneSistemaProxy(utente);

                sistema.sciogliGruppo(idGruppo);

                // Reindirizzo al Feed perché il gruppo non esiste più
                response.sendRedirect("FeedServlet?msg=GruppoSciolto");

            } catch (SecurityException e) {
                response.sendRedirect("FeedServlet?error=Unauthorized");
            } catch (NumberFormatException e) {
                response.sendRedirect("FeedServlet?error=InvalidID");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            response.sendRedirect("FeedServlet");
        }
    }
}