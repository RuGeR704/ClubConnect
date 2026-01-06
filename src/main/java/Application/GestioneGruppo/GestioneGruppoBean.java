package Application.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
}