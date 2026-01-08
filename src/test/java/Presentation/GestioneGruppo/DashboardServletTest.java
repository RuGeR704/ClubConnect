package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.AssociazioneBean;
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

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService gruppoServiceMock;
    @Mock EventoService eventoServiceMock;

    DashboardServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new DashboardServlet();
        servlet.setGruppoService(gruppoServiceMock);
        servlet.setEventoService(eventoServiceMock);
    }

    @Test
    void testDoGet_Successo() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getParameter("idGruppo")).thenReturn("5");

        when(gruppoServiceMock.recuperaGruppo(5)).thenReturn(new AssociazioneBean());
        when(gruppoServiceMock.contaMembri(5)).thenReturn(10);
        when(gruppoServiceMock.recuperaSociDelGruppo(5)).thenReturn(new ArrayList<>());
        when(eventoServiceMock.retrieveAllEventi()).thenReturn(new ArrayList<>());

        when(request.getRequestDispatcher("gestioneGruppo.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("totaleMembri"), eq(10));
        verify(dispatcher).forward(request, response);
    }
}