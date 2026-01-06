package Application.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Storage.ComunicazioneDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicazioneServiceTest {

    @Mock ComunicazioneDAO daoMock;
    @Mock Connection connection; // Mock della connessione
    @InjectMocks ComunicazioneService service;

    @Test
    void testInviaConfermaIscrizione() {
        // Testiamo che non esploda (il metodo scrive solo log)
        UtenteBean utente = new UtenteBean();
        utente.setEmail("test@email.com");
        utente.setNome("Mario");

        EventoBean evento = Mockito.mock(EventoBean.class); // Mockiamo se EventoBean è complesso o astratto
        when(evento.getNome()).thenReturn("Evento Rock");

        assertDoesNotThrow(() -> service.inviaConfermaIscrizione(utente, evento));
    }

    @Test
    void testCreaComunicazione() throws SQLException {
        ComunicazioniBean bean = new ComunicazioniBean();
        // Usiamo l'overload che accetta la connessione
        service.creaComunicazione(connection, bean);
        verify(daoMock).doSave(eq(connection), eq(bean));
    }

    @Test
    void testRimuoviComunicazione() throws SQLException {
        int id = 5;
        service.rimuoviComunicazione(connection, id);
        // Verifica che chiami delete passando un bean (Mockito matcher any)
        verify(daoMock).doDelete(eq(connection), any(ComunicazioniBean.class));
    }

    @Test
    void testRecuperaTutte() throws SQLException {
        List<ComunicazioniBean> lista = new ArrayList<>();
        when(daoMock.doRetrieveAll()).thenReturn(lista);

        List<ComunicazioniBean> result = service.recuperaTutteLeComunicazioni();

        assertNotNull(result);
        verify(daoMock).doRetrieveAll();
    }
}