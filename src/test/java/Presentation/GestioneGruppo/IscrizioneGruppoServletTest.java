package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoService;
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
class IscrizioneGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock GruppoService serviceMock;

    IscrizioneGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new IscrizioneGruppoServlet();
        servlet.setGruppoService(serviceMock);
    }

    @Test
    void testDoPost_Iscrizione() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean u = new UtenteBean(); u.setId_utente(5);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getParameter("id_gruppo")).thenReturn("100");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(serviceMock).iscriviUtenteAlGruppo(5, 100);
        verify(response).sendRedirect(contains("VisualizzaGruppoServlet?id=100"));
    }
}