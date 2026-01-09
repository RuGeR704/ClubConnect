package Application.GestioneFeed;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedService {

    private UtenteDAO utenteDAO;
    private GruppoDAO gruppoDAO;
    private ComunicazioneDAO comunicazioneDAO;
    private EventoDAO eventoDAO;

    public FeedService() {
        this.utenteDAO = new UtenteDAO();
        this.gruppoDAO = new GruppoDAO();
        this.comunicazioneDAO = new ComunicazioneDAO();
        this.eventoDAO = new EventoDAO();
    }

    // Setters per i Test
    public void setUtenteDAO(UtenteDAO dao) { this.utenteDAO = dao; }
    public void setGruppoDAO(GruppoDAO dao) { this.gruppoDAO = dao; }
    public void setComunicazioneDAO(ComunicazioneDAO dao) { this.comunicazioneDAO = dao; }
    public void setEventoDAO(EventoDAO dao) { this.eventoDAO = dao; }

    // 1. Recupera i gruppi a cui l'utente è iscritto
    public List<GruppoBean> getGruppiIscritto(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            return utenteDAO.doRetrieveGruppiIscritto(con, idUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 2. Recupera il Feed Misto (Eventi + Comunicazioni) e lo mescola
    public List<Object> getFeedMisto(int idUtente) {
        List<Object> feedMisto = new ArrayList<>();
        try (Connection con = ConPool.getConnection()) {
            List<ComunicazioniBean> com = comunicazioneDAO.doRetrievebyGroup(con, idUtente);
            List<EventoBean> ev = eventoDAO.doRetrievebyGruppiIscritti(con, idUtente);

            if (com != null) feedMisto.addAll(com);
            if (ev != null) feedMisto.addAll(ev);

            Collections.shuffle(feedMisto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedMisto;
    }

    // 3. Recupera i Suggerimenti (Tutti i gruppi)
    public List<GruppoBean> getSuggerimenti() {
        try (Connection con = ConPool.getConnection()) {
            // Nota: doRetrieveAll di solito restituisce Collection o List
            return (List<GruppoBean>) gruppoDAO.doRetrieveAll(con);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 4. Recupera Eventi Prenotati
    public List<EventoBean> getEventiPrenotati(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            return eventoDAO.doRetrieveEventiByUtente(con, idUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}