package Application.GestionePagamenti;

import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.PagamentoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionePagamentiBean {
    //Imposta o modifica i dati dell'abbonamento (Costo e Frequenza) per un Club.

    public boolean impostaAbbonamento(int idClub, double importo, int frequenza) {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO gruppoDAO = new GruppoDAO();

            // Recuperiamo il gruppo per assicurarci che sia un CLUB
            GruppoBean gruppo = gruppoDAO.doRetrieveByid(con, idClub);

            if (gruppo != null && gruppo instanceof ClubBean) {
                ClubBean club = (ClubBean) gruppo;

                // Aggiorniamo i dati dell'abbonamento
                club.setImporto_retta(importo);
                club.setFrequenza(frequenza);

                // Salviamo le modifiche nel DB usando il doUpdate del DAO
                gruppoDAO.doUpdate(con, club);
                return true;
            }
            return false; // L'ID non corrisponde a un Club valido

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Metodo per modificare un abbonamento esistente.
     * Tecnicamente esegue la stessa operazione di 'impostaAbbonamento' (UPDATE sul DB).
     */
    public boolean modificaAbbonamento(int idClub, double nuovoImporto, int nuovaFrequenza) {
        // Riutilizzo la logica esistente
        return impostaAbbonamento(idClub, nuovoImporto, nuovaFrequenza);
    }
    /**
     * Controlla i permessi dell'utente per un gruppo specifico.
     * Chiama il DAO per verificare se nella tabella Iscrizione l'utente figura come 'Gestore'.
     */
    public boolean isUtenteGestore(int idUtente, int idGruppo) {
        try (Connection con = ConPool.getConnection()) {
            GruppoDAO dao = new GruppoDAO();
            return dao.isGestore(con, idUtente, idGruppo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Map<Integer, Boolean> getSituazionePagamenti(int idGruppo, List<UtenteBean> soci, int frequenzaGiorni) {
        Map<Integer, Boolean> mappa = new HashMap<>();
        PagamentoDAO daoPagamento = new PagamentoDAO();

        try (Connection con = Storage.ConPool.getConnection()) {
            for (UtenteBean socio : soci) {
                // Controlla se l'ultimo pagamento di questo socio per questo gruppo è ancora valido
                boolean inRegola = daoPagamento.isAbbonamentoValido(con, socio.getId_utente(), idGruppo, frequenzaGiorni);
                mappa.put(socio.getId_utente(), inRegola);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mappa;
    }
    /**
     * Registra un nuovo pagamento per la retta di un club.
     * @param idGruppo ID del club
     * @param idMetodo ID del metodo di pagamento scelto dall'utente
     * @param importo Somma da pagare
     * @return true se il pagamento è registrato con successo
     */
    public boolean pagaRetta(int idGruppo, int idMetodo, double importo) {
        try (Connection con = ConPool.getConnection()) {
            PagamentoDAO dao = new PagamentoDAO();

            DettagliPagamentoBean pagamento = new DettagliPagamentoBean();
            pagamento.setId_gruppo(idGruppo);
            pagamento.setId_metodo(idMetodo);
            pagamento.setImporto(importo);
            // Impostiamo la data della transazione al momento attuale
            pagamento.setData_tansazione(java.time.LocalDateTime.now());

            dao.doSaveDettagliPagamento(con, pagamento);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
