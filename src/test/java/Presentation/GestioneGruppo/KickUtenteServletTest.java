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

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KickUtenteServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock GruppoService serviceMock;

    KickUtenteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new KickUtenteServlet();
        servlet.setGruppoService(serviceMock);
    }

    @Test
    void testDoPost_Successo() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean gestore = new UtenteBean(); gestore.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(gestore);

        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idUtente")).thenReturn("20"); // Nota: parametro cambiato in 'idUtente'

        // Il service conferma l'espulsione
        when(serviceMock.espelliUtente(10, 20, 1)).thenReturn(true);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Verifica il redirect corretto (VisualizzaSociServlet + parametro success)
        verify(response).sendRedirect(contains("VisualizzaSociServlet?id=10&success=kickOk"));
    }

    @Test
    void testDoPost_Fallimento() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean gestore = new UtenteBean(); gestore.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(gestore);

        when(request.getParameter("idGruppo")).thenReturn("10");
        when(request.getParameter("idUtente")).thenReturn("20");

        // Il service nega l'espulsione (es. non gestore)
        when(serviceMock.espelliUtente(10, 20, 1)).thenReturn(false);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Verifica il redirect di errore
        verify(response).sendRedirect(contains("VisualizzaSociServlet?id=10&error=kickFail"));
    }
}