package Application.GestioneEventi;

import Storage.ConPool;
import Storage.EventoDAO;
import java.sql.Connection;
import java.sql.SQLException;

public class GestioneEventiBean {

    public void creaEvento(EventoBean evento) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            dao.DoSave(con, evento);
        }
    }
    public void registraPartecipazione(PartecipazioneBean partecipazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            EventoDAO dao = new EventoDAO();
            // Chiama il metodo che ho visto nel codice del DAO che mi hai mandato
            dao.DoSavePartecipazioni(con, partecipazione);
        }
    }
}
