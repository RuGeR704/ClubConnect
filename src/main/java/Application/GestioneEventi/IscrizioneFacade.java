package Application.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class IscrizioneFacade {

    public boolean iscriviUtente(UtenteBean utente, int idEvento) {
        GestioneEventiBean eventiService = new GestioneEventiBean();

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

            // PREPARA L'ISCRIZIONE
            PartecipazioneBean partecipazione = new PartecipazioneBean();
            partecipazione.setId_utente(utente.getId_utente());
            partecipazione.setId_evento(idEvento);
            partecipazione.setData_registrazione(LocalDateTime.now());

            // SALVA E AGGIORNA
            eventiService.registraPartecipazione(partecipazione);

            // Scala il posto (chiama il metodo appena creato)
            eventiService.diminuisciPosti(evento);

            return true; // Tutto andato a buon fine

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
