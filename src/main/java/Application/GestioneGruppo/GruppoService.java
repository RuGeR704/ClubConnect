package Application.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.UtenteDAO; // Assicurati di avere questo import
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class GruppoService {

    private GruppoDAO gruppoDAO;
    private UtenteDAO utenteDAO; // CORRETTO: Tipo UtenteDAO

    // Costruttore unico che inizializza entrambi
    public GruppoService() {
        this.gruppoDAO = new GruppoDAO();
        this.utenteDAO = new UtenteDAO(); // CORRETTO: Inizializzazione aggiunta
    }

    // Per i Test (GruppoDAO)
    public void setGruppoDAO(GruppoDAO dao) {
        this.gruppoDAO = dao;
    }

    // Per i Test (UtenteDAO) - AGGIUNTO
    public void setUtenteDAO(UtenteDAO dao) {
        this.utenteDAO = dao;
    }

    // --- METODI ---

    public GruppoBean recuperaGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return recuperaGruppo(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Overload testabile
    public GruppoBean recuperaGruppo(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.doRetrieveByid(con, idGruppo);
    }

    public int contaMembri(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return gruppoDAO.contaMembri(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Serve per test (overload con Connection)
    public int contaMembri(Connection con, int idGruppo) throws SQLException {
        return gruppoDAO.contaMembri(con, idGruppo);
    }

    public List<UtenteBean> recuperaSociDelGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return gruppoDAO.doRetrieveSoci(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    // Overload testabile
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
    // Overload testabile
    public void iscriviUtenteAlGruppo(Connection con, int idUtente, int idGruppo) throws SQLException {
        gruppoDAO.doIscrizione(con, idUtente, idGruppo);
    }

    public void creaGruppo(GruppoBean gruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            creaGruppo(con, gruppo, idUtente);
        }
    }
    // Overload testabile
    public void creaGruppo(Connection con, GruppoBean gruppo, int idUtente) throws SQLException {
        gruppoDAO.doSave(con, gruppo, idUtente);
    }

    public boolean espelliUtente(int idGruppo, int idUtenteDaEspellere, int idRichiedente) {
        try (Connection con = ConPool.getConnection()) {
            return espelliUtente(con, idGruppo, idUtenteDaEspellere, idRichiedente);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Overload testabile
    public boolean espelliUtente(Connection con, int idGruppo, int idUtenteDaEspellere, int idRichiedente) throws SQLException {
        boolean isGestore = gruppoDAO.isGestore(con, idRichiedente, idGruppo);

        if (!isGestore) {
            return false;
        }
        if (idRichiedente == idUtenteDaEspellere) {
            return false;
        }

        return gruppoDAO.doRimuoviMembro(con, idGruppo, idUtenteDaEspellere);
    }

    public List<GruppoBean> recuperaGruppiNonIscritto(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            // Ora utenteDAO è inizializzato e non darà NullPointerException
            return utenteDAO.doRetrieveGruppiNonIscritto(con, idUtente);
        }
    }

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

    public boolean isUtenteGestore(int idGruppo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return gruppoDAO.isGestore(con, idUtente, idGruppo);
        }
    }
}