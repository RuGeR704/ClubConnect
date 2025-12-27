package Presentation.AutenticazioneController;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.UtenteDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet ("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

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

        request.getSession().setAttribute("utente", utente);

        try {
            UtenteDAO utenteDAO = new UtenteDAO();
            utenteDAO.doSave(ConPool.getConnection(), utente);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/confermaRegistrazione.jsp");
        view.forward(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
