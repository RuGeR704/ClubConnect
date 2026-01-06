package Presentation.GestioneAccount;
import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.UtenteDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet(name = "ModificaDatiServlet", value="/ModificaDatiServlet")
public class ModificaDatiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            UtenteBean utente = null;
            // Quando sei sulla pagine utente cce un bottone modifica dati utente tipo
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp"); //manda al login se la sessione non esiste
                return;
            }
            utente = (UtenteBean) session.getAttribute("utente");
            request.getRequestDispatcher("gestioneUtente.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ModificaDati.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");
        int id = utente.getId_utente();
        try {
            try (Connection con = ConPool.getConnection()) {
                con.setAutoCommit(false);
                String username = request.getParameter("username");
                String datanascitaStr = request.getParameter("datanascita");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String nome = request.getParameter("nome");
                String cognome = request.getParameter("cognome");
                String cellulare = request.getParameter("cellulare");

                StringBuilder errori = new StringBuilder();

                if (username == null || username.trim().isEmpty()) errori.append("Username obbligatorio. ");

                LocalDate datanascita = null;
                try {
                    if (datanascitaStr != null && !datanascitaStr.isEmpty()) {
                        datanascita = LocalDate.parse(datanascitaStr);
                        if(datanascita.isAfter(LocalDate.now())){
                            errori.append("Data di nascita obbligatoria. ");
                        }
                    }
                } catch (Exception e) {
                    errori.append("Formato data di nascita non valido. ");
                }

                if (password == null || password.trim().isEmpty()) errori.append("Password obbligatoria. ");
                if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) errori.append("Email non valida. ");
                if (nome == null || nome.trim().isEmpty()) errori.append("Nome obbligatorio. ");
                if (cognome == null || cognome.trim().isEmpty()) errori.append("Cognome obbligatorio. ");
                if (cellulare != null && !cellulare.trim().isEmpty() && !cellulare.matches("^[0-9]{7,15}$"))
                    errori.append("Telefono non valido. ");

                if (errori.length() > 0) {
                    request.getSession().setAttribute("errorMsg", errori.toString());
                    // Torna al form di modifica di quell'utente
                    response.sendRedirect(request.getContextPath() + "/ModificaDatiServlet?id_utente=" + id);
                    return;
                }

                UtenteBean ub = new UtenteBean();
                ub.setId_utente(id);
                ub.setUsername(username);
                ub.setPasswordhash(password);
                ub.setEmail(email);
                ub.setNome(nome);
                ub.setCognome(cognome);
                ub.setData_nascita(datanascita);
                ub.setCellulare(cellulare);
                ub.setStato(utente.getStato());
                ub.setIsadmin(utente.isAdmin());

                UtenteDAO dao = new UtenteDAO();
                dao.doUpdate(con, ub);
                con.commit();
                session.setAttribute("utente", ub);
                response.sendRedirect(request.getContextPath() + "/gestioneUtente.jsp");
            }
        } catch (SQLException sql) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }

    }
}
