package Presentation.AutenticazioneController;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneAccount.UtenteService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrazioneServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock UtenteService serviceMock;

    @Test
    void testDoPost_RegistrazioneOk() throws Exception {
        // 1. Setup Servlet e Service Mock
        RegistrazioneServlet servlet = new RegistrazioneServlet();
        servlet.setUtenteService(serviceMock);

        // 2. Simuliamo TUTTI i parametri del form
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("username")).thenReturn("mariorossi");
        when(request.getParameter("dataNascita")).thenReturn("1990-01-01"); // Formato ISO per LocalDate
        when(request.getParameter("email")).thenReturn("mario@email.it");
        when(request.getParameter("password")).thenReturn("pass123");
        when(request.getParameter("prefisso")).thenReturn("+39");
        when(request.getParameter("telefono")).thenReturn("3331234567");

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        // 3. Esecuzione
        servlet.doPost(request, response);

        // 4. Verifiche
        // Verifichiamo che il service sia stato chiamato per registrare
        verify(serviceMock).registraUtente(any(UtenteBean.class));

        // Verifichiamo che l'utente sia loggato automaticamente (messo in sessione)
        verify(session).setAttribute(eq("utente"), any(UtenteBean.class));

        // Verifichiamo il forward alla pagina di successo
        verify(dispatcher).forward(request, response);
    }
}