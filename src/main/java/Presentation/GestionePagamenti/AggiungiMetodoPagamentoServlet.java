package Presentation.GestionePagamenti;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.ConPool;
import Storage.PagamentoDAO;
import Storage.UtenteDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet(name = "AggiungiMetodoPagamentoServlet", value = "/AggiungiMetodoPagamentoServlet")
public class AggiungiMetodoPagamentoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            UtenteBean utente = null;

            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp"); //manda al login se la sessione non esiste
                return;
            }
            utente = (UtenteBean) session.getAttribute("utente");
            request.getRequestDispatcher("/VisualizzaMetodidiPagamentoServlet").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VisualizzaProfiloServlet");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LocalDate oggi=LocalDate.now();
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
                String nome_intestatario = request.getParameter("nome_intestatario");
                String cognome_intestatario = request.getParameter("cognome_intestatario");
                String numero_carta = request.getParameter("numero_carta");
                String scadenza_cartastr = request.getParameter("scandenza_carta");

                StringBuilder errori = new StringBuilder();

                if (nome_intestatario == null || nome_intestatario.trim().isEmpty()) errori.append("nome intestatario obbligatorio. ");

                if (scadenza_cartastr != null && scadenza_cartastr.matches("(0[1-9]|1[0-2])\\/20[2-9][0-9]")) {
                    try {
                        // Estraiamo i dati per la validazione temporale
                        String[] parts = scadenza_cartastr.split("/");
                        int mese = Integer.parseInt(parts[0]);
                        int anno = Integer.parseInt(parts[1]);

                        // Creiamo un riferimento per il confronto (primo giorno del mese indicato)
                        LocalDate scadenzaData = LocalDate.of(anno, mese, 1);
                        LocalDate oggiMese = LocalDate.now().withDayOfMonth(1);

                        if (scadenzaData.isBefore(oggiMese)) {
                            errori.append("La carta è già scaduta. ");
                        } else if (scadenzaData.isAfter(LocalDate.now().plusYears(15))) {
                            errori.append("Data di scadenza troppo lontana nel futuro. ");
                        }
                    } catch (Exception e) {
                        errori.append("Errore nel formato della data. ");
                    }
                } else {
                    errori.append("Formato scadenza non valido (richiesto MM/YYYY). ");
                }

                if (cognome_intestatario == null || cognome_intestatario.trim().isEmpty()) errori.append("cognome intestatario obbligatoria. ");
                if (numero_carta == null || numero_carta.isEmpty())
                    errori.append("numero carta non valido. ");

                if (errori.length() > 0) {
                    request.getSession().setAttribute("errorMsg", errori.toString());
                    // Torna al form di modifica di quell'utente
                    response.sendRedirect(request.getContextPath() + "/AggiungiMetodoPagamentoServlet?id_utente=" + id);
                    return;
                }

                MetodoPagamentoBean metodo = new MetodoPagamentoBean();
                metodo.setId_utente(id);
                metodo.setNome_intestatario(nome_intestatario);
                metodo.setCognome_intestatario(cognome_intestatario);
                metodo.setNumero_carta(numero_carta);
                metodo.setScadenza_carta(scadenza_cartastr);
                PagamentoDAO dao = new PagamentoDAO();
                dao.doSaveMetodoPagamento(con, metodo);
                con.commit();
                response.sendRedirect(request.getContextPath() + "/VisualizzaMetodidiPagamentoServlet");
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}