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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Fondamentale per evitare errori di stub non usati
class BannaUtenteServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock GestioneSistemaInterface sistemaMock;

    BannaUtenteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new BannaUtenteServlet();
        servlet.setSistemaTest(sistemaMock); // Iniezione del Mock

        when(request.getSession()).thenReturn(session);

        // Setup Utente Admin in sessione (Pre-condizione per arrivare al Ban)
        UtenteBean admin = new UtenteBean();
        admin.setIsadmin(true);
        when(session.getAttribute("utente")).thenReturn(admin);
    }

    /**
     * TCS ID: TC 3.1_1
     * Scenario: L'utente è già bannato.
     * [cite_start]Oracolo: Errore "L'utente è già bannato"[cite: 750].
     */
    @Test
    void testBanna_GiaBannato() throws Exception {
        // GIVEN
        when(request.getParameter("idUtente")).thenReturn("10");
        when(request.getParameter("motivazione")).thenReturn("Comportamento scorretto");

        // Simuliamo che il Service lanci eccezione o gestisca l'errore logico
        // Assumiamo che il Bean lanci IllegalArgumentException se l'utente è già stato 0
        doThrow(new IllegalArgumentException("L'utente è già bannato"))
                .when(sistemaMock).bannaUtente(10);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Verifica che venga restituito l'errore specifico previsto dal TCS
        verify(response).sendRedirect(contains("error=GiaBannato"));
    }

    /**
     * TCS ID: TC 3.2_1 (Caso Errore)
     * Scenario: La motivazione è vuota.
     * [cite_start]Oracolo: Errore "La motivazione non può risultare vuota"[cite: 757].
     */
    @Test
    void testBanna_MotivazioneVuota() throws Exception {
        // GIVEN
        when(request.getParameter("idUtente")).thenReturn("10");
        when(request.getParameter("motivazione")).thenReturn(""); // Stringa vuota

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Il service NON deve essere chiamato (validazione lato Servlet)
        verify(sistemaMock, never()).bannaUtente(anyInt());

        // Verifica redirect con errore
        verify(response).sendRedirect(contains("error=MotivazioneVuota"));
    }

    /**
     * TCS ID: TC 3.2_1 (Caso Successo)
     * Scenario: Input validi.
     * [cite_start]Oracolo: "Corretto: Utente bannato con successo"[cite: 758].
     */
    @Test
    void testBanna_Successo() throws Exception {
        // GIVEN
        when(request.getParameter("idUtente")).thenReturn("10");
        when(request.getParameter("motivazione")).thenReturn("Violazione termini");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Verifica che il metodo del service venga chiamato correttamente
        verify(sistemaMock).bannaUtente(10);

        // Verifica il messaggio di successo
        verify(response).sendRedirect(contains("msg=UtenteBannato"));
    }

    /**
     * Test Extra (Safety): ID Utente non valido o nullo.
     */
    @Test
    void testBanna_IdNonValido() throws Exception {
        // GIVEN
        when(request.getParameter("idUtente")).thenReturn(null); // o "abc"

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(sistemaMock, never()).bannaUtente(anyInt());
        // Deve tornare alla lista o dare errore generico
        verify(response).sendRedirect(contains("VisualizzaListaClientiServlet"));
    }
}