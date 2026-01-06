package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaMetodiPagamentoServletTest {

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
    }

    @Test
    void testDoGet_Successo() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(5);
        when(session.getAttribute("utente")).thenReturn(utente);

        List<MetodoPagamentoBean> metodi = new ArrayList<>();
        metodi.add(Mockito.mock(MetodoPagamentoBean.class));

        when(serviceMock.getMetodiPagamento(5)).thenReturn(metodi);
        when(request.getRequestDispatcher("/gestioneUtente.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("metodipagamento"), eq(metodi));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_ErroreSQL() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        // Simuliamo errore DB
        when(serviceMock.getMetodiPagamento(anyInt())).thenThrow(new SQLException("Errore"));

        // WHEN
        servlet.doGet(request, response);

        // THEN
        // Nel tuo codice originale qui fai REDIRECT a error.jsp, non forward
        verify(response).sendRedirect(contains("/error.jsp"));
    }
}