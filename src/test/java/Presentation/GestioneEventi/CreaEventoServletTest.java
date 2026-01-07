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
        // Sovrascriviamo getServletContext per evitare IllegalStateException durante l'upload
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
        when(request.getParameter("capienza")).thenReturn("500");

        // Simulazione Upload
        when(request.getPart("foto")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(1024L); // File presente
        when(filePart.getSubmittedFileName()).thenReturn("poster.jpg");

        // Simuliamo il path temporaneo del sistema
        when(context.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // 1. Verifichiamo che il service sia stato chiamato
        ArgumentCaptor<EventoBean> captor = ArgumentCaptor.forClass(EventoBean.class);
        verify(serviceMock).creaEvento(captor.capture());

        EventoBean evento = captor.getValue();
        assertEquals("Concerto Rock", evento.getNome());
        assertEquals(5, evento.getId_gruppo());
        // Verifichiamo che il path della foto sia stato impostato
        assertTrue(evento.getFoto().contains("poster.jpg"));

        // 2. Verifichiamo il redirect finale
        verify(response).sendRedirect(contains("VisualizzaGruppoServlet?id=5"));
    }

    @Test
    void testDoPost_ErroreDatiMancanti() throws Exception {
        // GIVEN
        when(request.getParameter("idGruppo")).thenReturn("5");
        when(request.getParameter("nomeEvento")).thenReturn(null); // Nome mancante -> Errore

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(serviceMock, never()).creaEvento(any());
        verify(response).sendRedirect(contains("errore="));
    }
}