package Storage;

import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {
    //Metodi di pagamento
    public List<MetodoPagamentoBean> doRetrieveAllMetodiPagamento() throws SQLException{
        List<MetodoPagamentoBean> metodi = new ArrayList<MetodoPagamentoBean>();
        try(Connection con = ConPool.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM Metodo_Pagamento")) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    MetodoPagamentoBean m = new MetodoPagamentoBean();
                    m.setId_metodo(rs.getInt("id_metodo"));
                    m.setId_utente(rs.getInt("id_utente"));
                    m.setNome_intestatario(rs.getString("nome_intestatario"));
                    m.setCognome_intestatario(rs.getString("cognome_intestatario"));
                    m.setNumero_carta(rs.getString("numero_carta"));
                    m.setScadenza_carta(rs.getString("scadenza_carta"));
                    metodi.add(m);
                }
            }
        }
        return metodi;
    }

    public MetodoPagamentoBean doRetrieveMetodoPagamentoById(int id_metodo) throws SQLException {
        MetodoPagamentoBean m = null;
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Metodo_Pagamento WHERE id_metodo = ?")) {
            ps.setInt(1, id_metodo);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    m = new MetodoPagamentoBean();
                    m.setId_metodo(rs.getInt("id_metodo"));
                    m.setId_utente(rs.getInt("id_utente"));
                    m.setNome_intestatario(rs.getString("nome_intestatario"));
                    m.setCognome_intestatario(rs.getString("cognome_intestatario"));
                    m.setNumero_carta(rs.getString("numero_carta"));
                    m.setScadenza_carta(rs.getString("scadenza_carta"));
                }
            }
        }
        return m;
    }

    public void doUpdateMetodoPagamento(Connection con, MetodoPagamentoBean m) throws SQLException{
        String query =  "UPDATE Metodo_Pagamento SET id_utente = ?, nome_intestatario = ?, cognome_intestatario = ?, numero_carta = ?, scadenza_carta = ? WHERE id_metodo = ?";
        try (PreparedStatement ps = con.prepareStatement(query)){
// L'ordine dei ? è: 1:id_utente, 2:nome, 3:cognome, 4:numero, 5:scadenza, 6:WHERE id_metodo
            ps.setInt(1, m.getId_utente()); // Prima era m.getId_metodo()
            ps.setString(2, m.getNome_intestatario());
            ps.setString(3, m.getCognome_intestatario());
            ps.setString(4, m.getNumero_carta());
            ps.setString(5, m.getScadenza_carta()); // Prima era ripetuto il 5 su indice 4
            ps.setInt(6, m.getId_metodo()); // Prima era m.getId_utente()
            ps.executeUpdate();
        }
    }

    public void doDeleteMetodoPagamento(Connection con, int id_metodo, int id_utente) throws SQLException{
        String query = "DELETE FROM Metodo_pagamento WHERE id_metodo = ? AND id_utente=?";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, id_metodo);
            ps.executeUpdate();
        }
    }

    public void doSaveMetodoPagamento(Connection con, MetodoPagamentoBean m) throws SQLException{
        String query = "INSERT INTO Metodo_Pagamento (id_utente, nome_intestatario, cognome_intestatario, numero_carta, scadenza_carta) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, m.getId_utente());
            ps.setString(2, m.getNome_intestatario());
            ps.setString(3, m.getCognome_intestatario());
            ps.setString(4, m.getNumero_carta());
            ps.setString(5, m.getScadenza_carta());
            ps.executeUpdate();
        }
    }

    //Dettagli pagamento
    public List<DettagliPagamentoBean> doRetrieveAllDettagliPagamento() throws SQLException{
        List<DettagliPagamentoBean> pagamenti = new ArrayList<DettagliPagamentoBean>();
        DettagliPagamentoBean d = null;
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Pagamento")){
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    d = new DettagliPagamentoBean();
                    d.setId_pagamento(rs.getInt("id_pagamento"));
                    d.setId_gruppo(rs.getInt("id_gruppo"));
                    d.setId_metodo(rs.getInt("id_metodo"));
                    d.setImporto(rs.getDouble("importo"));
                    java.sql.Timestamp data_transazione = rs.getTimestamp("data_transazione");
                    if(data_transazione != null){
                        d.setData_tansazione(data_transazione.toLocalDateTime());
                    } else {
                        d.setData_tansazione(null);
                    }
                    pagamenti.add(d);
                }
            }

        }
        return pagamenti;
    }

    public DettagliPagamentoBean doRetrieveDettagliPagamentoById(int id_pagamento) throws SQLException{
        DettagliPagamentoBean d = null;
        try(Connection con = ConPool.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Pagamento WHERE id_pagamento = ?")){
            ps.setInt(1, id_pagamento);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    d.setId_pagamento(rs.getInt("id_pagamento"));
                    d.setId_gruppo(rs.getInt("id_gruppo"));
                    d.setId_metodo(rs.getInt("id_metodo"));
                    d.setImporto(rs.getDouble("importo"));
                    java.sql.Timestamp data_transazione = rs.getTimestamp("data_transazione");
                    if(data_transazione != null){
                        d.setData_tansazione(data_transazione.toLocalDateTime());
                    } else {
                        d.setData_tansazione(null);
                    }
                }
            }
        }
        return d;
    }

    public void doSaveDettagliPagamento(Connection con, DettagliPagamentoBean d) throws SQLException{
        String query = "INSERT INTO Pagamento (id_gruppo, id_metodo, importo, data_transazione) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, d.getId_gruppo());
            ps.setInt(2, d.getId_metodo());
            ps.setDouble(3, d.getImporto());
            if(d.getData_tansazione() != null){
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(d.getData_tansazione()));
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }
    }
    /**
     * Verifica se esiste un pagamento effettuato da un utente per un determinato gruppo
     * la cui validità (basata sulla frequenza del club) non sia ancora scaduta.
     */
    public boolean isAbbonamentoValido(Connection con, int idUtente, int idGruppo, int giorniValidita) throws SQLException {
        // Seleziona l'ultimo pagamento effettuato dall'utente per quel gruppo specifico
        String query = "SELECT p.data_transazione FROM Pagamento p " +
                "JOIN Metodo_Pagamento mp ON p.id_metodo = mp.id_metodo " +
                "WHERE mp.id_utente = ? AND p.id_gruppo = ? " +
                "ORDER BY p.data_transazione DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp dataTransazione = rs.getTimestamp("data_transazione");
                    if (dataTransazione != null) {
                        // Calcola la scadenza: millisecondi della transazione + (giorni di validità in ms)
                        long scadenzaMillis = dataTransazione.getTime() + ((long) giorniValidita * 24 * 60 * 60 * 1000);
                        // L'abbonamento è valido se la scadenza è successiva all'istante attuale
                        return scadenzaMillis > System.currentTimeMillis();
                    }
                }
            }
        }
        return false; // Nessun pagamento trovato o abbonamento scaduto
    }
}