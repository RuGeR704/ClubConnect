package Presentation.GestioneGruppo;

import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.GruppoService;
import Application.GestionePagamenti.GestionePagamentiBean;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaSociServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService gruppoService;
    @Mock GestionePagamentiBean pagamentiService;

    VisualizzaSociServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaSociServlet();
        servlet.setGruppoService(gruppoService);
        servlet.setPagamentiService(pagamentiService);
    }

    @Test
    void testDoGet_Visualizza() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new Object()); // Basta che non sia null
        when(request.getParameter("id")).thenReturn("3");

        when(gruppoService.recuperaGruppo(3)).thenReturn(new AssociazioneBean());
        when(gruppoService.recuperaSociDelGruppo(3)).thenReturn(new ArrayList<>());

        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("listaSoci"), any());
        verify(dispatcher).forward(request, response);
    }
}