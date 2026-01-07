//Implementazione Reale del servizio GestioneSistema dove mettiamo la logica vera e propria
//il REALSUBJECT
package Application.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.UtenteDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestioneSistemaBean implements GestioneSistemaInterface {

    // Variabile statica per simulare lo stato del server
    private static boolean manutenzione = false;
    // Costanti per lo stato utente (Adatta questi numeri alla tua logica: es. 0=Bannato, 1=Attivo)
    private static final int STATO_ATTIVO = 1;
    private static final int STATO_BANNATO = 0;

    @Override
    public void bannaUtente(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            UtenteDAO dao = new UtenteDAO();
            // Usiamo il nuovo metodo sicuro
            dao.doUpdateStato(con, idUtente, STATO_BANNATO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sbannaUtente(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            UtenteDAO dao = new UtenteDAO();
            dao.doUpdateStato(con, idUtente, STATO_ATTIVO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attivaManutenzione() {
        manutenzione = true;
    }

    @Override
    public void disattivaManutenzione() {
        manutenzione = false;
    }

    @Override
    public boolean isManutenzioneAttiva() {
        return manutenzione;
    }

    @Override
    public void sciogliGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO dao = new GruppoDAO();
            dao.doDelete(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UtenteBean> visualizzaListaClienti() {
        try {
            UtenteDAO dao = new UtenteDAO();
            return dao.doRetrieveAll(); // Questo metodo gestisce la connessione internamente nel DAO
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}