package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/VisualizzaGruppoServlet")
public class VisualizzaGruppoServlet extends HttpServlet {

    // Dependency Injection
    private GruppoService gruppoService = new GruppoService();
    private ComunicazioneService comService = new ComunicazioneService();

    // Setters per i Test
    public void setGruppoService(GruppoService gs) { this.gruppoService = gs; }
    public void setComunicazioneService(ComunicazioneService cs) { this.comService = cs; }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (idStr == null || idStr.isEmpty() || utente == null) {
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

                // Chiedo al Service quanti membri ci sono
                int numeroMembri = gruppoService.getNumeroMembri(idGruppo);

                // Chiedo al Service quanti eventi futuri ci sono
                int numeroEventi = gruppoService.getNumeroEventiProgrammati(idGruppo);

                // Salvo questi numeri nella request per mandarli alla JSP
                request.setAttribute("numeroMembri", numeroMembri);
                request.setAttribute("numeroEventi", numeroEventi);
                request.setAttribute("gruppo", gruppo);
                request.setAttribute("isAdmin", isAdmin);
                request.setAttribute("isIscritto", isIscritto);
                request.setAttribute("comunicazioni", comunicazioni);

                RequestDispatcher view = request.getRequestDispatcher("paginaGruppo.jsp");
                view.forward(request, response);
            } else {
                response.sendRedirect("feedServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("feedServlet");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}