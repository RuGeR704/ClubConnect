package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsploraGruppiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService serviceMock;

    EsploraGruppiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new EsploraGruppiServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoGet() throws Exception {
        when(request.getSession()).thenReturn(session);
        UtenteBean u = new UtenteBean(); u.setId_utente(7);
        when(session.getAttribute("utente")).thenReturn(u);

        when(serviceMock.recuperaGruppiNonIscritto(7)).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("esploraGruppi.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("gruppiEsplora"), anyList());
        verify(dispatcher).forward(request, response);
    }
}