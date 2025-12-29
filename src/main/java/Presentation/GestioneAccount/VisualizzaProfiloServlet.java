package Presentation.GestioneAccount;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "VisualizzaProfiloServlet", value = "/VisualizzaProfiloServlet")
public class VisualizzaProfiloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        request.setAttribute("utente", utente);
        RequestDispatcher rd = request.getRequestDispatcher("/Profilo.jsp");
        rd.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Elabora la richiesta
    }
}