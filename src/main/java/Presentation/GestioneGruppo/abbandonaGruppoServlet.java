package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/abbandonaGruppoServlet")
public class abbandonaGruppoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Controllo Sessione
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;
        GruppoDAO gruppodao = new GruppoDAO();

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Recupero Parametri
        String idGruppoStr = request.getParameter("idGruppo");
        int idUtente = utente.getId_utente();

        if (idGruppoStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        try (Connection con = ConPool.getConnection()) {
            int idGruppo = Integer.parseInt(idGruppoStr);

            // Esecuzione della logica di business tramite DAO
            if (gruppodao.doRimuoviMembro(con, idGruppo, idUtente)) {
                // Successo: l'utente non fa più parte del gruppo
                session.setAttribute("successMsg", "Hai abbandonato il gruppo correttamente.");

                // Reindirizziamo alla servlet che mostra i gruppi a cui è iscritto
                response.sendRedirect(request.getContextPath() + "/VisualizzaIscrizioniGruppiServlet");
            } else {
                // Fallimento (es. l'utente non era iscritto o errore DB)
                session.setAttribute("errorMsg", "Impossibile abbandonare il gruppo. Riprova più tardi.");
                response.sendRedirect(request.getContextPath() + "/VisualizzaGruppoServlet?id=" + idGruppo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // In caso di errore SQL, mandiamo alla pagina di errore generica
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo non valido");
        }
    }

}

