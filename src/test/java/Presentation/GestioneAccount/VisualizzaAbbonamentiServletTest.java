package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaAbbonamentiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock;

    VisualizzaAbbonamentiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaAbbonamentiServlet();
        servlet.setAccountService(serviceMock); // Iniettiamo il mock
    }

    @Test
    void testDoGet_Successo() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        // Prepariamo la lista finta
        List<GruppoBean> gruppi = new ArrayList<>();
        gruppi.add(Mockito.mock(GruppoBean.class)); // Mockiamo il bean astratto
        when(serviceMock.getGruppiIscritto(1)).thenReturn(gruppi);

        when(request.getRequestDispatcher("/Abbonamenti.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("ClubIscrizioni"), eq(gruppi));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_SessioneScaduta() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect(contains("/login.jsp"));
    }

    @Test
    void testDoGet_ErroreSQL() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(serviceMock.getGruppiIscritto(anyInt())).thenThrow(new SQLException("Errore DB"));
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("errore"), anyString());
        verify(dispatcher).forward(request, response);
    }
}