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

        verify(serviceMock).pagaRetta(10, 5, 25.50);
        verify(response).sendRedirect(contains("status=success"));
    }

    @Test
    void testPagamento_ErroreDB() throws Exception {
        // 1. SETUP PARAMETRI VALIDI
        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idMetodo")).thenReturn("5");
        when(request.getParameter("importo")).thenReturn("25.50");

        // 2. SIMULA ERRORE SQL
        doThrow(new SQLException("Errore Connessione"))
                .when(serviceMock).pagaRetta(anyInt(), anyInt(), anyDouble());

        // 3. ESECUZIONE
        servlet.doPost(request, response);

        // 4. VERIFICHE CORRETTE (Forward con Attributo)

        // Verifica che venga settato il messaggio di errore
        verify(request).setAttribute(eq("error"), contains("Errore tecnico"));

        // Verifica che venga richiesto il dispatcher per la JSP specifica indicata da te
        verify(request).getRequestDispatcher("pagine_gruppo.jsp");

        // Verifica che venga eseguito il forward (NON il redirect)
        verify(dispatcher).forward(request, response);
    }
}