package Application.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
//è lo standard professionale per tracciare le operazioni in un server reale.
/**
 * Qui gestiamo sia l'invio di notifiche (Email) sia la gestione delle News (ComunicazioniBean).
 */
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
            LOGGER.log(Level.WARNING, "Impossibile inviare conferma: Utente o Evento nulli.");
            return;
        }

        // Costruzione del messaggio
        String destinatario = utente.getEmail();
        String oggetto = "Conferma Iscrizione: " + evento.getNome();

        StringBuilder corpoMessaggio = new StringBuilder();
        corpoMessaggio.append("Gentile ").append(utente.getNome()).append(",\n\n");
        corpoMessaggio.append("Siamo felici di confermare la tua iscrizione all'evento: ").append(evento.getNome()).append(".\n");
        corpoMessaggio.append("Dettagli:\n");
        corpoMessaggio.append("- Data: ").append(evento.getData_ora()).append("\n");
        corpoMessaggio.append("- Luogo/Sede: ClubConnect Association\n\n");
        corpoMessaggio.append("Cordiali saluti,\nIl team di ClubConnect");

        // Invio effettivo (Delegato a un metodo privato per pulizia)
        sendEmail(destinatario, oggetto, corpoMessaggio.toString());
    }

    /**
     * Metodo privato che gestisce l'invio tecnico dell'email.
     * Attualmente simula l'invio tramite Logger, ma è predisposto per JavaMail.
     */
    private void sendEmail(String to, String subject, String body) {

        try {
            // Simulazione ritardo di rete (opzionale)
            Thread.sleep(500);

            LOGGER.info("--------------------------------------------------");
            LOGGER.info(" [EMAIL SENDER SERVICE] Invio in corso...");
            LOGGER.info(" TO:      " + to);
            LOGGER.info(" SUBJECT: " + subject);
            LOGGER.info(" BODY:    \n" + body);
            LOGGER.info(" [STATUS] Email inviata con successo al server SMTP.");
            LOGGER.info("--------------------------------------------------");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'invio dell'email a " + to, e);
        }
    }

    /* * --- SPAZIO PER I METODI FUTURI ---
     * public void creaComunicazioneGruppo(ComunicazioniBean comunicazione) { ... }
     * public List<ComunicazioniBean> recuperaComunicazioni() { ... }
     * ...
     */
}