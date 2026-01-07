package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.IscrizioneFacade;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IscrizioneEventoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock IscrizioneFacade facadeMock;

    IscrizioneEventoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new IscrizioneEventoServlet();
        servlet.setFacade(facadeMock);
    }

    @Test
    void testDoPost_JoinSuccesso() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getParameter("idEvento")).thenReturn("100");
        when(request.getParameter("action")).thenReturn("join");

        when(facadeMock.iscriviUtente(any(), eq(100))).thenReturn(true);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(facadeMock).iscriviUtente(any(), eq(100));
        verify(session).setAttribute(eq("successo"), anyString());
        verify(response).sendRedirect("feedServlet");
    }

    @Test
    void testDoPost_JoinFallito() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getParameter("idEvento")).thenReturn("100");
        when(request.getParameter("action")).thenReturn("join");

        when(facadeMock.iscriviUtente(any(), eq(100))).thenReturn(false);
        when(request.getRequestDispatcher(contains("visualizzaEvento"))).thenReturn(dispatcher);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(request).setAttribute(eq("errore"), anyString());
        verify(dispatcher).forward(request, response);
    }
}