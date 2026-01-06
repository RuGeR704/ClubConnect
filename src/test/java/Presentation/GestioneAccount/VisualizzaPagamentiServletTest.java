package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaPagamentiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock;

    VisualizzaPagamentiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaPagamentiServlet();
        servlet.setAccountService(serviceMock);
    }

    @Test
    void testDoGet_Successo() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(100);
        when(session.getAttribute("utente")).thenReturn(utente);

        List<DettagliPagamentoBean> pagamenti = new ArrayList<>();
        // Mockiamo anche qui per sicurezza
        pagamenti.add(Mockito.mock(DettagliPagamentoBean.class));

        when(serviceMock.getStoricoPagamenti(100)).thenReturn(pagamenti);
        when(request.getRequestDispatcher("/PagamentiEffettuati.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("Pagamenti"), eq(pagamenti));
        verify(dispatcher).forward(request, response);
    }
}