package Application.GestioneAccount;

import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.UtenteDAO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    UtenteDAO utenteDAO; // Il DAO finto

    @Mock
    Connection connection; // La connessione finta

    @InjectMocks
    AccountService accountService; // Il service sotto test

    @Test
    void testGetGruppiIscritto_Trovati() throws SQLException {
        // GIVEN
        int idUtente = 1;
        List<GruppoBean> listaAttesa = new ArrayList<>();
        GruppoBean gruppoFinto = Mockito.mock(GruppoBean.class);
        listaAttesa.add(gruppoFinto);

        // CORREZIONE: Ora il metodo DAO accetta la connessione!
        when(utenteDAO.doRetrieveGruppiIscritto(connection, idUtente)).thenReturn(listaAttesa);

        // WHEN
        // Usiamo il metodo del Service 'per i test' che accetta la Connection
        List<GruppoBean> risultato = accountService.getGruppiIscritto(connection, idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());

        // Verifichiamo che il DAO sia stato chiamato passando LA NOSTRA connessione mock
        verify(utenteDAO).doRetrieveGruppiIscritto(connection, idUtente);
    }

    @Test
    void testGetGruppiAdmin_Trovati() throws SQLException {
        // GIVEN
        int idUtente = 5;
        List<GruppoBean> listaAdmin = new ArrayList<>();
        GruppoBean gruppoAdminFinto = Mockito.mock(GruppoBean.class);
        listaAdmin.add(gruppoAdminFinto);

        // CORREZIONE: Passiamo connection anche qui
        when(utenteDAO.doRetrieveGruppiAdmin(connection, idUtente)).thenReturn(listaAdmin);

        // WHEN
        List<GruppoBean> risultato = accountService.getGruppiAdmin(connection, idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveGruppiAdmin(connection, idUtente);
    }

    @Test
    void testGetMetodiPagamento() throws SQLException {
        // GIVEN
        int idUtente = 10;
        List<MetodoPagamentoBean> listaMetodi = new ArrayList<>();
        listaMetodi.add(Mockito.mock(MetodoPagamentoBean.class));

        // CORREZIONE: Assumiamo che il DAO ora prenda la connessione
        when(utenteDAO.doRetrieveAllMetodiPagamento(connection, idUtente)).thenReturn(listaMetodi);

        // WHEN
        // ATTENZIONE: Affinché questo funzioni col Mock, dovresti aggiungere nel Service
        // un metodo: public List<MetodoPagamentoBean> getMetodiPagamento(Connection con, int id)
        // Se non lo hai ancora fatto, fallo subito nel Service!
        List<MetodoPagamentoBean> risultato = accountService.getMetodiPagamento(connection, idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveAllMetodiPagamento(connection, idUtente);
    }

    @Test
    void testGetStoricoPagamenti() throws SQLException {
        // GIVEN
        int idUtente = 20;
        List<DettagliPagamentoBean> listaPagamenti = new ArrayList<>();
        listaPagamenti.add(Mockito.mock(DettagliPagamentoBean.class));

        // CORREZIONE: Passiamo connection
        when(utenteDAO.doRetrievePagamenti(connection, idUtente)).thenReturn(listaPagamenti);

        // WHEN
        // Anche qui serve il metodo overload nel Service: getStoricoPagamenti(Connection con, int id)
        List<DettagliPagamentoBean> risultato = accountService.getStoricoPagamenti(connection, idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrievePagamenti(connection, idUtente);
    }

    @Test
    void testModificaDatiUtente() throws SQLException {
        // GIVEN
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(100);
        utente.setNome("NuovoNomeTest");

        // WHEN
        // Questo metodo 'per i test' esiste già nel tuo Service, quindi è OK
        accountService.modificaDatiUtente(connection, utente);

        // THEN
        // Verifichiamo che il DAO venga chiamato con la connessione giusta
        verify(utenteDAO).doUpdate(eq(connection), eq(utente));
    }
}