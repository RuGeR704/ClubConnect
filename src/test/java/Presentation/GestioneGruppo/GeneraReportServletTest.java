package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import Application.GestioneEventi.EventoBean;
import Application.GestioneEventi.EventoService;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletOutputStream;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeneraReportServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock ServletOutputStream outputStream;

    @Mock GruppoService gruppoServiceMock;
    @Mock EventoService eventoServiceMock;
    @Mock ComunicazioneService comunicazioneServiceMock;

    GeneraReportServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new GeneraReportServlet();
        servlet.setGruppoService(gruppoServiceMock);
        servlet.setEventoService(eventoServiceMock);
        servlet.setComunicazioneService(comunicazioneServiceMock);

        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Mock output stream per i test che generano PDF
        lenient().when(response.getOutputStream()).thenReturn(outputStream);
    }

    // TCS 3.1: Accesso Negato
    @Test
    void testGenera_AccessoNegato() throws Exception {
        setupUser(10);
        when(request.getParameter("idGruppo")).thenReturn("1");
        // Utente NON è gestore
        when(gruppoServiceMock.isUtenteGestore(1, 10)).thenReturn(false);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("non ha i permessi"));
        verify(dispatcher).forward(request, response);
    }

    // TCS 3.2: Categorie Mancanti
    @Test
    void testGenera_CategorieMancanti() throws Exception {
        setupValidUser();
        when(request.getParameterValues("categorie")).thenReturn(null); // Nessuna checkbox

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("Nessuna categoria"));
    }

    // TCS 3.3: Date Incoerenti
    @Test
    void testGenera_DateIncoerenti() throws Exception {
        setupValidUser();
        when(request.getParameterValues("categorie")).thenReturn(new String[]{"Eventi"});

        when(request.getParameter("dataInizio")).thenReturn("2026-12-31");
        when(request.getParameter("dataFine")).thenReturn("2025-01-01"); // Fine prima dell'inizio

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("successiva alla data di fine"));
    }

    // TCS 3.4: Nessun Dato Trovato
    @Test
    void testGenera_NessunDato() throws Exception {
        setupValidUser();
        when(request.getParameterValues("categorie")).thenReturn(new String[]{"Iscritti"});

        // Il service restituisce liste vuote
        when(gruppoServiceMock.recuperaGruppo(1)).thenReturn(new ClubBean());
        when(gruppoServiceMock.recuperaSociDelGruppo(1)).thenReturn(new ArrayList<>());

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("Nessun dato trovato"));
    }

    // TCS 3.6: Report Voluminoso (Email)
    @Test
    void testGenera_Voluminoso() throws Exception {
        setupValidUser();
        when(request.getParameterValues("categorie")).thenReturn(new String[]{"Iscritti"});

        when(gruppoServiceMock.recuperaGruppo(1)).thenReturn(new ClubBean());

        // Simuliamo una lista di 150 utenti (> 100)
        List<UtenteBean> tantiSoci = new ArrayList<>();
        for(int i=0; i<150; i++) tantiSoci.add(new UtenteBean());

        when(gruppoServiceMock.recuperaSociDelGruppo(1)).thenReturn(tantiSoci);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("msg"), contains("inviato via e-mail"));
        // Verifica che NON generi il PDF
        verify(response, never()).setContentType("application/pdf");
    }

    // TCS 3.5: Successo Standard (Download PDF)
    @Test
    void testGenera_SuccessoStandard() throws Exception {
        setupValidUser();
        when(request.getParameterValues("categorie")).thenReturn(new String[]{"Eventi"});

        // Dati validi (pochi)
        ClubBean club = new ClubBean(); club.setNome("Club Test");
        when(gruppoServiceMock.recuperaGruppo(1)).thenReturn(club);

        List<EventoBean> eventi = new ArrayList<>();
        EventoBean e = new EventoBean();
        e.setId_gruppo(1);
        e.setData_ora(LocalDateTime.now());
        e.setNome("Evento 1");
        eventi.add(e);

        when(eventoServiceMock.retrieveAllEventi()).thenReturn(eventi);

        servlet.doPost(request, response);

        verify(response).setContentType("application/pdf");
        verify(response).setHeader(contains("Content-Disposition"), contains("Report_Club_Test.pdf"));
    }

    // Helper method
    private void setupUser(int id) {
        UtenteBean u = new UtenteBean();
        u.setId_utente(id);
        when(session.getAttribute("utente")).thenReturn(u);
    }

    private void setupValidUser() throws Exception {
        setupUser(10);
        when(request.getParameter("idGruppo")).thenReturn("1");
        when(gruppoServiceMock.isUtenteGestore(1, 10)).thenReturn(true);
    }
}