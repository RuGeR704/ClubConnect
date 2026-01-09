package Application.GestionePagamenti;

import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneAccount.UtenteBean;
import Storage.ConPool;
import Storage.GruppoDAO;
import Storage.PagamentoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock GruppoDAO gruppoDAO;
    @Mock PagamentoDAO pagamentoDAO;
    @Mock Connection connection;

    PagamentoService service;

    @BeforeEach
    void setUp() {
        service = new PagamentoService();
        service.setGruppoDAO(gruppoDAO);
        service.setPagamentoDAO(pagamentoDAO);
    }

    @Test
    void testImpostaAbbonamento_Success() throws SQLException {
        // Mock statico per ConPool (necessario perché il service chiama ConPool.getConnection())
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            // Simuliamo che il gruppo esista e sia un Club
            ClubBean club = new ClubBean();
            club.setId_gruppo(1);
            when(gruppoDAO.doRetrieveByid(any(Connection.class), eq(1))).thenReturn(club);

            service.impostaAbbonamento(1, 20.0, 30);

            // Verifica che sia stato chiamato l'update con i nuovi valori
            verify(gruppoDAO).doUpdate(any(Connection.class), argThat(g ->
                    ((ClubBean)g).getImporto_retta() == 20.0 && ((ClubBean)g).getFrequenza() == 30
            ));
        }
    }

    @Test
    void testIsUtenteGestore_True() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            when(gruppoDAO.isGestore(any(Connection.class), eq(5), eq(10))).thenReturn(true);

            boolean result = service.isUtenteGestore(5, 10);
            assertTrue(result);
        }
    }

    @Test
    void testPagaRetta_Success() throws SQLException {
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            service.pagaRetta(1, 1, 50.0);

            verify(pagamentoDAO).doSaveDettagliPagamento(any(Connection.class), any(DettagliPagamentoBean.class));
        }
    }
}