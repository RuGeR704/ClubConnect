package Presentation.GestioneEventi;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaCalendarioEventiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock EventoService serviceMock;

    VisualizzaCalendarioEventiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaCalendarioEventiServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoGet_MostraCalendario() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        UtenteBean u = new UtenteBean();
        u.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(u);

        when(serviceMock.retrieveAllEventi()).thenReturn(new ArrayList<>());
        when(serviceMock.retrieveEventiUtente(1)).thenReturn(new ArrayList<>());

        when(request.getRequestDispatcher(contains("visualizzaCalendario"))).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("eventi"), anyList());
        verify(request).setAttribute(eq("eventiUtente"), anyList());
        verify(dispatcher).forward(request, response);
    }
}