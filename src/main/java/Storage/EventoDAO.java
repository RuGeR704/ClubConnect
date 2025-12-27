package Storage;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneEventi.EventoBean;

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

    public EventoBean DoRetrieveById(Connection con,int id_evento) throws SQLException {
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
}
