package Presentation.AutenticazioneController;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneAccount.UtenteService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock UtenteService serviceMock; // Il nostro Service finto

    LoginServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new LoginServlet();
        servlet.setUtenteService(serviceMock); // INIEZIONE DEL MOCK!
    }

    @Test
    void testDoPost_LoginSuccesso() throws Exception {
        // --- GIVEN ---
        when(request.getParameter("email")).thenReturn("mario@test.it");
        when(request.getParameter("password")).thenReturn("12345");
        when(request.getSession()).thenReturn(session); // La request deve dare una sessione

        UtenteBean utenteTrovato = new UtenteBean();
        utenteTrovato.setEmail("mario@test.it");

        // Il service deve dire "Sì, l'utente esiste"
        when(serviceMock.login("mario@test.it", "12345")).thenReturn(utenteTrovato);

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        // 1. Verifica che l'utente sia messo in sessione
        verify(session).setAttribute("utente", utenteTrovato);
        // 2. Verifica il redirect
        verify(response).sendRedirect(anyString()); // Verifica che faccia un redirect qualsiasi
    }

    @Test
    void testDoPost_CredenzialiErrate() throws Exception {
        // --- GIVEN ---
        when(request.getParameter("email")).thenReturn("sbagliato@test.it");
        when(request.getParameter("password")).thenReturn("errata");
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(dispatcher);

        // Il service restituisce NULL (login fallito)
        when(serviceMock.login("sbagliato@test.it", "errata")).thenReturn(null);

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        verify(request).setAttribute(anyString(), anyString()); // Setta un messaggio di errore
        verify(dispatcher).forward(request, response); // Rimanda alla pagina di login
    }
}