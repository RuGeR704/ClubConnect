package Storage;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.PartecipazioneBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    /*dividiamo le operazioni in
    *   1) hanno bisogno della connessione come parametro perche fanno parte di una transazione e possono creare race condition nel caso passiamo
    * la connesione tramite una classe service perche fa brutto nella servlet
    *   2) aprono e chiudono la connessione nel metodo perche non da problemi ---> metodi che prendono dal db come i retrieve
    * l'architettura a tre livelli (Three-Tier Architecture). È proprio così che si lavora nelle applicazioni Java professionali:
    Servlet (Controller): Riceve i dati dal form web. Non sa nulla di SQL o Transazioni.
    Service (Business Logic): È il "cervello". Apre la connessione, gestisce la transazione (tutto o niente), chiama i vari DAO e chiude la connessione.
    DAO (Data Access): Esegue le singole query SQL usando la connessione che gli passa il Service.*/

    public List<EventoBean> DoRetrieveAll() throws SQLException {
        List<EventoBean> Eventi = new ArrayList<EventoBean>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Evento")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventoBean e = new EventoBean();
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_gruppo(rs.getInt("id_gruppo"));
                    e.setNome(rs.getString("nome"));
                    e.setDescrizione(rs.getString("descrizione"));
                    e.setFoto(rs.getString("foto"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setPosti_disponibili(rs.getInt("posti_disponibili"));
                    e.setCapienza_massima(rs.getInt("capienza_massima"));
                    java.sql.Timestamp data_ora = rs.getTimestamp("data_ora");
                    if(data_ora != null) {
                        e.setData_ora(data_ora.toLocalDateTime());
                    }else {
                        e.setData_ora(null);
                    }
                    Eventi.add(e);
                }
            }
        }
        return Eventi;
    }

    public List<EventoBean> doRetrievebyGroup (Connection con, int idGruppo) throws SQLException {
        List<EventoBean> eventi = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM Evento E WHERE E.id_gruppo = ? ORDER BY E.data_ora")) {
            ps.setInt(1, idGruppo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventoBean e = new EventoBean();
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_gruppo(rs.getInt("id_gruppo"));
                    e.setNome(rs.getString("nome"));
                    e.setDescrizione(rs.getString("descrizione"));
                    e.setFoto(rs.getString("foto"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setPosti_disponibili(rs.getInt("posti_disponibili"));
                    e.setCapienza_massima(rs.getInt("capienza_massima"));
                    java.sql.Timestamp data_ora = rs.getTimestamp("data_ora");
                    if(data_ora != null) {
                        e.setData_ora(data_ora.toLocalDateTime());
                    }else {
                        e.setData_ora(null);
                    }
                    eventi.add(e);
                }
            }
        }
        return eventi;
    }

    public List<EventoBean> doRetrievebyGruppiIscritti (Connection con, int id_utente) throws SQLException {
        List<EventoBean> eventi = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM Evento E JOIN Iscrizione I ON E.id_gruppo = I.id_gruppo WHERE I.id_utente = ? ORDER BY E.data_ora")) {
            ps.setInt(1, id_utente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventoBean e = new EventoBean();
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_gruppo(rs.getInt("id_gruppo"));
                    e.setNome(rs.getString("nome"));
                    e.setDescrizione(rs.getString("descrizione"));
                    e.setFoto(rs.getString("foto"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setPosti_disponibili(rs.getInt("posti_disponibili"));
                    e.setCapienza_massima(rs.getInt("capienza_massima"));
                    java.sql.Timestamp data_ora = rs.getTimestamp("data_ora");
                    if(data_ora != null) {
                        e.setData_ora(data_ora.toLocalDateTime());
                    }else {
                        e.setData_ora(null);
                    }
                    eventi.add(e);
                }
            }
        }
        return eventi;
    }

    public EventoBean DoRetrieveEventoById(Connection con,int id_evento) throws SQLException {
            EventoBean e = null;
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Evento WHERE id_evento= ?")) {
            ps.setInt(1, id_evento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    e = new EventoBean();
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_gruppo(rs.getInt("id_gruppo"));
                    e.setNome(rs.getString("nome"));
                    e.setDescrizione(rs.getString("descrizione"));
                    e.setFoto(rs.getString("foto"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setPosti_disponibili(rs.getInt("posti_disponibili"));
                    e.setCapienza_massima(rs.getInt("capienza_massima"));
                    java.sql.Timestamp data_ora = rs.getTimestamp("data_ora");
                    if (data_ora != null) {
                        e.setData_ora(data_ora.toLocalDateTime());
                    } else {
                        e.setData_ora(null);
                    }
                }
            }
        }
        return e;
    }

    public void DoUpdate(Connection con,EventoBean e) throws SQLException {
            String query = "UPDATE Evento SET id_gruppo=?, nome=?, descrizione=?, foto=?,costo=?, posti_disponibili=?, capienza_massima=?, data_ora=? WHERE id_evento=?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, e.getId_gruppo());
                ps.setString(2, e.getNome());
                ps.setString(3, e.getDescrizione());
                ps.setString(4, e.getFoto());
                ps.setDouble(5, e.getCosto());
                ps.setInt(6, e.getPosti_disponibili());
                ps.setInt(7, e.getCapienza_massima());
                if (e.getData_ora() != null) {
                    ps.setTimestamp(8, java.sql.Timestamp.valueOf(e.getData_ora()));
                } else {
                    ps.setNull(8, java.sql.Types.TIMESTAMP);
                }
                ps.setInt(9, e.getId_evento());
                ps.executeUpdate();
            }
    }
    public void DoDelete(Connection con,EventoBean e) throws SQLException {
        String query = "DELETE FROM Evento WHERE id_evento=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, e.getId_evento());
            ps.executeUpdate();
        }
    }

    public void DoSave(Connection con,EventoBean e) throws SQLException {
        String query = "INSERT INTO Evento (id_gruppo, nome, descrizione, foto,costo, posti_disponibili, capienza_massima, data_ora) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, e.getId_gruppo());
            ps.setString(2, e.getNome());
            ps.setString(3, e.getDescrizione());
            ps.setString(4, e.getFoto());
            ps.setDouble(5, e.getCosto());
            ps.setInt(6, e.getPosti_disponibili());
            ps.setInt(7, e.getCapienza_massima());
            if (e.getData_ora() != null) {
                ps.setTimestamp(8, java.sql.Timestamp.valueOf(e.getData_ora()));
            } else {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }
    }
    //retrieve orario partecipazione
    public PartecipazioneBean DoRetrievePartecipazioneById(Connection con,int id_partecipazione) throws SQLException {
        PartecipazioneBean e = null;
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Partecipazione WHERE id_partecipazione= ?")) {
            ps.setInt(1, id_partecipazione);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    e = new PartecipazioneBean();
                    e.setId_partecipazione(rs.getInt("id_partecipazione"));
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_utente(rs.getInt("id_utente"));
                    java.sql.Timestamp data_registrazione = rs.getTimestamp("data_registrazione");
                    if (data_registrazione != null) {
                        e.setData_registrazione(data_registrazione.toLocalDateTime());
                    } else {
                        e.setData_registrazione(null);
                    }
                }
            }
        }
        return e;
    }

    public List<PartecipazioneBean> DoRetrievePartecipazioneUtente(Connection con, int idUtente) throws SQLException {
        List<PartecipazioneBean> partecipazioni = new ArrayList<PartecipazioneBean>();;
        PartecipazioneBean e = null;
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Partecipazione WHERE id_utente = ?")) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    e = new PartecipazioneBean();
                    e.setId_partecipazione(rs.getInt("id_partecipazione"));
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setId_utente(rs.getInt("id_utente"));
                    java.sql.Timestamp data_registrazione = rs.getTimestamp("data_registrazione");
                    if (data_registrazione != null) {
                        e.setData_registrazione(data_registrazione.toLocalDateTime());
                    } else {
                        e.setData_registrazione(null);
                    }
                    partecipazioni.add(e);
                }
            }
        }
        return partecipazioni;
    }

    public List<EventoBean> doRetrieveEventiByUtente(Connection con, int idUtente) throws SQLException {
        List<EventoBean> eventi = new ArrayList<>();
        String query = "SELECT E.id_evento, E.nome, E.data_ora, E.foto " +
                "FROM Evento E, Partecipazione P " +
                "WHERE E.id_evento = P.id_evento " +
                "AND P.id_utente = ? " +
                "AND E.data_ora >= NOW() " + // Solo eventi futuri
                "ORDER BY E.data_ora ASC LIMIT 5"; // Ordina per data e prendine max 5

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventoBean e = new EventoBean();
                    e.setId_evento(rs.getInt("id_evento"));
                    e.setNome(rs.getString("nome"));
                    e.setFoto(rs.getString("foto"));

                    java.sql.Timestamp ts = rs.getTimestamp("data_ora");
                    if(ts != null) e.setData_ora(ts.toLocalDateTime());

                    eventi.add(e);
                }
            }
        }
        return eventi;
    }

    public void DoSavePartecipazioni(Connection con, PartecipazioneBean e) throws SQLException {
        String queryInsert = "INSERT INTO Partecipazione (id_evento, id_utente, data_registrazione) VALUES (?,?,?)";
        // CORRETTO: posti_disponibili invece di capienza_massima
        String queryUpdate = "UPDATE Evento SET posti_disponibili = posti_disponibili - 1 WHERE id_evento = ?";

        try (PreparedStatement ps = con.prepareStatement(queryInsert);
             PreparedStatement pst = con.prepareStatement(queryUpdate)) {

            ps.setInt(1, e.getId_evento());
            ps.setInt(2, e.getId_utente());
            if (e.getData_registrazione() != null) {
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(e.getData_registrazione()));
            } else {
                ps.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            }
            ps.executeUpdate();

            pst.setInt(1, e.getId_evento());
            pst.executeUpdate();
        }
    }

    public PartecipazioneBean doRetrievePartecipazione(Connection con, int idUtente, int idEvento) throws SQLException {
        String query = "SELECT * FROM Partecipazione WHERE id_utente = ? AND id_evento = ?";
        PartecipazioneBean p = new PartecipazioneBean();

        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, idUtente);
            ps.setInt(2, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setId_partecipazione(rs.getInt("id_partecipazione"));
                    p.setId_evento(rs.getInt("id_evento"));
                    p.setId_utente(rs.getInt("id_utente"));
                    java.sql.Timestamp ts = rs.getTimestamp("data_registrazione");
                    if(ts != null) p.setData_registrazione(ts.toLocalDateTime());
                }
            }
        }
        return p;
    }

    public void doRemovePartecipazione(Connection con, PartecipazioneBean e) throws SQLException {
        // AGGIUNTO: AND id_utente = ?
        String query = "DELETE FROM Partecipazione WHERE id_evento = ? AND id_utente = ?";

        try (PreparedStatement ps = con.prepareStatement(query)){
            // Cancella partecipazione
            ps.setInt(1, e.getId_evento());
            ps.setInt(2, e.getId_utente());
            ps.executeUpdate();

        }
    }

    public boolean isUtentePartecipante(Connection con, int idUtente, int idEvento) throws SQLException {
        String query = "SELECT 1 FROM Partecipazione WHERE id_utente = ? AND id_evento = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Torna TRUE se è già iscritto
            }
        }
    }
}
