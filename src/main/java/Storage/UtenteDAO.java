package Storage;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;

import java.sql.*;
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
    public List<GruppoBean> doRetrieveGruppiIscritto( int id_utente) throws SQLException{
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

    public List<MetodoPagamentoBean> doRetrieveAllMetodiPagamento(int id_utente) throws SQLException{
        List<MetodoPagamentoBean> metodi = new ArrayList<MetodoPagamentoBean>();
        try(Connection con = ConPool.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT Metodo_Pagamento.* FROM Metodo_Pagamento JOIN Utente ON Metodo_Pagamento.id_utente=Utente.id_utente WHERE Utente.id_utente=?")) {
            ps.setInt(1, id_utente);
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
    public List<ClubBean> doRetrieveClubIscritto( int id_utente) throws SQLException{
        List<ClubBean> gruppi = new ArrayList<>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Gruppo.importo_retta,Gruppo.frequenza,Gruppo.id_gruppo   FROM Iscrizione JOIN Gruppo ON Iscrizione.id_gruppo=Gruppo.id_gruppo WHERE id_utente=? AND Gruppo.tipoGruppo=true ")) {
            ps.setInt(1, id_utente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClubBean c;
                        ClubBean club= new ClubBean();
                        club.setImporto_retta(rs.getDouble("importo_retta"));
                        club.setFrequenza(rs.getInt("frequenza"));
                        club.setId_gruppo(rs.getInt("id_gruppo"));
                        c=club;
                    gruppi.add(c);
                }
            }
        }
        return gruppi;
    }

    public List<GruppoBean> doRetrieveGruppiAdmin (Connection con, int idUtente) throws SQLException {
        List<GruppoBean> gruppiAdmin = new ArrayList<>();

        String query = "SELECT g.* FROM Gruppo g " +
                "JOIN Iscrizione i ON g.id_gruppo = i.id_gruppo " +
                "WHERE i.id_utente = ? AND i.gestore = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setBoolean(2, true); // Cerchiamo solo dove è Admin

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GruppoBean bean;

                    // 1. Gestione Polimorfismo (Club vs Associazione)
                    boolean tipo = rs.getBoolean("tipoGruppo"); // true = Club, false = Associazione

                    if (tipo) {
                        ClubBean club = new ClubBean();
                        // Assicurati che i nomi delle colonne corrispondano al tuo DB
                        club.setImporto_retta(rs.getDouble("importo_retta"));
                        club.setFrequenza(rs.getInt("frequenza")); // Usa getInt se nel DB è intero (0,1,2)
                        bean = club;
                    } else {
                        bean = new AssociazioneBean();
                    }

                    // 2. Popolamento Dati Comuni
                    bean.setId_gruppo(rs.getInt("id_gruppo"));
                    bean.setNome(rs.getString("nome"));
                    bean.setDescrizione(rs.getString("descrizione"));
                    bean.setLogo(rs.getString("logo"));
                    bean.setSede(rs.getString("sede"));
                    bean.setSettore(rs.getString("settore"));
                    bean.setRegole(rs.getString("regole"));
                    bean.setSlogan(rs.getString("slogan"));
                    bean.setStato(rs.getBoolean("stato"));
                    bean.setTipoGruppo(tipo);

                    // Aggiunta alla lista
                    gruppiAdmin.add(bean);
                }
            }
        }
        return gruppiAdmin;
    }

    public List<DettagliPagamentoBean> doRetrievePagamenti(int id_utente) throws SQLException{
        List<DettagliPagamentoBean> DettagliPagamenti = new ArrayList<>();
        try (Connection con = ConPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT Pagamento.*  FROM Metodo_Pagamento JOIN Pagamento ON Metodo_Pagamento.id_metodo=Pagamento.id_metodo WHERE Metodo_Pagamento.id_utente=?")) {
            ps.setInt(1, id_utente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DettagliPagamentoBean c= new DettagliPagamentoBean();
                    c.setId_pagamento(rs.getInt("id_pagamento"));
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setId_metodo(rs.getInt("id_metodo"));
                    c.setImporto(rs.getDouble("importo"));
                    java.sql.Timestamp data_transazione = rs.getTimestamp("data_transazione");
                    if (data_transazione != null) {
                        c.setData_tansazione(data_transazione.toLocalDateTime());
                    } else {
                        c.setData_tansazione(null);
                    }
                    DettagliPagamenti.add(c);
                }
            }
        }
        return DettagliPagamenti;
    }

    public void doSubscribe(Connection con, int idUtente, int idGruppo) throws SQLException {
        String query = "INSERT INTO Iscrizione (id_utente, id_gruppo, data_iscrizione) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idGruppo);

            // Imposta la data/ora corrente
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            // Esegue l'inserimento
            ps.executeUpdate();
        }
    }

    public void doSubscribeAdminGroup(Connection con, int idUtente, int idGruppo) throws SQLException {
        String query = "INSERT INTO Iscrizione (id_utente, id_gruppo, data_iscrizione, gestore) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idGruppo);

            // Imposta la data/ora corrente
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setBoolean(4, true);

            // Esegue l'inserimento
            ps.executeUpdate();
        }
    }

    public void doUnsubscribe (Connection con, int idUtente, int idGruppo) throws SQLException {
        String query = "DELETE FROM Iscrizione WHERE id_utente = ? AND id_gruppo = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idGruppo);
            ps.executeUpdate();
        }
    }

    public List<GruppoBean> doRetrieveGruppiNonIscritto(Connection con, int idUtente) throws SQLException {
        List<GruppoBean> gruppi = new ArrayList<>();

        String query = "SELECT * FROM Gruppo WHERE id_gruppo NOT IN " +
                "(SELECT id_gruppo FROM Iscrizione WHERE id_utente = ?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GruppoBean bean;
                    boolean tipo = rs.getBoolean("tipoGruppo");

                    if (tipo) { // Club
                        ClubBean club = new ClubBean();
                        club.setImporto_retta(rs.getDouble("importo_retta"));
                        club.setFrequenza(rs.getInt("frequenza"));
                        bean = club;
                    } else { // Associazione
                        bean = new AssociazioneBean();
                    }

                    bean.setId_gruppo(rs.getInt("id_gruppo"));
                    bean.setNome(rs.getString("nome"));
                    bean.setDescrizione(rs.getString("descrizione"));
                    bean.setLogo(rs.getString("logo"));
                    bean.setSede(rs.getString("sede"));
                    bean.setSettore(rs.getString("settore"));
                    bean.setSlogan(rs.getString("slogan"));
                    bean.setTipoGruppo(tipo); // Importante!

                    gruppi.add(bean);
                }
            }
        }
        return gruppi;
    }
}