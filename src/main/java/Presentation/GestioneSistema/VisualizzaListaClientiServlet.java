package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "VisualizzaListaClientiServlet", urlPatterns = {"/VisualizzaListaClientiServlet"})
public class VisualizzaListaClientiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        try {
            // Proxy Check: Se non sei admin, qui scatta l'eccezione
            GestioneSistemaInterface sistema = new GestioneSistemaProxy(utente);

            // Recupero la lista
            List<UtenteBean> listaClienti = sistema.visualizzaListaClienti();

            request.setAttribute("listaClienti", listaClienti);

            // Forward alla JSP di amministrazione (assicurati che il percorso sia giusto)
            request.getRequestDispatcher("/WEB-INF/jsp/adminUsers.jsp").forward(request, response);

        } catch (SecurityException e) {
            // Accesso Negato
            response.sendRedirect("FeedServlet?error=AccessoNegato");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("FeedServlet?error=GenericError");
        }
    }
}