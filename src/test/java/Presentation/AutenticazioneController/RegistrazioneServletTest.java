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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistrazioneServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock UtenteService serviceMock;

    RegistrazioneServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new RegistrazioneServlet();
        servlet.setUtenteService(serviceMock);

        // --- AGGIUNTO PER GESTIRE LA NUOVA LOGICA DI SESSIONE ---

        // 1. Quando la servlet chiede una nuova sessione: getSession(true) -> restituisci il mock
        when(request.getSession(true)).thenReturn(session);

        // 2. Quando la servlet controlla se c'è una vecchia sessione: getSession(false) -> restituisci null (simuliamo che non ci sia)
        when(request.getSession(false)).thenReturn(null);

        // Manteniamo questo per sicurezza (compatibilità con chiamate standard)
        when(request.getSession()).thenReturn(session);

        // --------------------------------------------------------

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoPost_RegistrazioneOk() throws Exception {
        // Happy Path (TC1.8)
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("username")).thenReturn("mariorossi");
        when(request.getParameter("dataNascita")).thenReturn("1990-01-01");
        when(request.getParameter("email")).thenReturn("mario@email.it");
        when(request.getParameter("password")).thenReturn("pass123");
        when(request.getParameter("prefisso")).thenReturn("+39");
        when(request.getParameter("telefono")).thenReturn("3331234567");

        servlet.doPost(request, response);

        verify(response).sendRedirect("feedServlet");
    }

    /**
     * Test Parametrico per errori di VALIDAZIONE (Input non validi formali)
     */
    @ParameterizedTest(name = "{0}: {8}")
    @CsvSource({
            // Username, Nome, Cognome... (Errori già presenti)
            "TC1.1_1, '', Domenico, Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, Username è vuoto",
            "TC1.2_1, Domz, '', Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, Nome è vuoto",
            "TC1.2_2, Domz, Domenico pasquale antonio marco los tocos giacomo, Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, Nome è troppo lungo",
            "TC1.3_1, Domz, Domenico, '', 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, Cognome è vuoto",
            "TC1.3_2, Domz, Domenico, Ricciardelli de angelis los todos marcello, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, Cognome è troppo lungo",
            "TC1.4_1, Domz, Domenico, Ricciardelli, '', d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, data di nascita è vuoto",
            "TC1.5_1, Domz, Domenico, Ricciardelli, 2004-10-20, '', pass#1234, 3331234567, email è vuoto",
            "TC1.5_2, Domz, Domenico, Ricciardelli, 2004-10-20, d.ricci@rdelli, pass#1234, 3331234567, email non rispetta il formato",
            "TC1.6_1, Domz, Domenico, Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, '', 3331234567, password è vuoto",
            "TC1.7_1, Domz, Domenico, Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, '', cellulare è vuoto",
            "TC1.7_2, Domz, Domenico, Ricciardelli, 2004-10-20, d.ricciardelli1@studenti.unisa.it, pass#1234, '+39,67a4', cellulare non rispetta il formato",

            // --- NUOVO: TC1.4_2 Data di nascita non valida (Formato Errato) ---
            "TC1.4_2, Domz, Domenico, Ricciardelli, dataErrata, d.ricciardelli1@studenti.unisa.it, pass#1234, 3331234567, data di nascita non rispetta il formato"
    })
    void testRegistrazione_ErroriValidazione(
            String testId, String user, String nome, String cognome,
            String data, String email, String pass, String tel, String expectedError
    ) throws Exception {

        setupRequestMock(user, nome, cognome, data, email, pass, tel);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains(expectedError));
        verify(serviceMock, never()).registraUtente(any());
    }

    /**
     * Test Parametrico per errori di BUSINESS LOGIC (Duplicati nel DB)
     */
    @ParameterizedTest(name = "{0}: {1}")
    @CsvSource({
            "TC1.1_2, username risulta già registrato, username risulta già registrato",
            "TC1.5_3, email risulta già registrata, email risulta già registrata"
    })
    void testRegistrazione_EccezioniService(String testId, String msgException, String expectedError) throws Exception {

        setupRequestMock("Domz", "Domenico", "Ricciardelli", "2004-10-20", "test@email.it", "pass#1234", "3331234567");

        doThrow(new IllegalArgumentException(msgException))
                .when(serviceMock).registraUtente(any(UtenteBean.class));

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains(expectedError));
    }

    private void setupRequestMock(String user, String nome, String cognome, String data, String email, String pass, String tel) {
        when(request.getParameter("username")).thenReturn(user);
        when(request.getParameter("nome")).thenReturn(nome);
        when(request.getParameter("cognome")).thenReturn(cognome);
        when(request.getParameter("dataNascita")).thenReturn(data);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(pass);
        when(request.getParameter("prefisso")).thenReturn("+39");
        when(request.getParameter("telefono")).thenReturn(tel);
    }
}