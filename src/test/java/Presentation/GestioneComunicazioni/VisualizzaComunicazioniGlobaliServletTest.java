package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaComunicazioniGlobaliServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock ComunicazioneService serviceMock;

    VisualizzaComunicazioniGlobaliServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaComunicazioniGlobaliServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoGet_FiltraSoloGlobali() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());

        List<ComunicazioniBean> lista = new ArrayList<>();
        ComunicazioniBean c1 = new ComunicazioniBean(); c1.setIsglobal(true);
        ComunicazioniBean c2 = new ComunicazioniBean(); c2.setIsglobal(false);
        lista.add(c1);
        lista.add(c2);

        when(serviceMock.recuperaTutteLeComunicazioni()).thenReturn(lista);
        when(request.getRequestDispatcher("bachecaGlobale.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        // Verifica che passi solo la lista con 1 elemento (quello globale)
        verify(request).setAttribute(eq("listaComunicazioni"), argThat(list -> ((List)list).size() == 1));
        verify(dispatcher).forward(request, response);
    }
}