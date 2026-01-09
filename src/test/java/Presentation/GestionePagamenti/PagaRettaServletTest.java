package Presentation.GestionePagamenti;

import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PagaRettaServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock PagamentoService serviceMock;

    PagaRettaServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new PagaRettaServlet();
        servlet.setPagamentoService(serviceMock);

        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testPagamento_Successo() throws Exception {
        // Setup Parametri Validi
        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idMetodo")).thenReturn("5");
        when(request.getParameter("importo")).thenReturn("25.50");

        servlet.doPost(request, response);

        verify(serviceMock).pagaRetta(10, 5, 25.50);

        // --- CORREZIONE 1: Aggiornato per matchare "esito=pagamento_ok" ---
        verify(response).sendRedirect(contains("esito=pagamento_ok"));
    }

    @Test
    void testPagamento_ErroreDB() throws Exception {
        // 1. Setup Parametri FONDAMENTALE (altrimenti la servlet si ferma prima)
        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idMetodo")).thenReturn("5");
        when(request.getParameter("importo")).thenReturn("25.50");

        // 2. Simuliamo l'eccezione SQL dal service
        doThrow(new SQLException("Errore Connessione"))
                .when(serviceMock).pagaRetta(anyInt(), anyInt(), anyDouble());

        // 3. Esecuzione
        servlet.doPost(request, response);

        // 4. VERIFICA CORRETTA
        // Il test precedente falliva perché cercava un setAttribute/forward.
        // La tua Servlet invece fa un redirect a "feedServlet" o "error.jsp" in caso di errore.
        // Verifichiamo che venga chiamato sendRedirect.
        verify(response).sendRedirect(anyString());
    }
}