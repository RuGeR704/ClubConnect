package Presentation.GestionePagamenti;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RimuoviMetodoPagamentoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock AccountService serviceMock;

    RimuoviMetodoPagamentoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new RimuoviMetodoPagamentoServlet();
        servlet.setAccountService(serviceMock);

        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);
    }

    @Test
    void testRimuovi_Successo() throws Exception {
        when(request.getParameter("id_metodo_pagamento")).thenReturn("123");

        servlet.doPost(request, response);

        verify(serviceMock).rimuoviMetodoPagamento(123, 1);
        verify(response).sendRedirect("VisualizzaMetodidiPagamentoServlet");
    }

    @Test
    void testRimuovi_ParametroMancante() throws Exception {
        when(request.getParameter("id_metodo_pagamento")).thenReturn("");

        servlet.doPost(request, response);

        verify(serviceMock, never()).rimuoviMetodoPagamento(anyInt(), anyInt());
        verify(response).sendRedirect("VisualizzaMetodidiPagamentoServlet");
    }
}