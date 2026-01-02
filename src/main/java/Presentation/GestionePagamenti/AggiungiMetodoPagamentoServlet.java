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
            request.getRequestDispatcher("/WEB-INF/AggiungiMetodoPagamentoForm.jsp").forward(request, response); //TODO scegli nome jsp
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

                LocalDate scadenza_carta = null;
                try {
                    if (scadenza_cartastr != null && !scadenza_cartastr.isEmpty()) {
                        scadenza_carta = LocalDate.parse(scadenza_cartastr);
                        if(scadenza_carta.isBefore(oggi)||scadenza_carta.isAfter(oggi.plusYears(10))) {
                            errori.append("data di scadena non valida.");
                        }
                    }else{
                        errori.append("data di scadena non valida.");
                    }
                } catch (Exception e) {
                    errori.append("Formato data di nascita non valido. ");
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
                response.sendRedirect(request.getContextPath() + "/VisualizzaProfiloServlet");
            }
        } catch (SQLException sql) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}