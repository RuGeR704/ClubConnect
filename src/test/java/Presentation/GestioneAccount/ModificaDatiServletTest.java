package Presentation.GestioneAccount;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificaDatiServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock; // Il nostro Service finto

    ModificaDatiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new ModificaDatiServlet();
        servlet.setAccountService(serviceMock); // Iniettiamo il mock nel controller
    }

    @Test
    void testDoPost_Successo() throws Exception {
        // --- GIVEN ---
        // 1. Sessione valida con utente loggato
        when(request.getSession(false)).thenReturn(session);
        UtenteBean utenteSessione = new UtenteBean();
        utenteSessione.setId_utente(10);
        utenteSessione.setStato(1);
        utenteSessione.setIsadmin(false);
        when(session.getAttribute("utente")).thenReturn(utenteSessione);

        // 2. Parametri del form TUTTI VALIDI
        when(request.getParameter("username")).thenReturn("marioRossi99");
        when(request.getParameter("datanascita")).thenReturn("1990-05-20"); // Formato YYYY-MM-DD
        when(request.getParameter("password")).thenReturn("PasswordSicura1!");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("cellulare")).thenReturn("3331234567");

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        // 1. Verifichiamo che il service sia stato chiamato per aggiornare il DB
        verify(serviceMock).modificaDatiUtente(any(UtenteBean.class));

        // 2. Verifichiamo che l'utente aggiornato sia stato rimesso in sessione
        verify(session).setAttribute(eq("utente"), any(UtenteBean.class));

        // 3. Verifichiamo il redirect alla pagina del profilo
        verify(response).sendRedirect(contains("/AccountServlet"));
    }

    @Test
    void testDoPost_ErroreValidazione_EmailInvalida() throws Exception {
        // --- GIVEN ---
        // 1. Mockiamo ENTRAMBE le versioni di getSession
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session); // <--- QUESTA RIGA MANCAVA!

        UtenteBean utenteSessione = new UtenteBean();
        utenteSessione.setId_utente(10);
        when(session.getAttribute("utente")).thenReturn(utenteSessione);

        // 2. Mockiamo tutti i parametri
        when(request.getParameter("username")).thenReturn("marioRossi99");
        when(request.getParameter("datanascita")).thenReturn("1990-01-01");
        when(request.getParameter("email")).thenReturn("emailSbagliata.it"); // <--- Errore qui
        when(request.getParameter("password")).thenReturn("pass");
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("cellulare")).thenReturn("3331234567");

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        verify(serviceMock, never()).modificaDatiUtente(any());
        verify(session).setAttribute(eq("errorMsg"), contains("Email non valida"));
        verify(response).sendRedirect(contains("ModificaDatiServlet?id_utente=10"));
    }

    @Test
    void testDoGet_VisualizzaForm() throws Exception {
        // Test semplice del doGet che mostra il form
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getRequestDispatcher("/WEB-INF/ModificaDatiForm.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_SessioneScaduta() throws Exception {
        // Se la sessione è null
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        // Deve mandare al login
        verify(response).sendRedirect(contains("/login.jsp"));
        // E non deve toccare il service
        verifyNoInteractions(serviceMock);
    }
}