package Presentation.GestioneGruppo;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/VisualizzaGruppoServlet")
public class VisualizzaGruppoServlet extends HttpServlet {

    // --- 1. DEFINIZIONE CAMPI ---
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

        // Controllo ID valido
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedServlet");
            return;
        }

        try {
            int idGruppo = Integer.parseInt(idStr);

            // A. Recupero Gruppo
            GruppoBean gruppo = gruppoService.recuperaGruppo(idGruppo);

            // Se il gruppo non esiste, redirect immediato
            if (gruppo == null) {
                response.sendRedirect("feedServlet");
                return;
            }

            // Imposto il gruppo (visibile anche ai non loggati, se previsto)
            request.setAttribute("gruppo", gruppo);

            // B. Logica Utente Loggato (Permessi, Iscrizione, Pagamenti)
            if (utente != null) {

                // 1. Recupero Comunicazioni dell'utente
                List<ComunicazioniBean> comunicazioni = comunicazioneService.recuperaComunicazioniPerUtente(utente.getId_utente());
                request.setAttribute("comunicazioni", comunicazioni);

                // 2. Controllo se è iscritto
                boolean isIscritto = false;
                try {
                    // Usiamo il metodo del gruppoService se esiste, altrimenti il ciclo sui gruppi iscritti
                    // Qui uso il ciclo come da tua logica precedente per sicurezza
                    List<GruppoBean> gruppiIscritto = accountService.getGruppiIscritto(utente.getId_utente());
                    for (GruppoBean g : gruppiIscritto) {
                        if (g.getId_gruppo() == idGruppo) {
                            isIscritto = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isIscritto = false;
                }
                request.setAttribute("isIscritto", isIscritto);

                // 3. Controllo se è Gestore
                boolean isGestore = pagamentoService.isUtenteGestore(utente.getId_utente(), idGruppo);
                request.setAttribute("isAdmin", isGestore);

                // 4. Recupero Metodi di Pagamento (per eventuale iscrizione)
                List<MetodoPagamentoBean> metodi = accountService.getMetodiPagamento(utente.getId_utente());
                request.setAttribute("metodiUtente", metodi);

                boolean hasPaid = pagamentoService.isPagato(idGruppo, utente.getId_utente());
                request.setAttribute("hasPaid", hasPaid);

                int numeroMembri = gruppoService.contaMembri(gruppo.getId_gruppo());
                request.setAttribute("numeroMembri", numeroMembri);

                int numeroEventi = gruppoService.getNumeroEventiProgrammati(gruppo.getId_gruppo());
                request.setAttribute("numeroEventi", numeroEventi);

            }

            // C. Forward alla JSP
            request.getRequestDispatcher("paginaGruppo.jsp").forward(request, response);

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