package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "BannaUtenteServlet", urlPatterns = {"/BannaUtenteServlet"})
public class BannaUtenteServlet extends HttpServlet {

    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        String idStr = request.getParameter("idUtente");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int idDaBannare = Integer.parseInt(idStr);

                GestioneSistemaInterface sistema = (sistemaTest != null)
                        ? sistemaTest
                        : new GestioneSistemaProxy(utente);

                sistema.bannaUtente(idDaBannare);

                response.sendRedirect("VisualizzaListaClientiServlet?msg=UtenteBannato");

            } catch (SecurityException e) {
                response.sendRedirect("FeedServlet?error=Unauthorized");
            } catch (NumberFormatException e) {
                response.sendRedirect("VisualizzaListaClientiServlet?error=InvalidID");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("FeedServlet?error=GenericError");
            }
        } else {
            response.sendRedirect("VisualizzaListaClientiServlet");
        }
    }
}