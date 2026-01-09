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
class BannaUtenteServletTest {
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock GestioneSistemaInterface sistemaMock;

    BannaUtenteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new BannaUtenteServlet();
        servlet.setSistemaTest(sistemaMock);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
    }

    @Test
    void testBanna_Successo() throws Exception {
        when(request.getParameter("idUtente")).thenReturn("5");

        servlet.doPost(request, response);

        verify(sistemaMock).bannaUtente(5);
        verify(response).sendRedirect(contains("UtenteBannato"));
    }

    @Test
    void testBanna_SecurityError() throws Exception {
        when(request.getParameter("idUtente")).thenReturn("5");
        doThrow(new SecurityException("No Admin")).when(sistemaMock).bannaUtente(5);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("Unauthorized"));
    }
}