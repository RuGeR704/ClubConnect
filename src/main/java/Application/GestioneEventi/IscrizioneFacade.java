package Application.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class IscrizioneFacade {

    public boolean iscriviUtente(UtenteBean utente, int idEvento) {
        GestioneEventiBean eventiService = new GestioneEventiBean();
        ComunicazioneService comunicazioniService = new ComunicazioneService();
        try {
            // RECUPERA L'EVENTO DAL DB
            // Qui sfruttiamo il metodo retrieveEvento
            EventoBean evento = eventiService.retrieveEvento(idEvento);

            if (evento == null) {
                return false; // L'evento non esiste
            }

            // CONTROLLA DISPONIBILITÀ
            if (evento.getPosti_disponibili() <= 0) {
                return false; // Non ci sono posti, iscrizione fallita
            }

            // PREPARA L'ISCRIZIONE (Sottosistema Eventi)
            PartecipazioneBean partecipazione = new PartecipazioneBean();
            partecipazione.setId_utente(utente.getId_utente());
            partecipazione.setId_evento(idEvento);
            partecipazione.setData_registrazione(LocalDateTime.now());

            // SALVA E AGGIORNA
            eventiService.registraPartecipazione(partecipazione);

            // Scala il posto
            eventiService.diminuisciPosti(evento);

            // INVIA CONFERMA (Sottosistema Comunicazioni)
            comunicazioniService.inviaConfermaIscrizione(utente, evento);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
