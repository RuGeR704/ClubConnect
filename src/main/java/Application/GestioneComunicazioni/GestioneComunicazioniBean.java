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
// è lo standard professionale per tracciare le operazioni in un server reale.

// Qui gestiamo sia l'invio di notifiche (Email) sia la gestione delle News (ComunicazioniBean).
public class GestioneComunicazioniBean {

    // Logger professionale per registrare le attività del server,
    // questi messaggi appariranno nei log del server in rosso/nero a seconda del livello (Info/Severe)
    private static final Logger LOGGER = Logger.getLogger(GestioneComunicazioniBean.class.getName());
    /**
     * Invia una notifica di conferma iscrizione all'utente.
     * Dato che questa è una notifica privata, non viene salvata nel DB,
     * ma viene inviata via Email
     *
     * @param utente L'utente che si è iscritto.
     * @param evento L'evento a cui si è iscritto.
     */
    public void inviaConfermaIscrizione(UtenteBean utente, EventoBean evento) {

        // Validazione dati (difesa contro oggetti nulli)
        if (utente == null || evento == null) {
            LOGGER.log(Level.WARNING, "Dati incompleti per invio conferma.");
            return;
        }

        // Costruzione del messaggio
        String destinatario = utente.getEmail();
        String oggetto = "Conferma Iscrizione: " + evento.getNome();

        String corpo = "Ciao " + utente.getNome() + ",\n" +
                "La tua iscrizione all'evento '" + evento.getNome() + "' è confermata.\n" +
                "Data: " + evento.getData_ora();

        sendEmail(destinatario, oggetto, corpo);
    }

    // Metodo privato che gestisce l'invio tecnico dell'email.
    private void sendEmail(String to, String subject, String body) {

        try {
            // Simulazione ritardo di rete (opzionale)
            Thread.sleep(500);

            LOGGER.info("--------------------------------------------------");
            LOGGER.info(" [EMAIL SENDER SERVICE] Invio in corso...");
            LOGGER.info(" TO:      " + to);
            LOGGER.info(" SUBJECT: " + subject);
            LOGGER.info(" BODY:    \n" + body);
            LOGGER.info(" [STATUS] Email inviata con successo.");
            LOGGER.info("--------------------------------------------------");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'invio dell'email a " + to, e);
        }
    }

    // Crea una nuova comunicazione nel database.

    public void creaComunicazione(ComunicazioniBean comunicazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            ComunicazioneDAO dao = new ComunicazioneDAO();
            dao.doSave(con, comunicazione);
        }
    }
    // Rimuove una comunicazione dal database dato il suo ID.
    // Usato sia per le comunicazioni globali che per quelle di gruppo.

    public void rimuoviComunicazione(int idComunicazione) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            ComunicazioneDAO dao = new ComunicazioneDAO();
            // Il DAO vuole un oggetto Bean per fare la delete
            ComunicazioniBean bean = new ComunicazioniBean();
            bean.setId_comunicazione(idComunicazione);

            dao.doDelete(con, bean);
        }
    }
    // Recupera una comunicazione dal database dato il suo ID.
    // Usato sia per le comunicazioni globali che per quelle di gruppo.

    public ComunicazioniBean recuperaComunicazione(int id) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            ComunicazioneDAO dao = new ComunicazioneDAO();
            return dao.doRetrieveById(con, id);
        }
    }
    //Recupera tutte le comunicazioni presenti nel database.

    public List<ComunicazioniBean> recuperaTutteLeComunicazioni() throws SQLException {
        ComunicazioneDAO dao = new ComunicazioneDAO();
        // Nota: il doRetrieveAll nel DAO apre già la connessione internamente
        return dao.doRetrieveAll();
    }

    //Recupera le comunicazioni visibili a un utente specifico (basate sui gruppi a cui è iscritto).

    public List<ComunicazioniBean> recuperaComunicazioniPerUtente(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            ComunicazioneDAO dao = new ComunicazioneDAO();
            return dao.doRetrievebyGroup(con, idUtente);
        }
    }

}