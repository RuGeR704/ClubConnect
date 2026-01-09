package Application.GestionePagamenti;

import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.PagamentoDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagamentoService {

    private GruppoDAO gruppoDAO;
    private PagamentoDAO pagamentoDAO;

    public PagamentoService() {
        this.gruppoDAO = new GruppoDAO();
        this.pagamentoDAO = new PagamentoDAO();
    }

    // Setters per i Test Unitari (Mockito)
    public void setGruppoDAO(GruppoDAO gruppoDAO) {
        this.gruppoDAO = gruppoDAO;
    }

    public void setPagamentoDAO(PagamentoDAO pagamentoDAO) {
        this.pagamentoDAO = pagamentoDAO;
    }

    /**
     * Imposta o modifica i dati dell'abbonamento (Costo e Frequenza) per un Club.
     */
    public void impostaAbbonamento(int idClub, double importo, int frequenza) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            // Recuperiamo il gruppo per assicurarci che sia un CLUB
            GruppoBean gruppo = gruppoDAO.doRetrieveByid(con, idClub);

            if (gruppo instanceof ClubBean) {
                ClubBean club = (ClubBean) gruppo;
                club.setImporto_retta(importo);
                club.setFrequenza(frequenza);

                // Salviamo le modifiche
                gruppoDAO.doUpdate(con, club);
            } else {
                throw new IllegalArgumentException("L'ID fornito non corrisponde a un Club valido.");
            }
        }
    }

    /**
     * Controlla se l'utente è gestore del gruppo.
     */
    public boolean isUtenteGestore(int idUtente, int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            return gruppoDAO.isGestore(con, idUtente, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calcola la situazione pagamenti per una lista di soci.
     */
    public Map<Integer, Boolean> getSituazionePagamenti(int idGruppo, List<UtenteBean> soci, int frequenzaGiorni) {
        Map<Integer, Boolean> mappa = new HashMap<>();

        try (Connection con = ConPool.getConnection()) {
            for (UtenteBean socio : soci) {
                // Passiamo la connessione 'con' per evitare di aprirne una nuova per ogni socio (Performance)
                boolean inRegola = pagamentoDAO.isAbbonamentoValido(con, socio.getId_utente(), idGruppo, frequenzaGiorni);
                mappa.put(socio.getId_utente(), inRegola);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mappa;
    }

    /**
     * Registra il pagamento della retta.
     */
    public void pagaRetta(int idGruppo, int idMetodo, double importo) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            DettagliPagamentoBean pagamento = new DettagliPagamentoBean();
            pagamento.setId_gruppo(idGruppo);
            pagamento.setId_metodo(idMetodo);
            pagamento.setImporto(importo);
            pagamento.setData_tansazione(LocalDateTime.now());

            pagamentoDAO.doSaveDettagliPagamento(con, pagamento);
        }
    }
}