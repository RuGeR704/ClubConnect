package Application.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
// Importa eventualmente GestionePagamentiService e GestioneComunicazioniService se esistono

import java.sql.SQLException;
import java.time.LocalDateTime;

public class IscrizioneFacade {

    public boolean iscriviUtente(UtenteBean utente, int idEvento) {
        // 1. Inizializza i Service necessari
        GestioneEventiBean eventiService = new GestioneEventiBean();

        // Esempi di altri service (se i colleghi li hanno fatti)
        // GestioneEventiBean pagamentiService = new GestionePagamentiService();
        // GestioneComunicazioniService comunicazioniService = new GestioneComunicazioniService();

        try {
            // 2. Verifica disponibilità posti (Dovresti aggiungere questo metodo al Service)
            // boolean postiDisponibili = eventiService.controllaPosti(idEvento);
            // if (!postiDisponibili) return false;

            // 3. Prepara l'oggetto Partecipazione
            PartecipazioneBean partecipazione = new PartecipazioneBean();
            partecipazione.setId_utente(utente.getId_utente()); // Assumo getter getId()
            partecipazione.setId_evento(idEvento);
            partecipazione.setData_registrazione(LocalDateTime.now());

            // 4. Salva la partecipazione tramite il Service
            eventiService.registraPartecipazione(partecipazione);

            // 5. (Opzionale) Gestione Pagamento e Email
            // pagamentiService.paga(...);
            // comunicazioniService.inviaEmailConferma(utente.getEmail(), ...);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}