package Presentation.AutenticazioneController;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneAccount.UtenteService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet ("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {

    private UtenteService utenteService = new UtenteService();

    // Setter per i test
    public void setUtenteService(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // ... Logica di parsing identica a prima (recupero parametri) ...
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String username = request.getParameter("username");
        String dataStr = request.getParameter("dataNascita");
        LocalDate data = LocalDate.parse(dataStr);
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String telefono = request.getParameter("prefisso") + request.getParameter("telefono");

        UtenteBean utente = new UtenteBean();

        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setUsername(username);
        utente.setData_nascita(data);
        utente.setEmail(email);
        utente.setPassword(password);
        utente.setCellulare(telefono);

        try {
            // USIAMO IL SERVICE
            utenteService.registraUtente(utente);

            request.getSession().setAttribute("utente", utente);
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/confermaRegistrazione.jsp");
            view.forward(request,response);

        } catch (SQLException e) {
            e.printStackTrace();
            String erroreMsg = "Errore tecnico durante la registrazione";
            if (e.getMessage().contains("email") || e.getMessage().contains("username")) {
                erroreMsg = "Email o Username già in uso"; // Messaggio più pulito
            }
            request.setAttribute("errore", erroreMsg);
            request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}