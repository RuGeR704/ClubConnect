package Application.GestioneEventi;

import Storage.ConPool;
import Storage.EventoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GestioneEventiBean {
    // per creare un evento
    public void creaEvento(EventoBean evento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            dao.DoSave(con, evento);
        }
    }
    // per registrare una partecipazione
    public void registraPartecipazione(PartecipazioneBean partecipazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            // Chiama il metodo nel codice del DAO
            dao.DoSavePartecipazioni(con, partecipazione);
        }
    }
    //per modificare un evento
    public void modificaEvento(EventoBean evento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            // Chiama il DoUpdate del DAO
            dao.DoUpdate(con, evento);
        }
    }
    /**
     * Metodo per diminuire di 1 i posti disponibili di un evento
     */
    public void diminuisciPosti(EventoBean evento) throws SQLException {
        // 1. Usiamo il metodo getPosti_disponibili()
        int postiAttuali = evento.getPosti_disponibili();

        // 2. Scaliamo di 1
        evento.setPosti_disponibili(postiAttuali - 1);

        // 3. Aggiorniamo il database usando il DAO
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            dao.DoUpdate(con, evento); // Questo salva il nuovo numero nel DB
        }
    }
    //per eliminare un evento
    public void rimuoviEvento(int idEvento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();

            // Il DAO vuole un oggetto EventoBean per fare la delete, non solo l'int
            EventoBean eventoDaRimuovere = new EventoBean();
            eventoDaRimuovere.setId_evento(idEvento);

            dao.DoDelete(con, eventoDaRimuovere);
        }
    }
    /**
     * Recupera tutti gli eventi per il calendario.
     * Il DAO DoRetrieveAll apre la connessione internamente, quindi qui la chiamata è diretta.
     */
    public List<EventoBean> retrieveAllEventi() throws SQLException {
        EventoDAO dao = new EventoDAO();
        return dao.DoRetrieveAll();
    }

    /**
     * Recupera un singolo evento per la pagina di dettaglio.
     * Il DAO DoRetrieveEventoById richiede la connessione, quindi la apriamo qui.
     */
    public EventoBean retrieveEvento(int idEvento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            return dao.DoRetrieveEventoById(con, idEvento);
        }
    }
}
