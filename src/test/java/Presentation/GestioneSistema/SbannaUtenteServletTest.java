package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SbannaUtenteServletTest {
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock GestioneSistemaInterface sistemaMock;

    SbannaUtenteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new SbannaUtenteServlet();
        servlet.setSistemaTest(sistemaMock);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
    }

    @Test
    void testSbanna_Successo() throws Exception {
        when(request.getParameter("idUtente")).thenReturn("10");

        servlet.doPost(request, response);

        verify(sistemaMock).sbannaUtente(10);
        verify(response).sendRedirect(contains("UtenteSbannato")); // o UtenteAttivato
    }
}