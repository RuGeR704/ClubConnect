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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImpostaAbbonamentoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock PagamentoService serviceMock;

    ImpostaAbbonamentoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new ImpostaAbbonamentoServlet();
        servlet.setPagamentoService(serviceMock);

        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testGestore_Successo() throws Exception {
        setupParams("10", "50.0", "30");
        // Simuliamo che l'utente sia gestore
        when(serviceMock.isUtenteGestore(1, 10)).thenReturn(true);

        servlet.doPost(request, response);

        verify(serviceMock).impostaAbbonamento(10, 50.0, 30);
        verify(response).sendRedirect(contains("VisualizzaGruppoServlet"));
    }

    @Test
    void testNonGestore_AccessoNegato() throws Exception {
        setupParams("10", "50.0", "30");
        // Simuliamo che l'utente NON sia gestore
        when(serviceMock.isUtenteGestore(1, 10)).thenReturn(false);

        servlet.doPost(request, response);

        verify(serviceMock, never()).impostaAbbonamento(anyInt(), anyDouble(), anyInt());
        verify(response).sendRedirect(contains("error_permissions")); // O quello che hai messo nella servlet
    }

    @Test
    void testParametriMancanti() throws Exception {
        when(request.getParameter("idGruppo")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("Tutti i campi sono obbligatori"));
    }

    private void setupParams(String id, String imp, String freq) {
        when(request.getParameter("idGruppo")).thenReturn(id);
        when(request.getParameter("importo")).thenReturn(imp);
        when(request.getParameter("frequenza")).thenReturn(freq);
    }
}