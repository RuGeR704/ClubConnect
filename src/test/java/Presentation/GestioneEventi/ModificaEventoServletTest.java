package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificaEventoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock EventoService serviceMock;

    ModificaEventoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new ModificaEventoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_ModificaOK() throws Exception {
        // GIVEN
        when(request.getParameter("idEvento")).thenReturn("50");
        when(request.getParameter("id_gruppo")).thenReturn("5");
        when(request.getParameter("nome")).thenReturn("Nuovo Nome");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        when(request.getParameter("foto")).thenReturn("img.jpg");
        when(request.getParameter("costo")).thenReturn("20.0");
        when(request.getParameter("posti_disponibili")).thenReturn("100");
        when(request.getParameter("capienza_massima")).thenReturn("200");
        when(request.getParameter("data_ora")).thenReturn("2025-01-01T10:00");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        ArgumentCaptor<EventoBean> captor = ArgumentCaptor.forClass(EventoBean.class);
        verify(serviceMock).modificaEvento(captor.capture());

        EventoBean evento = captor.getValue();
        assertEquals(50, evento.getId_evento());
        assertEquals("Nuovo Nome", evento.getNome());

        verify(response).sendRedirect(contains("visualizzaEvento.jsp?id=50"));
    }
}