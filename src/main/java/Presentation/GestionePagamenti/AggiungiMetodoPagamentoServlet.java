package Presentation.GestionePagamenti;

import Application.GestioneAccount.AccountService;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@WebServlet("/AggiungiMetodoPagamentoServlet")
public class AggiungiMetodoPagamentoServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    // Setter per i test
    public void setAccountService(AccountService service) {
        this.accountService = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 1. Recupero Parametri
        String nome = request.getParameter("nome_intestatario");
        String cognome = request.getParameter("cognome_intestatario");
        String numero = request.getParameter("numero_carta");
        String scadenzaStr = request.getParameter("scandenza_carta"); // Nota: il tuo parametro era 'scandenza' con la 'n'

        StringBuilder errori = new StringBuilder();

        // 2. Validazione Input (Category Partition)
        if (nome == null || nome.trim().isEmpty()) {
            errori.append("Nome intestatario obbligatorio. ");
        }
        if (cognome == null || cognome.trim().isEmpty()) {
            errori.append("Cognome intestatario obbligatorio. ");
        }

        // Validazione Numero Carta (Semplificata: 16 cifre)
        if (numero == null || !numero.matches("\\d{16}")) {
            errori.append("Numero carta non valido (richieste 16 cifre). ");
        }

        // Validazione Scadenza (Stringa MM/yyyy)
        if (scadenzaStr == null || !scadenzaStr.matches("(0[1-9]|1[0-2])\\/20[2-9][0-9]")) {
            errori.append("Formato scadenza non valido (richiesto MM/YYYY). ");
        } else {
            // Controllo logico data (non scaduta)
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");
                YearMonth scadenzaDate = YearMonth.parse(scadenzaStr, fmt);
                YearMonth oggi = YearMonth.now();

                if (scadenzaDate.isBefore(oggi)) {
                    errori.append("La carta è scaduta. ");
                }
            } catch (DateTimeParseException e) {
                errori.append("Data non valida. ");
            }
        }

        // Se ci sono errori, torna indietro
        if (errori.length() > 0) {
            request.setAttribute("errorMsg", errori.toString());
            request.getRequestDispatcher("aggiungi_metodo.jsp").forward(request, response);
            return;
        }

        try {
            // 3. Creazione Bean (Usando i setter corretti del tuo codice)
            MetodoPagamentoBean metodo = new MetodoPagamentoBean();
            metodo.setId_utente(utente.getId_utente());
            metodo.setNome_intestatario(nome);
            metodo.setCognome_intestatario(cognome);
            metodo.setNumero_carta(numero);
            metodo.setScadenza_carta(scadenzaStr); // Passiamo la Stringa direttamente

            // 4. Chiamata al Service
            accountService.aggiungiMetodoPagamento(metodo);

            response.sendRedirect("VisualizzaMetodidiPagamentoServlet"); // Attenzione al nome della servlet di destinazione

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("VisualizzaMetodidiPagamentoServlet");
    }
}