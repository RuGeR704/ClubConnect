package Storage;

import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GruppoDAO {
    public Collection<GruppoBean> doRetrieveAll(Connection con) throws SQLException {
        Collection<GruppoBean> gruppi = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM Gruppo")){
             try(ResultSet rs = ps.executeQuery()) {
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

    public GruppoBean doRetrieveByid(Connection con,int id_gruppo) throws SQLException {
        GruppoBean c=null;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM Gruppo WHERE id_gruppo = ?")) {
            ps.setInt(1, id_gruppo);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                }
            }
        }
        return c;
    }

    public void doUpdate(Connection con, GruppoBean gruppo) throws SQLException {
        String sql = "UPDATE Gruppo SET nome=?, descrizione=?, logo=?, sede=?, settore=?, regole=?, slogan=?, stato=?, tipoGruppo=?, importo_retta=?, frequenza=? WHERE id_gruppo=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gruppo.getNome());
            ps.setString(2, gruppo.getDescrizione());
            ps.setString(3, gruppo.getLogo());
            ps.setString(4, gruppo.getSede());
            ps.setString(5, gruppo.getSettore());
            ps.setString(6, gruppo.getRegole());
            ps.setString(7, gruppo.getSlogan());
            ps.setBoolean(8, gruppo.isStato());
            ps.setBoolean(9, gruppo.isTipoGruppo());
            if (gruppo instanceof ClubBean) {
                ClubBean c = (ClubBean) gruppo;
                ps.setDouble(10, c.getImporto_retta());
                ps.setInt(11, c.getFrequenza());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
                ps.setNull(11, java.sql.Types.INTEGER);
            }
            ps.setInt(12, gruppo.getId_gruppo());
            ps.executeUpdate();
        }
    }

    public void doDelete(Connection con, int id) throws SQLException {
        String sql = "DELETE FROM Gruppo WHERE id_gruppo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void doSave(Connection con, GruppoBean gruppo, int idUtente) throws SQLException {
        String query = "INSERT INTO Gruppo(nome,descrizione,logo,sede,settore,regole,slogan,stato,tipoGruppo,importo_retta,frequenza) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        try(PreparedStatement ps = con.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gruppo.getNome());
                ps.setString(2, gruppo.getDescrizione());
                ps.setString(3, gruppo.getLogo());
                ps.setString(4, gruppo.getSede());
                ps.setString(5, gruppo.getSettore());
                ps.setString(6, gruppo.getRegole());
                ps.setString(7, gruppo.getSlogan());
                ps.setBoolean(8, gruppo.isStato());
                ps.setBoolean(9, gruppo.isTipoGruppo());
            if (gruppo instanceof ClubBean) {
                ClubBean c = (ClubBean) gruppo;
                ps.setDouble(10,c.getImporto_retta());
                ps.setInt(11, c.getFrequenza());
            }
            if(gruppo instanceof AssociazioneBean) {
                ps.setNull(10,java.sql.Types.DOUBLE);
                ps.setNull(11,java.sql.Types.INTEGER);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerato = rs.getInt(1);

                    // Ora l'ID esiste (es. 5), quindi possiamo iscrivere l'utente!
                    UtenteDAO utenteDAO = new UtenteDAO();
                    utenteDAO.doSubscribeAdminGroup(con, idUtente, idGenerato);

                    // Aggiorniamo il bean (opzionale, ma buona prassi)
                    gruppo.setId_gruppo(idGenerato);

                }
            }
        }

    }
    /**
     * Conta il numero di utenti iscritti a un determinato gruppo.
     * @param con La connessione al database.
     * @param idGruppo L'ID del gruppo di cui contare i membri.
     * @return Il numero di iscritti.
     * @throws SQLException In caso di errore SQL.
     */
    public int contaMembri(Connection con, int idGruppo) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Iscrizione WHERE id_gruppo = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }

    /**
     * Conta il numero di eventi futuri (in programma) per un gruppo.
     * @param con La connessione al database.
     * @param idGruppo L'ID del gruppo.
     * @return Il numero di eventi con data futura.
     * @throws SQLException In caso di errore SQL.
     */
    public int contaEventiInProgramma(Connection con, int idGruppo) throws SQLException {
        int count = 0;
        // Utilizziamo NOW() per prendere solo quelli futuri
        String query = "SELECT COUNT(*) FROM Evento WHERE id_gruppo = ? AND data_ora > NOW()";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }
    //Verifica sul DB se un utente è Gestore di un determinato gruppo.

    public boolean isGestore(Connection con, int idUtente, int idGruppo) throws SQLException {
        // Assumo che la tabella Iscrizione abbia una colonna 'Gestore' (booleana/tinyint)
        String query = "SELECT Gestore FROM Iscrizione WHERE id_utente = ? AND id_gruppo = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Restituisce true se il campo Gestore è 1/true
                    return rs.getBoolean("Gestore");
                }
            }
        }
        return false; // Se non c'è iscrizione o non è gestore
    }
    // Recupera la lista completa degli utenti iscritti a un gruppo (Serve per la VisualizzaSociServlet)

    public ArrayList<Application.GestioneAccount.UtenteBean> doRetrieveSoci(Connection con, int idGruppo) throws SQLException {
        ArrayList<Application.GestioneAccount.UtenteBean> soci = new ArrayList<>();
        // Esegue una JOIN tra Utente e Iscrizione per trovare chi è iscritto a quel gruppo
        String query = "SELECT u.* FROM Utente u JOIN Iscrizione i ON u.id_utente = i.id_utente WHERE i.id_gruppo = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idGruppo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Application.GestioneAccount.UtenteBean utente = new Application.GestioneAccount.UtenteBean();
                    utente.setId_utente(rs.getInt("id_utente"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));
                    utente.setEmail(rs.getString("email"));
                    utente.setCellulare(rs.getString("cellulare")); // Assumendo che ci sia nel DB
                    // Aggiungi altri campi se necessario
                    soci.add(utente);
                }
            }
        }
        return soci;
    }
}
