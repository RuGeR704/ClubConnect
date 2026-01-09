package Presentation.GestioneEventi;

import Application.GestioneEventi.EventoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RimuoviEventoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock EventoService serviceMock;

    RimuoviEventoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new RimuoviEventoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_RimuoviSuccesso() throws Exception {
        when(request.getParameter("idEvento")).thenReturn("123");

        servlet.doPost(request, response);

        verify(serviceMock).rimuoviEvento(123);
        verify(response).sendRedirect(contains("VisualizzaCalendarioEventiServlet"));
    }
}