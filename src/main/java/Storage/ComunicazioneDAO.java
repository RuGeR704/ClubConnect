package Storage;

import Application.GestioneComunicazioni.ComunicazioniBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComunicazioneDAO {

    public List<ComunicazioniBean> doRetrieveAll() throws SQLException {
        List<ComunicazioniBean> comunicazioni = new ArrayList<ComunicazioniBean>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Comunicazioni")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComunicazioniBean c = new ComunicazioniBean();
                    c.setId_comunicazione(rs.getInt("id_comunicazione"));
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setId_autore(rs.getInt("id_autore"));
                    c.setContenuto(rs.getString("contenuto"));
                    c.setFoto(rs.getString("foto"));
                    c.setDataPubblicazione(rs.getDate("data_pubblicazione"));
                    c.setIsglobal(rs.getBoolean("isglobal"));
                    c.setTitolo(rs.getString("titolo"));
                    comunicazioni.add(c);
                }
            }
        }
        return comunicazioni;
    }

    public List<ComunicazioniBean> doRetrievebyGroup (Connection con, int id_utente) throws SQLException {
        List<ComunicazioniBean> comunicazioni = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM Comunicazione C JOIN Iscrizione I ON C.id_gruppo = I.id_gruppo WHERE I.id_utente = ? ORDER BY C.data_pubblicazione DESC")) {
            ps.setInt(1, id_utente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComunicazioniBean c = new ComunicazioniBean();
                    c.setId_comunicazione(rs.getInt("id_comunicazione"));
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setId_autore(rs.getInt("id_autore"));
                    c.setContenuto(rs.getString("contenuto"));
                    c.setFoto(rs.getString("foto"));
                    c.setDataPubblicazione(rs.getDate("data_pubblicazione"));
                    c.setIsglobal(rs.getBoolean("isglobal"));
                    comunicazioni.add(c);
                }
            }
        }
        return comunicazioni;
    }


    public ComunicazioniBean doRetrieveById(Connection con,int id) throws SQLException {
        ComunicazioniBean c = new ComunicazioniBean();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM Comunicazioni WHERE id_comunicazione = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    c = new ComunicazioniBean();
                    c.setId_comunicazione(rs.getInt("id_comunicazione"));
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setId_autore(rs.getInt("id_autore"));
                    c.setContenuto(rs.getString("contenuto"));
                    c.setFoto(rs.getString("foto"));
                    c.setDataPubblicazione(rs.getDate("data_pubblicazione"));
                    c.setIsglobal(rs.getBoolean("isglobal"));
                    c.setTitolo(rs.getString("titolo"));
                }
            }
        }
        return c;
    }

    public void doUpdate (Connection con,ComunicazioniBean c) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE Comunicazione SET id_gruppo = ?, id_autore = ?, contenuto = ?, foto = ?, data_pubblicazione = ?, titolo = ? WHERE id_comunicazione = ?")) {
            ps.setInt(1, c.getId_gruppo());
            ps.setInt(2, c.getId_autore());
            ps.setString(3, c.getContenuto());
            ps.setString(4, c.getFoto());
            ps.setDate(5, c.getDataPubblicazione());
            ps.setString(6, c.getTitolo()); //Nella query titolo è il 6 punto interrogativo e id_comunicazione è il 7
            ps.setInt(7, c.getId_comunicazione()); //Quindi li ho invertiti
            ps.executeUpdate();
        }
    }

    public void doDelete (Connection con,ComunicazioniBean c) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM Comunicazioni WHERE id_comunicazione = ?")) {
            ps.setInt(1, c.getId_comunicazione());
            ps.executeUpdate();
        }
    }

    public void doSave(Connection con,ComunicazioniBean c) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Comunicazione (id_gruppo, id_autore, contenuto, foto, data_pubblicazione, is_global, titolo) VALUES (?, ?, ?, ?, ?, ?, ?)")){
            ps.setInt(1, c.getId_gruppo());
            ps.setInt(2, c.getId_autore());
            ps.setString(3, c.getContenuto());
            ps.setString(4, c.getFoto());
            ps.setDate(5, c.getDataPubblicazione());
            ps.setBoolean(6, c.isIsglobal());
            ps.setString(7, c.getTitolo());
            ps.executeUpdate();
        }
    }

}