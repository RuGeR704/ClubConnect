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

    public GruppoBean recuperaGruppo(int idGruppo) {
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

    // Alias per la Servlet (chiama il metodo sotto) affinché la Servlet lo trovi
    public int getNumeroMembri(int idGruppo) {
        return contaMembri(idGruppo);
    }
    // Metodo Principale (gestisce la connessione)
    public int contaMembri(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return contaMembri(con, idGruppo);
        } catch (SQLException e) { return 0; }
    }
    // Overload (riceve la connessione)
    public int contaMembri(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.contaMembri(con, idGruppo);
    }

    // Per sfruttare il metodo che c'è già in GruppoDAO (contaEventiInProgramma)
    public int getNumeroEventiProgrammati(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {

            return gruppoDAO.contaEventiInProgramma(con, idGruppo);
        } catch (SQLException e) {
            return 0;
        }
    }

    public void creaGruppo(GruppoBean g, int idUtente) throws SQLException {
        try(Connection con = ConPool.getConnection()){
            creaGruppo(con, g, idUtente);
        }
    }
    // Overload testabile
    public void creaGruppo(Connection con, GruppoBean g, int idUtente) throws SQLException {
        gruppoDAO.doSave(con, g, idUtente);
    }

    // Metodo per EsploraGruppiServlet
    public List<GruppoBean> recuperaGruppiNonIscritto(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return utenteDAO.doRetrieveGruppiNonIscritto(con, idUtente);
        }
    }

    // Metodo per VisualizzaGruppoServlet
    public boolean isUtenteIscritto(int idGruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            // Se UtenteDAO non ha un metodo isIscritto diretto, usiamo le liste
            List<GruppoBean> iscritti = utenteDAO.doRetrieveGruppiIscritto(idUtente);
            for (GruppoBean g : iscritti) {
                if (g.getId_gruppo() == idGruppo) return true;
            }
            return false;
        }
    }

    // Metodo per VisualizzaGruppoServlet (controllo admin)
    public boolean isUtenteGestore(int idGruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return gruppoDAO.isGestore(con, idUtente, idGruppo);
        }
    }
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