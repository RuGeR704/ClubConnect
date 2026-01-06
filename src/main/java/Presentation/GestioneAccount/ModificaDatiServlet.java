package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet(name = "ModificaDatiServlet", value="/ModificaDatiServlet")
public class ModificaDatiServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    // Setter per i TEST
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            // UtenteBean utente = (UtenteBean) session.getAttribute("utente"); // Non serve riassegnarlo se fai solo forward
            request.getRequestDispatcher("/WEB-INF/ModificaDatiForm.jsp").forward(request, response);
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

        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("utente");
        int id = utenteSessione.getId_utente();

        // 1. Recupero Parametri
        String username = request.getParameter("username");
        String datanascitaStr = request.getParameter("datanascita");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String cellulare = request.getParameter("cellulare");

        // 2. Validazione
        StringBuilder errori = new StringBuilder();
        if (username == null || username.trim().isEmpty()) errori.append("Username obbligatorio. ");

        LocalDate datanascita = null;
        try {
            if (datanascitaStr != null && !datanascitaStr.isEmpty()) {
                datanascita = LocalDate.parse(datanascitaStr);
                if(datanascita.isAfter(LocalDate.now())){
                    errori.append("Data di nascita non valida. ");
                }
            }
        } catch (Exception e) {
            errori.append("Formato data non valido. ");
        }

        if (password == null || password.trim().isEmpty()) errori.append("Password obbligatoria. ");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) errori.append("Email non valida. ");
        if (nome == null || nome.trim().isEmpty()) errori.append("Nome obbligatorio. ");
        if (cognome == null || cognome.trim().isEmpty()) errori.append("Cognome obbligatorio. ");
        if (cellulare != null && !cellulare.trim().isEmpty() && !cellulare.matches("^[0-9]{7,15}$"))
            errori.append("Telefono non valido. ");

        if (errori.length() > 0) {
            request.getSession().setAttribute("errorMsg", errori.toString());
            response.sendRedirect(request.getContextPath() + "/ModificaDatiServlet?id_utente=" + id);
            return;
        }

        // 3. Creazione Bean Aggiornato
        UtenteBean ub = new UtenteBean();
        ub.setId_utente(id);
        ub.setUsername(username);
        ub.setPasswordhash(password);
        ub.setEmail(email);
        ub.setNome(nome);
        ub.setCognome(cognome);
        ub.setData_nascita(datanascita);
        ub.setCellulare(cellulare);
        ub.setStato(utenteSessione.getStato()); // Mantiene lo stato originale
        ub.setIsadmin(utenteSessione.isAdmin()); // Mantiene il ruolo originale

        // 4. Chiamata al Service (LOGICA BUSINESS E DB)
        try {
            accountService.modificaDatiUtente(ub);

            // Aggiorna sessione e redirect
            session.setAttribute("utente", ub);
            response.sendRedirect(request.getContextPath() + "/AccountServlet");
        } catch (SQLException sql) {
            sql.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}