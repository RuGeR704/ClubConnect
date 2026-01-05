package Application.GestionePagamenti;

import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Storage.ConPool;
import Storage.GruppoDAO;

import java.sql.Connection;
import java.sql.SQLException;

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
}
