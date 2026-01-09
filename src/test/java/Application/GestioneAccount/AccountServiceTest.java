package Application.GestioneAccount;

import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.ConPool;
import Storage.PagamentoDAO; // NUOVO DAO
import Storage.UtenteDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic; // Importante per mockare ConPool
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
class AccountServiceTest {

    @Mock
    UtenteDAO utenteDAO;

    @Mock
    PagamentoDAO pagamentoDAO; // 1. Aggiungiamo il mock del nuovo DAO

    @Mock
    Connection connection;

    @InjectMocks
    AccountService accountService;

    // --- TEST ESISTENTI (READ) ---
    // Questi usano i metodi "overload" del Service che accettano (Connection con)

    @Test
    void testGetGruppiIscritto_Trovati() throws SQLException {
        int idUtente = 1;
        List<GruppoBean> listaAttesa = new ArrayList<>();
        listaAttesa.add(Mockito.mock(GruppoBean.class));

        when(utenteDAO.doRetrieveGruppiIscritto(connection, idUtente)).thenReturn(listaAttesa);

        List<GruppoBean> risultato = accountService.getGruppiIscritto(connection, idUtente);

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveGruppiIscritto(connection, idUtente);
    }

    @Test
    void testGetGruppiAdmin_Trovati() throws SQLException {
        int idUtente = 5;
        List<GruppoBean> listaAdmin = new ArrayList<>();
        listaAdmin.add(Mockito.mock(GruppoBean.class));

        when(utenteDAO.doRetrieveGruppiAdmin(connection, idUtente)).thenReturn(listaAdmin);

        List<GruppoBean> risultato = accountService.getGruppiAdmin(connection, idUtente);

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveGruppiAdmin(connection, idUtente);
    }

    @Test
    void testGetMetodiPagamento() throws SQLException {
        int idUtente = 10;
        List<MetodoPagamentoBean> listaMetodi = new ArrayList<>();
        listaMetodi.add(Mockito.mock(MetodoPagamentoBean.class));

        when(utenteDAO.doRetrieveAllMetodiPagamento(connection, idUtente)).thenReturn(listaMetodi);

        List<MetodoPagamentoBean> risultato = accountService.getMetodiPagamento(connection, idUtente);

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveAllMetodiPagamento(connection, idUtente);
    }

    @Test
    void testGetStoricoPagamenti() throws SQLException {
        int idUtente = 20;
        List<DettagliPagamentoBean> listaPagamenti = new ArrayList<>();
        listaPagamenti.add(Mockito.mock(DettagliPagamentoBean.class));

        when(utenteDAO.doRetrievePagamenti(connection, idUtente)).thenReturn(listaPagamenti);

        List<DettagliPagamentoBean> risultato = accountService.getStoricoPagamenti(connection, idUtente);

        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrievePagamenti(connection, idUtente);
    }

    @Test
    void testModificaDatiUtente() throws SQLException {
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(100);
        utente.setNome("NuovoNomeTest");

        accountService.modificaDatiUtente(connection, utente);

        verify(utenteDAO).doUpdate(eq(connection), eq(utente));
    }

    // --- NUOVI TEST (WRITE / GESTIONE PAGAMENTI) ---
    // Questi metodi nel Service aprono la connessione internamente con ConPool.getConnection().
    // Usiamo mockStatic per intercettare quella chiamata.

    @Test
    void testAggiungiMetodoPagamento() throws SQLException {
        // GIVEN
        MetodoPagamentoBean metodo = new MetodoPagamentoBean();
        metodo.setId_utente(1);
        metodo.setNumero_carta("1234567812345678");

        // Usiamo il try-with-resources per il MockedStatic (si chiude da solo alla fine del blocco)
        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            // Istruiamo Mockito: quando chiami ConPool.getConnection(), restituisci la nostra connection mock
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            // WHEN
            accountService.aggiungiMetodoPagamento(metodo);

            // THEN
            // Verifichiamo che il PagamentoDAO sia stato chiamato correttamente
            verify(pagamentoDAO).doSaveMetodoPagamento(connection, metodo);
        }
    }

    @Test
    void testRimuoviMetodoPagamento() throws SQLException {
        // GIVEN
        int idMetodo = 5;
        int idUtente = 10;

        try (MockedStatic<ConPool> mockedConPool = mockStatic(ConPool.class)) {
            mockedConPool.when(ConPool::getConnection).thenReturn(connection);

            // WHEN
            accountService.rimuoviMetodoPagamento(idMetodo, idUtente);

            // THEN
            verify(pagamentoDAO).doDeleteMetodoPagamento(connection, idMetodo, idUtente);
        }
    }
}