package Application.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Storage.GruppoDAO;
import Storage.UtenteDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GruppoServiceTest {

    @Mock GruppoDAO gruppoDAO;
    @Mock UtenteDAO utenteDAO;
    @Mock Connection connection; // Mockiamo la connessione per i metodi overload

    @InjectMocks
    GruppoService service;

    @Test
    void testRecuperaGruppo() throws SQLException {
        GruppoBean g = new AssociazioneBean();
        g.setId_gruppo(1);
        when(gruppoDAO.doRetrieveByid(any(), eq(1))).thenReturn(g);

        GruppoBean result = service.recuperaGruppo(connection, 1);
        assertNotNull(result);
        assertEquals(1, result.getId_gruppo());
    }

    @Test
    void testEspelliUtente_Successo() throws SQLException {
        // GIVEN: L'utente richiedente è gestore e sta espellendo qualcun altro
        when(gruppoDAO.isGestore(any(), eq(10), eq(5))).thenReturn(true); // 10=Gestore, 5=Gruppo
        when(gruppoDAO.doRimuoviMembro(any(), eq(5), eq(99))).thenReturn(true); // 99=Utente da espellere

        // WHEN
        boolean result = service.espelliUtente(connection, 5, 99, 10);

        // THEN
        assertTrue(result);
        verify(gruppoDAO).doRimuoviMembro(any(), eq(5), eq(99));
    }

    @Test
    void testEspelliUtente_FallimentoNonGestore() throws SQLException {
        // GIVEN: L'utente richiedente NON è gestore
        when(gruppoDAO.isGestore(any(), eq(10), eq(5))).thenReturn(false);

        // WHEN
        boolean result = service.espelliUtente(connection, 5, 99, 10);

        // THEN
        assertFalse(result);
        verify(gruppoDAO, never()).doRimuoviMembro(any(), anyInt(), anyInt());
    }

    @Test
    void testEspelliUtente_AutoEspulsione() throws SQLException {
        // GIVEN: Il gestore prova ad espellere se stesso (id 10)
        when(gruppoDAO.isGestore(any(), eq(10), eq(5))).thenReturn(true);

        // WHEN
        boolean result = service.espelliUtente(connection, 5, 10, 10);

        // THEN
        assertFalse(result); // Non deve permetterlo
        verify(gruppoDAO, never()).doRimuoviMembro(any(), anyInt(), anyInt());
    }

    @Test
    void testIsUtenteIscritto() throws SQLException {
        // GIVEN
        GruppoBean g1 = new AssociazioneBean(); g1.setId_gruppo(1);
        GruppoBean g2 = new AssociazioneBean(); g2.setId_gruppo(2);
        List<GruppoBean> gruppi = Arrays.asList(g1, g2);

        when(utenteDAO.doRetrieveGruppiIscritto(anyInt())).thenReturn(gruppi);

        // WHEN
        boolean iscritto = service.isUtenteIscritto(2, 100); // Cerca gruppo 2
        boolean nonIscritto = service.isUtenteIscritto(3, 100); // Cerca gruppo 3

        // THEN
        assertTrue(iscritto);
        assertFalse(nonIscritto);
    }
}