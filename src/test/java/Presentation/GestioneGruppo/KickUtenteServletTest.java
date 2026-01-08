package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KickUtenteServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService serviceMock;

    KickUtenteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new KickUtenteServlet();
        servlet.setGruppoService(serviceMock);
    }

    @Test
    void testDoPost_Successo() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        UtenteBean u = new UtenteBean(); u.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idUtenteDaEspellere")).thenReturn("20");

        when(serviceMock.espelliUtente(10, 20, 1)).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("kickOk"));
    }

    @Test
    void testDoPost_Fallimento() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        UtenteBean u = new UtenteBean(); u.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idUtenteDaEspellere")).thenReturn("20");

        when(serviceMock.espelliUtente(10, 20, 1)).thenReturn(false);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), anyString());
    }
}