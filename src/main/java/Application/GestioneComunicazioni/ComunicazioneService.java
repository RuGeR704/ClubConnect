package Application.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Storage.ComunicazioneDAO;
import Storage.ConPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComunicazioneService {

    private static final Logger LOGGER = Logger.getLogger(ComunicazioneService.class.getName());
    private ComunicazioneDAO comunicazioneDAO;

    // Costruttore standard
    public ComunicazioneService() {
        this.comunicazioneDAO = new ComunicazioneDAO();
    }

    // Costruttore per i TEST (Dependency Injection)
    public ComunicazioneService(ComunicazioneDAO comunicazioneDAO) {
        this.comunicazioneDAO = comunicazioneDAO;
    }

    /**
     * Invia una notifica simulata (Log).
     */
    public void inviaConfermaIscrizione(UtenteBean utente, EventoBean evento) {
        if (utente == null || evento == null) {
            LOGGER.log(Level.WARNING, "Dati incompleti per invio conferma.");
            return;
        }

        String destinatario = utente.getEmail();
        String oggetto = "Conferma Iscrizione: " + evento.getNome();
        String corpo = "Ciao " + utente.getNome() + ",\n" +
                "La tua iscrizione all'evento '" + evento.getNome() + "' è confermata.\n" +
                "Data: " + evento.getData_ora();

        sendEmail(destinatario, oggetto, corpo);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            // Simuliamo l'invio senza Thread.sleep nei test unitari se possibile,
            // ma qui lo lasciamo per realismo
            LOGGER.info("[EMAIL] To: " + to + " | Subject: " + subject);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore invio email", e);
        }
    }

    // --- METODI CRUD (con Overloading per i Test) ---

    public void creaComunicazione(ComunicazioniBean comunicazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            creaComunicazione(con, comunicazione);
        }
    }
    // Per i test
    public void creaComunicazione(Connection con, ComunicazioniBean comunicazione) throws SQLException {
        comunicazioneDAO.doSave(con, comunicazione);
    }

    public void rimuoviComunicazione(int idComunicazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            rimuoviComunicazione(con, idComunicazione);
        }
    }
    // Per i test
    public void rimuoviComunicazione(Connection con, int idComunicazione) throws SQLException {
        ComunicazioniBean bean = new ComunicazioniBean();
        bean.setId_comunicazione(idComunicazione);
        comunicazioneDAO.doDelete(con, bean);
    }

    public ComunicazioniBean recuperaComunicazione(int id) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return recuperaComunicazione(con, id);
        }
    }
    // Per i test
    public ComunicazioniBean recuperaComunicazione(Connection con, int id) throws SQLException {
        return comunicazioneDAO.doRetrieveById(con, id);
    }

    public List<ComunicazioniBean> recuperaTutteLeComunicazioni() throws SQLException {
        // Se il DAO apre la connessione da solo nel doRetrieveAll,
        // qui chiamiamo direttamente il DAO mockato nei test.
        return comunicazioneDAO.doRetrieveAll();
    }

    public List<ComunicazioniBean> recuperaComunicazioniPerUtente(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return recuperaComunicazioniPerUtente(con, idUtente);
        }
    }
    // Per i test
    public List<ComunicazioniBean> recuperaComunicazioniPerUtente(Connection con, int idUtente) throws SQLException {
        return comunicazioneDAO.doRetrievebyGroup(con, idUtente);
    }
}