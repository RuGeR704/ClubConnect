package Application.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneGruppoBean {

    // Serve a VisualizzaSociServlet per mostrare i dati del gruppo in alto nella pagina
    public GruppoBean recuperaGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return new GruppoDAO().doRetrieveByid(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Serve a VisualizzaSociServlet per riempire la tabella dei soci
    public List<UtenteBean> recuperaSociDelGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            // Chiama il metodo doRetrieveSoci
            return new GruppoDAO().doRetrieveSoci(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    public void iscriviUtenteAlGruppo(int idUtente, int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO dao = new GruppoDAO();
            dao.doIscrizione(con, idUtente, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
    public boolean espelliUtente(int idGruppo, int idUtenteDaEspellere, int idRichiedente) {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO dao = new GruppoDAO();

            boolean isGestore = dao.isGestore(con, idRichiedente, idGruppo);

            if (!isGestore) {
                System.out.println("Tentativo di espulsione non autorizzato: Utente " + idRichiedente + " non è gestore.");
                return false;
            }
            if (idRichiedente == idUtenteDaEspellere) {
                return false;
            }


            return dao.doRimuoviMembro(con, idGruppo, idUtenteDaEspellere);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Integer, Boolean> recuperaMappaRuoli(int idGruppo) {
        // Il Bean gestisce la risorsa (connessione)
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO dao = new GruppoDAO();
            // Richiama il DAO per i dati
            return dao.getRuoliIscritti(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>(); // Ritorna mappa vuota in caso di errore
        }
    }
}