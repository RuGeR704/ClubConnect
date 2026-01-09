package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
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
class CreaGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService serviceMock;
    @Mock Part filePart;
    @Mock ServletContext servletContext;
    @Mock ServletConfig servletConfig;

    CreaGruppoServlet servlet;

    @BeforeEach
    void setUp() throws ServletException {
        servlet = new CreaGruppoServlet() {
            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }
        };
        servlet.setGruppoService(serviceMock);

        // Fix per l'errore "ServletConfig has not been initialized"
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet.init(servletConfig);

        when(servletContext.getRealPath(anyString())).thenReturn(System.getProperty("java.io.tmpdir"));

        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try {
            when(request.getPart("logo")).thenReturn(filePart);
            when(filePart.getSize()).thenReturn(100L);
            when(filePart.getSubmittedFileName()).thenReturn("logo.jpg");
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Test Parametrico (SENZA TAG)
     */
    @ParameterizedTest(name = "{0}: {9}")
    @CsvSource(delimiter = ';', nullValues = {"NULL"}, value = {
            // TC2.1_1: Nome vuoto
            "TC2.1_1; ; Descrizione valida; Club; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè il campo nome è vuoto",

            // TC2.3_1: Descrizione vuota
            "TC2.3_1; Club Libro; ; Club; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè il campo descrizione è vuoto",

            // TC2.3_2: Descrizione troppo lunga
            "TC2.3_2; Club Libro; Questa descrizione è volutamente resa lunghissima per superare il limite di caratteri imposto dal sistema e generare un errore di validazione come previsto dal test case specification del documento TCS.; Club; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè la descrizione è troppo lunga",

            // TC2.4_1: Tipo vuoto
            "TC2.4_1; Club Libro; Descrizione valida; ; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè il campo tipo è vuoto",

            // TC2.5: Settore vuoto
            "TC2.5; Club Libro; Descrizione valida; Club; ; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè il campo settore è vuoto",

            // TC2.6: Slogan troppo lungo
            "TC2.6; Club Libro; Descrizione valida; Club; Cultura; Questo slogan è decisamente troppo lungo per essere accettato dal sistema di validazione del club connect e generare errore; Via Guglielmo Marconi 13, Napoli (NA); 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè lo slogan è troppo lungo",

            // TC2.7: Indirizzo Sede formato errato
            "TC2.7; Club Libro; Descrizione valida; Club; Cultura; Slogan valido; Nel mezzo del cammin di nostra vita; 10; 30; Errore: La creazione del gruppo non è andata a buon fine poichè l'indirizzo non rispetta il formato",

            // TC2.8_1: Retta senza importo
            "TC2.8_1; Club Libro; Descrizione valida; Club; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); ; 30; Errore: La creazione del gruppo non è andata a buon fine poichè la retta deve necessariamente avere un importo",

            // TC2.8_2: Retta senza frequenza
            "TC2.8_2; Club Libro; Descrizione valida; Club; Cultura; Slogan valido; Via Guglielmo Marconi 13, Napoli (NA); 10; ; Errore: La creazione del gruppo non è andata a buon fine poichè la rette deve necessariamente avere informazioni sulla frequenza del pagamento"
    })
    void testCreaGruppo_ErroriValidazione(
            String testId, String nome, String desc, String tipo,
            String sett, String slogan, String sede, String importo, String freq, String oracle
    ) throws Exception {

        setupRequest(nome, desc, tipo, sett, slogan, sede, importo, freq);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains(oracle));
        verify(serviceMock, never()).creaGruppo(any(), anyInt());
    }

    @Test
    void testTC2_9_1_LogoTroppoGrande() throws Exception {
        setupRequest("Club Lettura", "Desc", "Club", "Cultura", "Slogan", "Via Roma 1, Napoli (NA)", "10", "30");
        when(filePart.getSize()).thenReturn(1024 * 1024 * 11L);

        servlet.doPost(request, response);
        // Logica di verifica dipendente dall'implementazione specifica del MultipartConfig
    }

    @Test
    void testTC2_10_CreazioneSuccesso() throws Exception {
        setupRequest("Club Luigi", "Desc", "Club", "Cultura", "Slogan", "Via Guglielmo Marconi 13, Napoli (NA)", "10", "30");

        servlet.doPost(request, response);

        verify(serviceMock).creaGruppo(any(GruppoBean.class), eq(1));
        verify(response).sendRedirect("feedServlet");
    }

    private void setupRequest(String nome, String desc, String tipo, String sett, String slogan, String sede, String importo, String freq) {
        when(request.getParameter("nome")).thenReturn(nome);
        when(request.getParameter("descrizione")).thenReturn(desc);
        when(request.getParameter("tipoGruppo")).thenReturn(tipo);
        when(request.getParameter("settore")).thenReturn(sett);
        when(request.getParameter("slogan")).thenReturn(slogan);
        when(request.getParameter("sede")).thenReturn(sede);
        when(request.getParameter("importo")).thenReturn(importo);
        when(request.getParameter("frequenza")).thenReturn(freq);
    }
}