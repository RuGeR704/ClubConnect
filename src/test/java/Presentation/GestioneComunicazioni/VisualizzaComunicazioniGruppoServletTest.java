package Presentation.GestioneComunicazioni;

import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
class VisualizzaComunicazioniGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock RequestDispatcher dispatcher;
    @Mock ComunicazioneService serviceMock;

    VisualizzaComunicazioniGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaComunicazioniGruppoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoGet_FiltraPerGruppo() throws Exception {
        // GIVEN
        when(request.getParameter("idGruppo")).thenReturn("5");

        List<ComunicazioniBean> lista = new ArrayList<>();
        ComunicazioniBean c1 = new ComunicazioniBean(); c1.setId_gruppo(5);
        ComunicazioniBean c2 = new ComunicazioniBean(); c2.setId_gruppo(99);
        lista.add(c1);
        lista.add(c2);

        when(serviceMock.recuperaTutteLeComunicazioni()).thenReturn(lista);
        when(request.getRequestDispatcher("paginaGruppo.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("listaComunicazioniGruppo"), argThat(list -> {
            List<ComunicazioniBean> l = (List<ComunicazioniBean>) list;
            return l.size() == 1 && l.get(0).getId_gruppo() == 5;
        }));
        verify(dispatcher).forward(request, response);
    }
}