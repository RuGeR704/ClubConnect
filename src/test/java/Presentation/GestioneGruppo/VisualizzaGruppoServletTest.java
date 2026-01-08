package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService gruppoService;
    @Mock ComunicazioneService comService;

    VisualizzaGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaGruppoServlet();
        servlet.setGruppoService(gruppoService);
        servlet.setComunicazioneService(comService);
    }

    @Test
    void testDoGet_MostraGruppo() throws Exception {
        when(request.getParameter("id")).thenReturn("5");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());

        when(gruppoService.recuperaGruppo(5)).thenReturn(new AssociazioneBean());
        when(comService.recuperaComunicazioniPerUtente(anyInt())).thenReturn(new ArrayList<>());
        when(gruppoService.isUtenteIscritto(eq(5), anyInt())).thenReturn(true);
        when(gruppoService.isUtenteGestore(eq(5), anyInt())).thenReturn(false);

        when(request.getRequestDispatcher("paginaGruppo.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("isIscritto"), eq(true));
        verify(request).setAttribute(eq("isAdmin"), eq(false));
        verify(dispatcher).forward(request, response);
    }
}