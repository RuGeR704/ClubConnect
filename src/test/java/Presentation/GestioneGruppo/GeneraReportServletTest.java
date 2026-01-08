package Presentation.GestioneGruppo;

import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneraReportServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock ServletOutputStream outputStream;
    @Mock GruppoService gruppoService;
    @Mock EventoService eventoService;

    GeneraReportServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new GeneraReportServlet();
        servlet.setGruppoService(gruppoService);
        servlet.setEventoService(eventoService);
    }

    @Test
    void testDoGet_GeneraPdf() throws Exception {
        when(request.getParameter("idGruppo")).thenReturn("10");

        AssociazioneBean g = new AssociazioneBean();
        g.setNome("Gruppo Test");
        g.setSettore("Test");
        when(gruppoService.recuperaGruppo(10)).thenReturn(g);
        when(gruppoService.recuperaSociDelGruppo(10)).thenReturn(new ArrayList<>());
        when(eventoService.retrieveAllEventi()).thenReturn(new ArrayList<>());

        when(response.getOutputStream()).thenReturn(outputStream);

        servlet.doGet(request, response);

        verify(response).setContentType("application/pdf");
        // Verifica che abbia provato a scrivere qualcosa
        // (iText scrive nello stream, quindi se non esplode il test è passato)
    }
}