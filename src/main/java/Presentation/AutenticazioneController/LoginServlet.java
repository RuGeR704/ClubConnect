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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errore", "Per favore compila tutti i campi."); // sempre per sapere che errore è
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        UtenteBean utente=null;
        try{
            UtenteDAO dao = new UtenteDAO();
             utente = dao.DoRetrieveEmailPassword(ConPool.getConnection(), email, password);
             System.out.println(utente.getEmail());
        }catch(SQLException e){
            e.printStackTrace();
        }

        if(utente!=null) {
            HttpSession session = request.getSession();
                session.setAttribute("utente", utente);
                response.sendRedirect(request.getContextPath() + "/index.jsp");
        }else{
            request.setAttribute("errore", "Email o password errati."); // per vedere gli errori
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
        }
    }

}
