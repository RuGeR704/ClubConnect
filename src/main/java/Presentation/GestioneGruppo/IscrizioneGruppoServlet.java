package Presentation.GestioneGruppo;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GestioneGruppoBean;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "IscrizioneGruppoServlet", value = "/IscrizioneGruppoServlet")
public class IscrizioneGruppoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente=null;
        if(session != null) {
            utente =  (UtenteBean) session.getAttribute("utente");
        }
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String idGruppoStr = request.getParameter("id_gruppo");

        if (idGruppoStr == null || idGruppoStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo mancante");
            return;
        }
        try {
            int idGruppo = Integer.parseInt(idGruppoStr);
            int idUtente = utente.getId_utente();

            // 3. Chiamata al Service
            GestioneGruppoBean service = new GestioneGruppoBean();
            service.iscriviUtenteAlGruppo(idUtente, idGruppo);
            response.sendRedirect("VisualizzaGruppoServlet?id=" + idGruppo);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Gruppo non valido");
        }
    }
}