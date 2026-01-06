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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaIscrizioniGruppiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock;

    VisualizzaIscrizioniGruppiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaIscrizioniGruppiServlet();
        servlet.setAccountService(serviceMock);
    }

    @Test
    void testDoGet_Successo() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(10);
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getRequestDispatcher("/GruppiIscritto.jsp")).thenReturn(dispatcher);

        // Simuliamo le due liste
        List<GruppoBean> iscritti = new ArrayList<>();
        iscritti.add(Mockito.mock(GruppoBean.class));

        List<GruppoBean> admin = new ArrayList<>();
        admin.add(Mockito.mock(GruppoBean.class));

        when(serviceMock.getGruppiIscritto(10)).thenReturn(iscritti);
        when(serviceMock.getGruppiAdmin(10)).thenReturn(admin);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("gruppiIscritto"), eq(iscritti));
        verify(request).setAttribute(eq("gruppiAdmin"), eq(admin));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_SessioneNull() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        servlet.doGet(request, response);
        verify(response).sendRedirect(contains("/login.jsp"));
    }
}