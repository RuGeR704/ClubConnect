package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.GestioneEventiBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/ModificaEventoServlet")
public class ModificaEventoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // Recupero ID Evento (per la modifica)
            int idEvento = Integer.parseInt(request.getParameter("idEvento"));

            // Recupero altri parametri
            String nome = request.getParameter("nome");
            String descrizione = request.getParameter("descrizione");
            String foto = request.getParameter("foto");
            double costo = Double.parseDouble(request.getParameter("costo"));
            int posti = Integer.parseInt(request.getParameter("posti_disponibili"));
            int capienza = Integer.parseInt(request.getParameter("capienza_massima"));
            int idGruppo = Integer.parseInt(request.getParameter("id_gruppo"));

            // Popolamento Bean
            EventoBean evento = new EventoBean();
            evento.setId_evento(idEvento); // Impostiamo l'ID da modificare
            evento.setId_gruppo(idGruppo);
            evento.setNome(nome);
            evento.setDescrizione(descrizione);
            evento.setFoto(foto);
            evento.setCosto(costo);
            evento.setPosti_disponibili(posti);
            evento.setCapienza_massima(capienza);

            String dataStr = request.getParameter("data_ora");
            if(dataStr != null && !dataStr.isEmpty()) {
                evento.setData_ora(LocalDateTime.parse(dataStr));
            }

            // Chiamata al Service
            GestioneEventiBean service = new GestioneEventiBean();
            service.modificaEvento(evento);

            // Redirect al dettaglio dell'evento modificato
            response.sendRedirect("visualizzaEvento.jsp?id=" + idEvento + "&msg=modifica_ok");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore modifica: " + e.getMessage());
            // In caso di errore torniamo al form di modifica (che potrebbe recuperare i dati vecchi)
            request.getRequestDispatcher("modificaEvento.jsp").forward(request, response);
        }
    }
}
