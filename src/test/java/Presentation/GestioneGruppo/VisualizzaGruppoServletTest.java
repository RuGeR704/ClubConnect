package Presentation.GestioneGruppo;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import Application.GestioneGruppo.ClubBean; // Usa la classe concreta!
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.RequestDispatcher;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VisualizzaGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;

    @Mock GruppoService gruppoServiceMock;
    @Mock PagamentoService pagamentoServiceMock;
    @Mock AccountService accountServiceMock;
    @Mock ComunicazioneService comunicazioneServiceMock;

    VisualizzaGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaGruppoServlet();
        // Iniezione dei Mock
        servlet.setGruppoService(gruppoServiceMock);
        servlet.setPagamentoService(pagamentoServiceMock);
        servlet.setAccountService(accountServiceMock);
        servlet.setComunicazioneService(comunicazioneServiceMock);

        // Setup base della Request
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_MostraGruppo() throws Exception {
        // --- 1. SETUP FONDAMENTALE (Senza questo il test fallisce subito) ---
        when(request.getParameter("id")).thenReturn("1"); // Simuliamo che l'ID nella URL sia "1"

        // --- 2. SETUP UTENTE ---
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(10);
        when(session.getAttribute("utente")).thenReturn(utente);

        // --- 3. SETUP GRUPPO ---
        // Usiamo ClubBean perché GruppoBean è astratta
        ClubBean gruppo = new ClubBean();
        gruppo.setId_gruppo(1); // Controlla se il tuo metodo è setId() o setId_gruppo()
        gruppo.setNome("Club Test");

        // Istruiamo il service a restituire questo gruppo
        when(gruppoServiceMock.recuperaGruppo(1)).thenReturn(gruppo);

        // --- 4. SETUP PERMESSI/ISCRIZIONE ---
        // Simuliamo che l'utente sia iscritto
        List<GruppoBean> listaIscritti = new ArrayList<>();
        listaIscritti.add(gruppo);
        when(accountServiceMock.getGruppiIscritto(10)).thenReturn(listaIscritti);

        // --- 5. ESECUZIONE ---
        servlet.doGet(request, response);

        // --- 6. VERIFICHE ---
        // Ora la servlet dovrebbe arrivare fino in fondo
        verify(request).setAttribute(eq("gruppo"), eq(gruppo));
        verify(request).setAttribute(eq("isIscritto"), eq(true));

        // Verifica che venga chiamata la JSP corretta
        verify(request).getRequestDispatcher("visualizza_gruppo.jsp");
        verify(dispatcher).forward(request, response);
    }
}