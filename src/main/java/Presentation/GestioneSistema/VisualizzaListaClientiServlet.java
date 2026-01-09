package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import Application.GestioneSistema.GestioneSistemaProxy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "VisualizzaListaClientiServlet", urlPatterns = {"/VisualizzaListaClientiServlet"})
public class VisualizzaListaClientiServlet extends HttpServlet {

    private GestioneSistemaInterface sistemaTest;

    public void setSistemaTest(GestioneSistemaInterface sistemaTest) {
        this.sistemaTest = sistemaTest;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            GestioneSistemaInterface sistema = (sistemaTest != null)
                    ? sistemaTest
                    : new GestioneSistemaProxy(utente);

            // Recupera la lista tramite il Proxy (che controlla se sei admin)
            List<UtenteBean> listaClienti = sistema.visualizzaListaClienti();

            request.setAttribute("listaClienti", listaClienti);

            // Assicurati che il percorso della JSP sia corretto nel tuo progetto
            request.getRequestDispatcher("adminUsers.jsp").forward(request, response);

        } catch (SecurityException e) {
            // Accesso Negato
            response.sendRedirect("FeedServlet?error=Unauthorized");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("FeedServlet?error=GenericError");
        }
    }
}