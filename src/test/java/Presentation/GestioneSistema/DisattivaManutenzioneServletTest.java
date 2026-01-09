package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisattivaManutenzioneServletTest {
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ServletContext servletContext;
    @Mock GestioneSistemaInterface sistemaMock;

    @Test
    void testDisattiva_Successo() throws Exception {
        DisattivaManutenzioneServlet servlet = new DisattivaManutenzioneServlet();
        servlet.setSistemaTest(sistemaMock);

        DisattivaManutenzioneServlet spyServlet = spy(servlet);
        doReturn(servletContext).when(spyServlet).getServletContext();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());

        spyServlet.doPost(request, response);

        verify(sistemaMock).disattivaManutenzione();
        verify(servletContext).setAttribute("manutenzione", false);
        verify(response).sendRedirect(contains("VisualizzaListaClientiServlet"));
    }
}