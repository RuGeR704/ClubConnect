package Presentation.AutenticazioneController;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneAccount.UtenteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    // Usiamo il Service
    private UtenteService utenteService = new UtenteService();

    // Metodo setter per iniettare un Service MOCK nei test della Servlet
    public void setUtenteService(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errore", "Per favore compila tutti i campi.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // CHIAMATA AL SERVICE
        UtenteBean utente = utenteService.login(email, password);

        if (utente != null) {
            HttpSession session = request.getSession();
            session.setAttribute("utente", utente);
            response.sendRedirect(request.getContextPath() + "/feedServlet");
        } else {
            request.setAttribute("errore", "Email o password errati.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}