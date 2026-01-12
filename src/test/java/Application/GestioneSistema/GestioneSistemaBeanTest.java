package Application.GestioneSistema;

import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.UtenteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneSistemaBeanTest {

    @Mock UtenteDAO utenteDAO;
    @Mock GruppoDAO gruppoDAO;
    @Mock Connection connection;

    @InjectMocks
    GestioneSistemaBean bean;

    @Test
    void testBannaUtente() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            bean.bannaUtente(10);

            // Verifica che utemte viene bannato
            verify(utenteDAO).doUpdateStato(connection, 10, 0);
        }
    }

    @Test
    void testSbannaUtente() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            bean.sbannaUtente(10);

            // Verifica che il DAO venga chiamato con stato 1 (Attivo)
            verify(utenteDAO).doUpdateStato(connection, 10, 1);
        }
    }

    @Test
    void testSciogliGruppo() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            bean.sciogliGruppo(5);

            verify(gruppoDAO).doDelete(connection, 5);
        }
    }

    @Test
    void testVisualizzaListaClienti() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            bean.visualizzaListaClienti();

            verify(utenteDAO).doRetrieveAll();
        }
    }

    @Test
    void testManutenzione() {
        // Test toggle stato manutenzione
        bean.attivaManutenzione();
        assertTrue(bean.isManutenzioneAttiva());

        bean.disattivaManutenzione();
        assertFalse(bean.isManutenzioneAttiva());
    }
}