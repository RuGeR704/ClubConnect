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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {

    private UtenteService utenteService = new UtenteService();

    public void setUtenteService(UtenteService service) {
        this.utenteService = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dataNascitaStr = request.getParameter("dataNascita");
        String cellulare = request.getParameter("cellulare");

        // --- FIX PER IL CASO CELLULARE VUOTO ---
        if (cellulare == null && request.getParameter("telefono") != null) {
            String tel = request.getParameter("telefono");
            // Se il numero è vuoto, ignoriamo il prefisso e settiamo cellulare a vuoto
            // così scatta l'errore "campo vuoto" invece di "formato errato"
            if (tel == null || tel.trim().isEmpty()) {
                cellulare = "";
            } else {
                String prefisso = request.getParameter("prefisso") != null ? request.getParameter("prefisso") : "";
                cellulare = prefisso + tel;
            }
        }
        // ---------------------------------------

        String errore = null;

        // Validazioni (Invariate)
        if (username == null || username.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo Username è vuoto";
        } else if (username.length() > 30) {
            errore = "Errore: La registrazione non va a buon fine perché l'username è troppo lungo";
        } else if (nome == null || nome.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo Nome è vuoto";
        } else if (nome.length() > 30) {
            errore = "Errore: La registrazione non va a buon fine perché il campo Nome è troppo lungo";
        } else if (cognome == null || cognome.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo Cognome è vuoto";
        } else if (cognome.length() > 30) {
            errore = "Errore: La registrazione non va a buon fine perché il campo Cognome è troppo lungo";
        } else if (dataNascitaStr == null || dataNascitaStr.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo data di nascita è vuoto";
        } else if (email == null || email.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo email è vuoto";
        } else if (!isValidEmail(email)) {
            errore = "Errore: La registrazione non va a buon fine perché l'email non rispetta il formato";
        } else if (password == null || password.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo password è vuoto";
        } else if (cellulare == null || cellulare.trim().isEmpty()) {
            errore = "Errore: La registrazione non va a buon fine perché il campo cellulare è vuoto";
        } else if (!isValidCellulare(cellulare)) {
            errore = "Errore: La registrazione non va a buon fine perché il campo cellulare non rispetta il formato";
        }

        LocalDate dataNascita = null;
        if (errore == null) {
            try {
                dataNascita = LocalDate.parse(dataNascitaStr);
            } catch (DateTimeParseException e) {
                errore = "Errore: La registrazione non va a buon fine perché il campo data di nascita non rispetta il formato";
            }
        }

        if (errore != null) {
            request.setAttribute("errore", errore);
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }

        try {
            UtenteBean nuovoUtente = new UtenteBean();
            nuovoUtente.setUsername(username);
            nuovoUtente.setNome(nome);
            nuovoUtente.setCognome(cognome);
            nuovoUtente.setEmail(email);
            nuovoUtente.setPassword(password);
            nuovoUtente.setData_nascita(dataNascita);
            nuovoUtente.setCellulare(cellulare);
            nuovoUtente.setStato(1);

            utenteService.registraUtente(nuovoUtente);

            HttpSession session = request.getSession();
            session.setAttribute("utente", nuovoUtente);
            // NOTA: Qui facciamo REDIRECT, quindi il test deve verificare sendRedirect
            response.sendRedirect("feedServlet");

        } catch (IllegalArgumentException e) {
            request.setAttribute("errore", "Errore: " + e.getMessage());
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidCellulare(String cellulare) {
        String phoneRegex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        return Pattern.compile(phoneRegex).matcher(cellulare.replace(" ", "")).matches();
    }
}