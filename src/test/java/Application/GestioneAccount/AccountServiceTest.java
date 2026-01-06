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

        // SOLUZIONE CLASSE ASTRATTA:
        // Chiediamo a Mockito di creare un'istanza finta di GruppoBean
        GruppoBean gruppoFinto = Mockito.mock(GruppoBean.class);
        listaAttesa.add(gruppoFinto);

        // Istruiamo il DAO
        when(utenteDAO.doRetrieveGruppiIscritto(idUtente)).thenReturn(listaAttesa);

        // WHEN
        // Usiamo il metodo che accetta la Connection
        List<GruppoBean> risultato = accountService.getGruppiIscritto(connection, idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveGruppiIscritto(idUtente);
    }

    @Test
    void testGetGruppiAdmin_Trovati() throws SQLException {
        // GIVEN
        int idUtente = 5;
        List<GruppoBean> listaAdmin = new ArrayList<>();

        // Mockiamo anche qui perché GruppoBean è astratto
        GruppoBean gruppoAdminFinto = Mockito.mock(GruppoBean.class);
        listaAdmin.add(gruppoAdminFinto);

        // Nota: Qui il metodo del DAO accetta la connessione nei parametri (come da tuo codice precedente)
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
        // Usiamo il mock per sicurezza, va bene anche se la classe non è astratta
        listaMetodi.add(Mockito.mock(MetodoPagamentoBean.class));

        when(utenteDAO.doRetrieveAllMetodiPagamento(idUtente)).thenReturn(listaMetodi);

        // WHEN
        List<MetodoPagamentoBean> risultato = accountService.getMetodiPagamento(idUtente); // Qui usa il metodo che chiama il DAO senza Connection esplicita se non l'abbiamo overloadato, oppure usa l'overload se l'hai creato.

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrieveAllMetodiPagamento(idUtente);
    }

    @Test
    void testGetStoricoPagamenti() throws SQLException {
        // GIVEN
        int idUtente = 20;
        List<DettagliPagamentoBean> listaPagamenti = new ArrayList<>();
        listaPagamenti.add(Mockito.mock(DettagliPagamentoBean.class));

        when(utenteDAO.doRetrievePagamenti(idUtente)).thenReturn(listaPagamenti);

        // WHEN
        List<DettagliPagamentoBean> risultato = accountService.getStoricoPagamenti(idUtente);

        // THEN
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(utenteDAO).doRetrievePagamenti(idUtente);
    }

    @Test
    void testModificaDatiUtente() throws SQLException {
        // GIVEN
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(100);
        utente.setNome("NuovoNomeTest");

        // WHEN
        // Passiamo la connessione mockata
        accountService.modificaDatiUtente(connection, utente);

        // THEN
        // Verifichiamo che il DAO venga chiamato con la connessione giusta e l'utente giusto
        verify(utenteDAO).doUpdate(eq(connection), eq(utente));
    }
}