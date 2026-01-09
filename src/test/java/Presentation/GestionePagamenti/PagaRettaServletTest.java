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
        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idMetodo")).thenReturn("5");
        when(request.getParameter("importo")).thenReturn("25.50");

        servlet.doPost(request, response);

        // Verifica che il service venga chiamato
        verify(serviceMock).pagaRetta(10, 5, 25.50);
        // Verifica redirect di successo
        verify(response).sendRedirect(contains("status=success"));
    }

    @Test
    void testPagamento_ErroreDB() throws Exception {
        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idMetodo")).thenReturn("5");
        when(request.getParameter("importo")).thenReturn("25.50");

        // Simuliamo eccezione SQL dal service
        doThrow(new SQLException("Errore Connessione")).when(serviceMock).pagaRetta(anyInt(), anyInt(), anyDouble());

        servlet.doPost(request, response);

        // Verifica gestione errore
        verify(request).setAttribute(eq("error"), contains("Errore tecnico"));
        verify(dispatcher).forward(request, response);
    }
}