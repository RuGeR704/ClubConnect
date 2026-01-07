package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
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

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaEventoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock EventoService serviceMock;

    VisualizzaEventoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaEventoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoGet_VisualizzaDettaglio() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getParameter("id")).thenReturn("99");

        // Mock Evento
        EventoBean evento = new EventoBean();
        evento.setId_evento(99);
        evento.setId_gruppo(10);
        when(serviceMock.retrieveEvento(99)).thenReturn(evento);

        // Mock Gruppo Astratto (FIX)
        GruppoBean gruppoFinto = Mockito.mock(GruppoBean.class);
        when(serviceMock.retrieveGruppo(10)).thenReturn(gruppoFinto);

        when(request.getRequestDispatcher(contains("visualizzaEvento"))).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("evento"), eq(evento));
        verify(request).setAttribute(eq("gruppo"), eq(gruppoFinto));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_NonTrovato() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("id")).thenReturn("999");
        when(serviceMock.retrieveEvento(999)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Evento non trovato");
    }
}