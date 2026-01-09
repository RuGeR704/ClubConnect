package Presentation.GestioneGruppo;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import Application.GestionePagamenti.MetodoPagamentoBean;
import jakarta.servlet.RequestDispatcher;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/VisualizzaGruppoServlet")
public class VisualizzaGruppoServlet extends HttpServlet {

    // --- 1. DEFINIZIONE CAMPI (Una sola volta!) ---
    private GruppoService gruppoService = new GruppoService();
    private PagamentoService pagamentoService = new PagamentoService();
    private AccountService accountService = new AccountService();
    private ComunicazioneService comunicazioneService = new ComunicazioneService();

    // --- 2. SETTER PER I TEST ---
    public void setGruppoService(GruppoService service) {
        this.gruppoService = service;
    }

    public void setPagamentoService(PagamentoService service) {
        this.pagamentoService = service;
    }

    public void setAccountService(AccountService service) {
        this.accountService = service;
    }

    public void setComunicazioneService(ComunicazioneService service) {
        this.comunicazioneService = service;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);

            // Uso del Service
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);

            if (gruppo != null) {
                // Recupero Comunicazioni
                List<ComunicazioniBean> comunicazioni = comService.recuperaComunicazioniPerUtente(utente.getId_utente());

                // Controllo Permessi (usando i nuovi metodi del service)
                boolean isIscritto = gruppoService.isUtenteIscritto(idGruppo, utente.getId_utente());
                boolean isAdmin = gruppoService.isUtenteGestore(idGruppo, utente.getId_utente());
                List<MetodoPagamentoBean> metodipagamenti = asService.getMetodiPagamento(utente.getId_utente());

            if (gruppo == null) {
                response.sendRedirect("feedServlet");
                return;
            }
            request.setAttribute("gruppo", gruppo);

            // B. Logica Utente Loggato
            if (utente != null) {
                // 1. Controllo se è iscritto
                boolean isIscritto = false;
                try {
                    List<GruppoBean> gruppiIscritto = accountService.getGruppiIscritto(utente.getId_utente());
                    for(GruppoBean g : gruppiIscritto) {
                        // NOTA: Controlla se nel Bean hai 'getId()' o 'getId_gruppo()'
                        if(g.getId_gruppo() == idGruppo) {
                            isIscritto = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isIscritto = false;
                }
                request.setAttribute("isIscritto", isIscritto);
                request.setAttribute("comunicazioni", comunicazioni);
                request.setAttribute("metodiUtente", metodipagamenti);

                // 2. Controllo se è Gestore
                boolean isGestore = pagamentoService.isUtenteGestore(utente.getId_utente(), idGruppo);
                request.setAttribute("isGestore", isGestore);

                // 3. Caricamento Metodi di Pagamento
                try {
                    List<MetodoPagamentoBean> metodi = accountService.getMetodiPagamento(utente.getId_utente());
                    request.setAttribute("metodiPagamento", metodi);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // C. Forward alla JSP
            request.getRequestDispatcher("visualizza_gruppo.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("feedServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}