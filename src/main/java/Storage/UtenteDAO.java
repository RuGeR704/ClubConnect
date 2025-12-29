package Storage;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {
    public List<UtenteBean> doRetrieveAll() throws SQLException {
        List<UtenteBean> utenti = new ArrayList<>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Utente")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UtenteBean c = new UtenteBean();
                    c.setId_utente(rs.getInt("id_utente"));
                    c.setUsername(rs.getString("Username"));
                    c.setNome(rs.getString("nome"));
                    c.setCognome(rs.getString("cognome"));
                    c.setEmail(rs.getString("email"));
                    c.setData_nascita(rs.getDate("data_nascita").toLocalDate());
                    c.setCellulare(rs.getString("cellulare"));
                    c.setStato(rs.getInt("stato"));
                    c.setIsadmin(rs.getBoolean("isAdmin") || c.getId_utente() == 1);
                    utenti.add(c);
                }
            }
        }
        return utenti;
    }

    public UtenteBean doRetrieveById(Connection con,int id) throws SQLException{
        UtenteBean c = null;
        try(PreparedStatement ps = con.prepareStatement("SELECT * FROM Utente WHERE id_utente = ?")){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    c = new UtenteBean();
                    c.setId_utente(rs.getInt("id_utente"));
                    c.setUsername(rs.getString("username"));
                    c.setNome(rs.getString("nome"));
                    c.setCognome(rs.getString("cognome"));
                    c.setEmail(rs.getString("email"));
                    c.setData_nascita(rs.getDate("data_nascita").toLocalDate());
                    c.setCellulare(rs.getString("cellulare"));
                    c.setStato(rs.getInt("stato"));
                    c.setIsadmin(rs.getBoolean("isAdmin") || c.getId_utente() == 1);;
                }
            }
        }
        return c;
    }

    public void doUpdate(Connection con,UtenteBean p) throws SQLException{
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE Utente SET username = ?, passwordhash = SHA1(?), nome = ?, cognome = ?, email = ?, data_nascita = ?, cellulare = ?, stato = ?, is_admin = ? WHERE id_utente = ?")) {
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getPasswordhash());
            ps.setString(3, p.getNome());
            ps.setString(4, p.getCognome());
            ps.setString(5, p.getEmail());
            ps.setDate(6, java.sql.Date.valueOf(p.getData_nascita()));
            ps.setString(7, p.getCellulare());
            ps.setInt(8, p.getStato());
            ps.setBoolean(9, p.isAdmin());
            ps.setInt(10, p.getId_utente());
            ps.executeUpdate();
        }
    }

    public void doDelete(Connection con,int id)throws SQLException{
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Utente WHERE id_utente = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void doSave(Connection con, UtenteBean p) throws SQLException{
        try(PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Utente (username, passwordhash, nome, cognome, email, data_nascita, cellulare, stato, is_admin) VALUES (?, ?, ?, ?, ?, ? ,?, ?, ?)")) {
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getPasswordhash());
            ps.setString(3, p.getNome());
            ps.setString(4, p.getCognome());
            ps.setString(5, p.getEmail());
            ps.setDate(6, java.sql.Date.valueOf(p.getData_nascita()));
            ps.setString(7, p.getCellulare());
            ps.setInt(8, p.getStato());
            ps.setBoolean(9, p.isAdmin());
            ps.executeUpdate();
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062) { //se trova duplicati email o username
                String messaggioErrore = e.getMessage();

                if (messaggioErrore.contains("email")) {
                    throw new SQLException("L'email inserita è già stata utilizzata");
                } else if (messaggioErrore.contains("username")) {
                    throw new SQLException("L'username inserito non è disponibile");
                } else {
                    throw new SQLException("Dati duplicati (Email o username già esistenti)");
                }
            }
            throw e;
        }
    }

    public UtenteBean DoRetrieveEmailPassword(Connection con, String email,String password) throws SQLException{
        UtenteBean c = null;
        try (PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Utente WHERE email = ? AND passwordhash = SHA1(?)")) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new UtenteBean();
                    c.setId_utente(rs.getInt("id_utente"));
                    c.setUsername(rs.getString("Username"));
                    c.setNome(rs.getString("nome"));
                    c.setCognome(rs.getString("cognome"));
                    c.setEmail(rs.getString("email"));
                    c.setPasswordhash(rs.getString("passwordhash"));
                    c.setData_nascita(rs.getDate("data_nascita").toLocalDate());
                    c.setCellulare(rs.getString("cellulare"));
                    c.setStato(rs.getInt("stato"));
                    c.setIsadmin(rs.getBoolean("is_admin") || c.getId_utente() == 1);
                }
            }
        }
        return c;
    }
    public List<GruppoBean> doRetrieveGruppi( int id_utente) throws SQLException{
        List<GruppoBean> gruppi = new ArrayList<>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Gruppo.*  FROM Iscrizione JOIN Gruppo ON Iscrizione.id_gruppo=Gruppo.id_gruppo WHERE id_utente=? ")) {
            ps.setInt(1, id_utente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GruppoBean c;
                    boolean tipo=rs.getBoolean("tipoGruppo");
                    if(tipo){
                        ClubBean club= new ClubBean();
                        club.setImporto_retta(rs.getDouble("importo_retta"));
                        club.setFrequenza(rs.getInt("frequenza"));
                        c=club;
                    }else{
                        c = new AssociazioneBean();
                    }
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setNome(rs.getString("nome"));
                    c.setDescrizione(rs.getString("descrizione"));
                    c.setLogo(rs.getString("logo"));
                    c.setSede(rs.getString("sede"));
                    c.setSettore(rs.getString("settore"));
                    c.setRegole(rs.getString("regole"));
                    c.setSlogan(rs.getString("slogan"));
                    c.setStato(rs.getBoolean("stato"));
                    c.setTipoGruppo(tipo);
                    gruppi.add(c);
                }
            }
        }
        return gruppi;
    }
}