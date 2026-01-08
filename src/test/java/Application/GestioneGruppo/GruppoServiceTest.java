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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GruppoServiceTest {

    @Mock GruppoDAO gruppoDAO;
    @Mock UtenteDAO utenteDAO;
    @Mock Connection connection;

    @InjectMocks
    GruppoService service;

    @Test
    void testRecuperaMappaRuoli() throws SQLException {
        // GIVEN
        Map<Integer, Boolean> expectedMap = new HashMap<>();
        expectedMap.put(1, true); // Admin
        expectedMap.put(2, false); // Utente normale

        when(gruppoDAO.getRuoliIscritti(connection, 10)).thenReturn(expectedMap);

        // WHEN
        Map<Integer, Boolean> result = service.recuperaMappaRuoli(connection, 10);

        // THEN
        assertEquals(expectedMap, result);
        verify(gruppoDAO).getRuoliIscritti(connection, 10);
    }

    @Test
    void testEspelliUtente_Successo() throws SQLException {
        // GIVEN: Richiedente (10) è gestore, Target (99) è diverso da Richiedente
        when(gruppoDAO.isGestore(any(), eq(10), eq(5))).thenReturn(true);
        when(gruppoDAO.doRimuoviMembro(any(), eq(5), eq(99))).thenReturn(true);

        // WHEN
        boolean result = service.espelliUtente(connection, 5, 99, 10);

        // THEN
        assertTrue(result);
    }

    @Test
    void testEspelliUtente_FallimentoNonGestore() throws SQLException {
        // GIVEN: Richiedente (10) NON è gestore
        when(gruppoDAO.isGestore(any(), eq(10), eq(5))).thenReturn(false);

        // WHEN
        boolean result = service.espelliUtente(connection, 5, 99, 10);

        // THEN
        assertFalse(result);
        verify(gruppoDAO, never()).doRimuoviMembro(any(), anyInt(), anyInt());
    }
}