package Presentation.GestioneAccount;

import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisualizzaProfiloServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;

    @Test
    void testDoGet_Visualizza() throws Exception {
        // Setup Servlet semplice (non ha service)
        VisualizzaProfiloServlet servlet = new VisualizzaProfiloServlet();

        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        when(session.getAttribute("utente")).thenReturn(utente);
        when(request.getRequestDispatcher("/Profilo.jsp")).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        verify(request).setAttribute(eq("utente"), eq(utente));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_NoSession() throws Exception {
        VisualizzaProfiloServlet servlet = new VisualizzaProfiloServlet();
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect(contains("/login.jsp"));
    }
}