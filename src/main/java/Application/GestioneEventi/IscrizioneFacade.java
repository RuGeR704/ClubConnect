package Application.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class IscrizioneFacade {

    // Dipendenze
    private EventoService eventiService;
    private ComunicazioneService comunicazioniService;

    // Costruttore standard (usato dalle Servlet)
    public IscrizioneFacade() {
        this.eventiService = new EventoService();
        this.comunicazioniService = new ComunicazioneService();
    }

    // Costruttore/Setter per i TEST (Dependency Injection)
    public void setServices(EventoService es, ComunicazioneService cs) {
        this.eventiService = es;
        this.comunicazioniService = cs;
    }

    public boolean iscriviUtente(UtenteBean utente, int idEvento) {
        try {
            // 1. Recupera evento
            EventoBean evento = eventiService.retrieveEvento(idEvento);

            if (evento == null) return false;

            // 2. Controlla posti
            if (evento.getPosti_disponibili() <= 0) return false;

            // 3. Prepara iscrizione
            PartecipazioneBean partecipazione = new PartecipazioneBean();
            partecipazione.setId_utente(utente.getId_utente());
            partecipazione.setId_evento(idEvento);
            partecipazione.setData_registrazione(LocalDateTime.now());

            // 4. Salva e aggiorna
            eventiService.registraPartecipazione(partecipazione);
            eventiService.diminuisciPosti(evento);

            // 5. Invia conferma
            comunicazioniService.inviaConfermaIscrizione(utente, evento);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean disiscriviUtente(UtenteBean utente, int idEvento) {
        try {
            PartecipazioneBean partecipazione = eventiService.retrievePartecipazione(utente.getId_utente(), idEvento);
            if (partecipazione == null) return false; // Utente non iscritto

            EventoBean evento = eventiService.retrieveEvento(partecipazione.getId_evento());
            if (evento == null) return false;

            boolean esito = eventiService.rimuoviPartecipazione(partecipazione);

            if (!esito) return false;

            eventiService.aumentaPosti(evento);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}