package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Application.GestioneAccount.UtenteBean;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisualizzaMetodidiPagamentoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock;

    VisualizzaMetodiPagamentoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaMetodiPagamentoServlet();
        servlet.setAccountService(serviceMock);

        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // FONDAMENTALE: Mockiamo getContextPath() per evitare "null/login.jsp"
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    void testDoGet_Successo() throws Exception {
        // Given
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(10);
        when(session.getAttribute("utente")).thenReturn(utente);

        List<MetodoPagamentoBean> lista = new ArrayList<>();
        when(serviceMock.getMetodiPagamento(10)).thenReturn(lista);

        // When
        servlet.doGet(request, response);

        // Then
        // Verifica che l'attributo sia "metodiPagamento" (camelCase)
        verify(request).setAttribute(eq("metodiPagamento"), eq(lista));
        // Verifica il path corretto della JSP
        verify(request).getRequestDispatcher("/gestioneUtente.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_NonLoggato() throws Exception {
        when(session.getAttribute("utente")).thenReturn(null);

        servlet.doGet(request, response);

        // Verifica il redirect corretto (dato che contextPath è vuoto, sarà /login.jsp)
        verify(response).sendRedirect("/login.jsp");
    }
}