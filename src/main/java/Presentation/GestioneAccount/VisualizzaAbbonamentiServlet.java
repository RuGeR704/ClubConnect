package Presentation.GestioneAccount;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.UtenteDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "VisualizzaAbbonamentiServlet", value = "/VisualizzaAbbonamentiServlet")
public class VisualizzaAbbonamentiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);

            if(session == null || session.getAttribute("utente") == null){
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }else{
                UtenteBean utente = (UtenteBean) session.getAttribute("utente");
                UtenteDAO dao = new UtenteDAO();
                List<GruppoBean> clubiscritti = dao.doRetrieveGruppiIscritto(utente.getId_utente());
                if(clubiscritti.size()>0){
                    request.setAttribute("ClubIscrizioni", clubiscritti);
                }
                RequestDispatcher rd = request.getRequestDispatcher("/Abbonamenti.jsp");
                rd.forward(request, response);
            }
        }catch(SQLException sql){
            sql.printStackTrace();
            request.setAttribute("errore", "errore dao"); // per vedere gli errori
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Elabora la richiesta
    }
}