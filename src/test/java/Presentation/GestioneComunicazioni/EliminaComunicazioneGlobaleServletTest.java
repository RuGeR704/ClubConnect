package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminaComunicazioneGlobaleServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ComunicazioneService serviceMock;

    EliminaComunicazioneGlobaleServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new EliminaComunicazioneGlobaleServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_SuccessoAdmin() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        UtenteBean admin = new UtenteBean();
        admin.setIsadmin(true);
        when(session.getAttribute("utente")).thenReturn(admin);

        when(request.getParameter("idComunicazione")).thenReturn("10");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(serviceMock).rimuoviComunicazione(10);
        verify(response).sendRedirect("VisualizzaComunicazioniGlobaliServlet");
    }

    @Test
    void testDoPost_NonAdmin() throws Exception {
        when(request.getSession()).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setIsadmin(false);
        when(session.getAttribute("utente")).thenReturn(utente);

        servlet.doPost(request, response);

        verifyNoInteractions(serviceMock); // Non deve cancellare nulla
        verify(response).sendRedirect("login.jsp");
    }
}