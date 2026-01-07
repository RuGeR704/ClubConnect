package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/ModificaEventoServlet")
public class ModificaEventoServlet extends HttpServlet {

    private EventoService service = new EventoService();

    public void setService(EventoService service) {
        this.service = service;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idEvento = Integer.parseInt(request.getParameter("idEvento"));
            int idGruppo = Integer.parseInt(request.getParameter("id_gruppo"));

            EventoBean evento = new EventoBean();
            evento.setId_evento(idEvento);
            evento.setId_gruppo(idGruppo);
            evento.setNome(request.getParameter("nome"));
            evento.setDescrizione(request.getParameter("descrizione"));
            evento.setFoto(request.getParameter("foto"));
            evento.setCosto(Double.parseDouble(request.getParameter("costo")));
            evento.setPosti_disponibili(Integer.parseInt(request.getParameter("posti_disponibili")));
            evento.setCapienza_massima(Integer.parseInt(request.getParameter("capienza_massima")));

            String dataStr = request.getParameter("data_ora");
            if(dataStr != null && !dataStr.isEmpty()) evento.setData_ora(LocalDateTime.parse(dataStr));

            service.modificaEvento(evento);

            response.sendRedirect("visualizzaEvento.jsp?id=" + idEvento + "&msg=modifica_ok");

        } catch (Exception e) {
            request.setAttribute("errore", "Errore modifica: " + e.getMessage());
            request.getRequestDispatcher("modificaEvento.jsp").forward(request, response);
        }
    }
}