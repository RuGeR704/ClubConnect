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

    private static boolean manutenzione = false;
    private static final int STATO_ATTIVO = 1;
    private static final int STATO_BANNATO = 0;

    // Dipendenze
    private UtenteDAO utenteDAO;
    private GruppoDAO gruppoDAO;

    public GestioneSistemaBean() {
        this.utenteDAO = new UtenteDAO();
        this.gruppoDAO = new GruppoDAO();
    }

    // Setters per i TEST
    public void setUtenteDAO(UtenteDAO utenteDAO) { this.utenteDAO = utenteDAO; }
    public void setGruppoDAO(GruppoDAO gruppoDAO) { this.gruppoDAO = gruppoDAO; }

    @Override
    public void bannaUtente(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            utenteDAO.doUpdateStato(con, idUtente, STATO_BANNATO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sbannaUtente(int idUtente) {
        try (Connection con = ConPool.getConnection()) {
            utenteDAO.doUpdateStato(con, idUtente, STATO_ATTIVO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attivaManutenzione() { manutenzione = true; }

    @Override
    public void disattivaManutenzione() { manutenzione = false; }

    @Override
    public boolean isManutenzioneAttiva() { return manutenzione; }

    @Override
    public void sciogliGruppo(int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            gruppoDAO.doDelete(con, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UtenteBean> visualizzaListaClienti() {
        try (Connection con = ConPool.getConnection()){
            // Nota: Ho aggiunto la connessione anche qui per coerenza col testing
            return utenteDAO.doRetrieveAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}