package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import jakarta.servlet.ServletContext;
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
class AttivaManutenzioneServletTest {
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ServletContext servletContext;
    @Mock GestioneSistemaInterface sistemaMock;

    AttivaManutenzioneServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new AttivaManutenzioneServlet();
        servlet.setSistemaTest(sistemaMock);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        // Simula getServletContext() poiché è un metodo di HttpServlet
        // Nota: Per testare getServletContext() in JUnit puro a volte serve mocking complesso.
        // Qui ci focalizziamo sulla chiamata al service e al redirect.
    }

    @Test
    void testAttiva_Successo() throws Exception {
        // Setup per bypassare getServletContext() se dà problemi nei test unitari puri
        // Creiamo una classe parziale o usiamo Mockito spy se necessario.
        // Per semplicità qui testiamo che chiami il service.

        // Mock parziale per getServletContext
        AttivaManutenzioneServlet spyServlet = spy(servlet);
        doReturn(servletContext).when(spyServlet).getServletContext();

        spyServlet.doPost(request, response);

        verify(sistemaMock).attivaManutenzione();
        verify(servletContext).setAttribute("manutenzione", true);
        verify(response).sendRedirect(contains("VisualizzaListaClientiServlet"));
    }
}