package Application.GestioneEventi;

import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.EventoDAO;
import Storage.GruppoDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EventoService {

    private EventoDAO eventoDAO;

    public EventoService() {
        this.eventoDAO = new EventoDAO();
    }

    // Costruttore per i TEST
    public EventoService(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    // --- METODI ---

    public void creaEvento(EventoBean evento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            creaEvento(con, evento);
        }
    }
    // Overload per Test
    public void creaEvento(Connection con, EventoBean evento) throws SQLException {
        eventoDAO.DoSave(con, evento);
    }

    public void registraPartecipazione(PartecipazioneBean partecipazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            registraPartecipazione(con, partecipazione);
        }
    }
    public void registraPartecipazione(Connection con, PartecipazioneBean partecipazione) throws SQLException {
        eventoDAO.DoSavePartecipazioni(con, partecipazione);
    }

    public PartecipazioneBean retrievePartecipazione(int idUtente, int idEvento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return retrievePartecipazione(con, idUtente, idEvento);
        }
    }
    public PartecipazioneBean retrievePartecipazione(Connection con, int idUtente, int idEvento) throws SQLException {
        return eventoDAO.doRetrievePartecipazione(con, idUtente, idEvento);
    }

    public boolean rimuoviPartecipazione(PartecipazioneBean partecipazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return rimuoviPartecipazione(con, partecipazione);
        }
    }
    public boolean rimuoviPartecipazione(Connection con, PartecipazioneBean partecipazione) throws SQLException {
        eventoDAO.doRemovePartecipazione(con, partecipazione);
        return true;
    }

    public void modificaEvento(EventoBean evento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            modificaEvento(con, evento);
        }
    }
    public void modificaEvento(Connection con, EventoBean evento) throws SQLException {
        eventoDAO.DoUpdate(con, evento);
    }

    public void diminuisciPosti(EventoBean evento) throws SQLException {
        evento.setPosti_disponibili(evento.getPosti_disponibili() - 1);
        try (Connection con = ConPool.getConnection()) {
            modificaEvento(con, evento); // Riutilizziamo il metodo interno
        }
    }
    // Overload per testare la logica senza aprire connessioni vere
    public void diminuisciPosti(Connection con, EventoBean evento) throws SQLException {
        evento.setPosti_disponibili(evento.getPosti_disponibili() - 1);
        modificaEvento(con, evento);
    }

    public void aumentaPosti(EventoBean evento) throws SQLException {
        evento.setPosti_disponibili(evento.getPosti_disponibili() + 1);
        try (Connection con = ConPool.getConnection()) {
            modificaEvento(con, evento);
        }
    }
    public void aumentaPosti(Connection con, EventoBean evento) throws SQLException {
        evento.setPosti_disponibili(evento.getPosti_disponibili() + 1);
        modificaEvento(con, evento);
    }

    public void rimuoviEvento(int idEvento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            rimuoviEvento(con, idEvento);
        }
    }
    public void rimuoviEvento(Connection con, int idEvento) throws SQLException {
        EventoBean eventoDaRimuovere = new EventoBean();
        eventoDaRimuovere.setId_evento(idEvento);
        eventoDAO.DoDelete(con, eventoDaRimuovere);
    }

    public List<EventoBean> retrieveAllEventi() throws SQLException {
        return eventoDAO.DoRetrieveAll();
    }

    public List<EventoBean> retrieveEventiUtente(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return eventoDAO.doRetrieveEventiByUtente(con, idUtente);
        }
    }

    public EventoBean retrieveEvento(int idEvento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return retrieveEvento(con, idEvento);
        }
    }
    public EventoBean retrieveEvento(Connection con, int idEvento) throws SQLException {
        return eventoDAO.DoRetrieveEventoById(con, idEvento);
    }
    public GruppoBean retrieveGruppo(int idGruppo) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO gruppoDAO = new GruppoDAO();
            // Il DAO restituirà un'istanza concreta (es. GruppoStudio o GruppoSportivo)
            return gruppoDAO.doRetrieveByid(con, idGruppo);
        }
    }
}