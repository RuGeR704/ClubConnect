package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreaEventoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock ServletContext context;
    @Mock Part filePart;
    @Mock EventoService serviceMock;

    CreaEventoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new CreaEventoServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_CreaEventoConFoto() throws Exception {
        // GIVEN
        when(request.getParameter("idGruppo")).thenReturn("5");
        when(request.getParameter("nomeEvento")).thenReturn("Concerto Rock");
        when(request.getParameter("descrizione")).thenReturn("Live Music");
        when(request.getParameter("dataOraEvento")).thenReturn("2025-12-31T22:00");
        when(request.getParameter("costo")).thenReturn("15.50");
        when(request.getParameter("capienza")).thenReturn("500"); // Capienza 500

        // Simulazione Upload
        when(request.getPart("foto")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(1024L);
        when(filePart.getSubmittedFileName()).thenReturn("poster.jpg");
        when(context.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));

        // WHEN
        servlet.doPost(request, response);

        // THEN
        ArgumentCaptor<EventoBean> captor = ArgumentCaptor.forClass(EventoBean.class);
        verify(serviceMock).creaEvento(captor.capture());

        EventoBean evento = captor.getValue();
        assertEquals("Concerto Rock", evento.getNome());
        assertEquals(5, evento.getId_gruppo());
        assertEquals(500, evento.getCapienza_massima());

        // VERIFICA AGGIUNTA: I posti disponibili devono essere uguali alla capienza!
        assertEquals(500, evento.getPosti_disponibili());

        assertTrue(evento.getFoto().contains("poster.jpg"));

        verify(response).sendRedirect(contains("VisualizzaGruppoServlet?id=5"));
    }

    @Test
    void testDoPost_ErroreDatiMancanti() throws Exception {
        when(request.getParameter("idGruppo")).thenReturn("5");
        when(request.getParameter("nomeEvento")).thenReturn(null);

        servlet.doPost(request, response);

        verify(serviceMock, never()).creaEvento(any());
        verify(response).sendRedirect(contains("errore="));
    }
}