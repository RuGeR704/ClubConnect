package Application.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IscrizioneFacadeTest {

    @Mock EventoService eventiService;
    @Mock ComunicazioneService comunicazioniService;

    IscrizioneFacade facade;

    @BeforeEach
    void setUp() {
        facade = new IscrizioneFacade();
        // INIETTIAMO I MOCK
        facade.setServices(eventiService, comunicazioniService);
    }

    @Test
    void testIscriviUtente_Successo() throws SQLException {
        // GIVEN
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);

        EventoBean evento = new EventoBean();
        evento.setId_evento(100);
        evento.setPosti_disponibili(5); // Ci sono posti

        when(eventiService.retrieveEvento(100)).thenReturn(evento);

        // WHEN
        boolean result = facade.iscriviUtente(utente, 100);

        // THEN
        assertTrue(result);
        // Verifica che abbia scalato i posti
        verify(eventiService).diminuisciPosti(evento);
        // Verifica che abbia registrato
        verify(eventiService).registraPartecipazione(any(PartecipazioneBean.class));
        // Verifica che abbia mandato la mail
        verify(comunicazioniService).inviaConfermaIscrizione(utente, evento);
    }

    @Test
    void testIscriviUtente_PostiEsauriti() throws SQLException {
        // GIVEN
        UtenteBean utente = new UtenteBean();
        EventoBean evento = new EventoBean();
        evento.setPosti_disponibili(0); // POSTI FINITI

        when(eventiService.retrieveEvento(100)).thenReturn(evento);

        // WHEN
        boolean result = facade.iscriviUtente(utente, 100);

        // THEN
        assertFalse(result); // Deve fallire
        verify(eventiService, never()).registraPartecipazione(any()); // Non deve registrare nulla
        verify(comunicazioniService, never()).inviaConfermaIscrizione(any(), any()); // Niente email
    }
}