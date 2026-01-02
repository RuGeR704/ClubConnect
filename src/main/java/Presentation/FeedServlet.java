package Presentation;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebServlet("/feedServlet")
public class FeedServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        GruppoDAO gruppoDAO = new GruppoDAO();
        ComunicazioneDAO comunicazioneDAO = new ComunicazioneDAO();
        EventoDAO eventoDAO = new EventoDAO();
        List<Object> feedMisto = new ArrayList<>();
        Collection<GruppoBean> gruppiSuggeriti = new ArrayList<>();
        boolean hasIscrizione = false;

        try{
            List<GruppoBean> gruppi = utenteDAO.doRetrieveGruppiIscritto(utente.getId_utente());

            if (gruppi != null && !gruppi.isEmpty()) {
                hasIscrizione = true;

                List<ComunicazioniBean> comunicazioni = comunicazioneDAO.doRetrievebyGroup(ConPool.getConnection(), utente.getId_utente());
                List<EventoBean> eventi = eventoDAO.doRetrievebyGroup(ConPool.getConnection(), utente.getId_utente());


                if(comunicazioni != null) {
                    feedMisto.addAll(comunicazioni);
                }
                if(eventi != null) {
                    feedMisto.addAll(eventi);
                }

                Collections.shuffle(feedMisto);

                request.setAttribute("feedMisto", feedMisto);
            } else {
                hasIscrizione = false;
                gruppiSuggeriti =  gruppoDAO.doRetrieveAll(ConPool.getConnection());
            }

            request.setAttribute("hasIscrizioni", hasIscrizione);
            request.setAttribute("feedMisto", feedMisto);
            request.setAttribute("gruppiSuggeriti", gruppiSuggeriti);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        RequestDispatcher view = request.getRequestDispatcher("feed.jsp");
        view.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }
}
