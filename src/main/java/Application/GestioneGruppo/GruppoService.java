package Application.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.UtenteDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GruppoService {

    private GruppoDAO gruppoDAO;
    private UtenteDAO utenteDAO;

    public GruppoService() {
        this.gruppoDAO = new GruppoDAO();
        this.utenteDAO = new UtenteDAO();
    }

    // Setters per i Test
    public void setGruppoDAO(GruppoDAO dao) { this.gruppoDAO = dao; }
    public void setUtenteDAO(UtenteDAO dao) { this.utenteDAO = dao; }

    // --- METODI ---

    // 1. RECUPERO GRUPPO (Alias per la Servlet)
    public GruppoBean visualizzaGruppo(int idGruppo) throws SQLException {
        return recuperaGruppo(idGruppo);
    }

    public GruppoBean recuperaGruppo(int idGruppo) throws SQLException{
        try (Connection con = ConPool.getConnection()) {
            return recuperaGruppo(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Overload per Test
    public GruppoBean recuperaGruppo(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.doRetrieveByid(con, idGruppo);
    }

    // 2. RECUPERO SOCI
    public List<UtenteBean> recuperaSociDelGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return recuperaSociDelGruppo(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<UtenteBean> recuperaSociDelGruppo(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.doRetrieveSoci(con, idGruppo);
    }

    // 3. ISCRIZIONE
    public void iscriviUtenteAlGruppo(int idUtente, int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            iscriviUtenteAlGruppo(con, idUtente, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void iscriviUtenteAlGruppo(Connection con, int idUtente, int idGruppo) throws SQLException {
        gruppoDAO.doIscrizione(con, idUtente, idGruppo);
    }

    // 4. ESPELLERE UTENTE
    public boolean espelliUtente(int idGruppo, int idUtenteDaEspellere, int idRichiedente) {
        try (Connection con = ConPool.getConnection()) {
            return espelliUtente(con, idGruppo, idUtenteDaEspellere, idRichiedente);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean espelliUtente(Connection con, int idGruppo, int idUtenteDaEspellere, int idRichiedente) throws SQLException {
        boolean isGestore = gruppoDAO.isGestore(con, idRichiedente, idGruppo);

        if (!isGestore) return false;
        if (idRichiedente == idUtenteDaEspellere) return false;

        return gruppoDAO.doRimuoviMembro(con, idGruppo, idUtenteDaEspellere);
    }

    // 5. RUOLI
    public Map<Integer, Boolean> recuperaMappaRuoli(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return recuperaMappaRuoli(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    public Map<Integer, Boolean> recuperaMappaRuoli(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.getRuoliIscritti(con, idGruppo);
    }

    // 6. CONTA MEMBRI
    public int getNumeroMembri(int idGruppo) {
        return contaMembri(idGruppo);
    }
    public int contaMembri(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return contaMembri(con, idGruppo);
        } catch (SQLException e) { return 0; }
    }
    public int contaMembri(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.contaMembri(con, idGruppo);
    }

    // 7. CONTA EVENTI
    public int getNumeroEventiProgrammati(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return getNumeroEventiProgrammati(con, idGruppo);
        } catch (SQLException e) {
            return 0;
        }
    }
    // Overload aggiunto per i test
    public int getNumeroEventiProgrammati(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.contaEventiInProgramma(con, idGruppo);
    }

    // 8. CREA GRUPPO
    public void creaGruppo(GruppoBean g, int idUtente) throws SQLException {
        try(Connection con = ConPool.getConnection()){
            creaGruppo(con, g, idUtente);
        }
    }
    public void creaGruppo(Connection con, GruppoBean g, int idUtente) throws SQLException {
        gruppoDAO.doSave(con, g, idUtente);
    }

    // 9. RECUPERA GRUPPI NON ISCRITTO (Per EsploraGruppiServlet)
    public List<GruppoBean> recuperaGruppiNonIscritto(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return recuperaGruppiNonIscritto(con, idUtente);
        }
    }
    // Overload aggiunto
    public List<GruppoBean> recuperaGruppiNonIscritto(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveGruppiNonIscritto(con, idUtente);
    }

    // 10. ISCRITTO (Per VisualizzaGruppoServlet)
    public boolean isUtenteIscritto(int idGruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return isUtenteIscritto(con, idGruppo, idUtente);
        }
    }
    // Overload aggiunto per Mockito
    public boolean isUtenteIscritto(Connection con, int idGruppo, int idUtente) throws SQLException {
        List<GruppoBean> iscritti = utenteDAO.doRetrieveGruppiIscritto(con, idUtente);
        for (GruppoBean g : iscritti) {
            if (g.getId_gruppo() == idGruppo) return true;
        }
        return false;
    }

    // 11. GESTORE (Per VisualizzaGruppoServlet)
    public boolean isUtenteGestore(int idGruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return isUtenteGestore(con, idGruppo, idUtente);
        }
    }
    // Overload aggiunto per Mockito
    public boolean isUtenteGestore(Connection con, int idGruppo, int idUtente) throws SQLException {
        return gruppoDAO.isGestore(con, idUtente, idGruppo);
    }

    // 12. RIMUOVI MEMBRO
    public boolean rimuoviMembro(int idGruppo, int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            return rimuoviMembro(con, idGruppo, idUtente);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean rimuoviMembro(Connection con, int idGruppo, int idUtente) throws SQLException {
        return gruppoDAO.doRimuoviMembro(con, idGruppo, idUtente);
    }
}