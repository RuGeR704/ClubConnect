package Presentation.AutenticazioneController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;

    @Test
    void testDoGet_Logout() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        // Diciamo che la sessione esiste
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/MyApp");

        // Eseguiamo il logout
        servlet.doGet(request, response);

        // Verifichiamo che la sessione venga distrutta
        verify(session).invalidate();

        // Verifichiamo il redirect alla home o login
        verify(response).sendRedirect("/MyApp/index.jsp");
    }
}