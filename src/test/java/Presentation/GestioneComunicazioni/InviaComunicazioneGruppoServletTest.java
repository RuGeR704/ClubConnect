package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviaComunicazioneGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ServletContext context; // Serve per getRealPath
    @Mock Part filePart; // Serve per simulare il file caricato
    @Mock ComunicazioneService serviceMock;

    // ... import vari ...

    InviaComunicazioneGruppoServlet servlet; // Togli l'annotazione @InjectMocks se c'è

    @BeforeEach
    void setUp() {
        servlet = new InviaComunicazioneGruppoServlet() {
            @Override
            public ServletContext getServletContext() {
                return context; // Restituisce il nostro mock!
            }
        };
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_SalvaConFoto() throws Exception {
        // --- GIVEN ---
        when(request.getSession()).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        // 1. Parametri Form Standard
        when(request.getParameter("idGruppo")).thenReturn("50");
        when(request.getParameter("titolo")).thenReturn("Titolo con Foto");
        when(request.getParameter("contenuto")).thenReturn("Guarda questa immagine");

        // 2. SIMULAZIONE FILE UPLOAD (La parte nuova!)
        when(request.getPart("foto")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(1024L); // File non vuoto (1KB)
        when(filePart.getSubmittedFileName()).thenReturn("vacanza.jpg");
        // Simuliamo una cartella temporanea sicura per il test
        String tempPath = System.getProperty("java.io.tmpdir");
        when(context.getRealPath("")).thenReturn(tempPath);

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        // 1. Verifichiamo che il file sia stato scritto
        // (Nota: verifichiamo che write venga chiamato con un percorso che finisce col nome del file)
        verify(filePart).write(contains("vacanza.jpg"));

        // 2. Catturiamo il bean salvato per vedere se il percorso foto è giusto
        ArgumentCaptor<ComunicazioniBean> captor = ArgumentCaptor.forClass(ComunicazioniBean.class);
        verify(serviceMock).creaComunicazione(captor.capture());

        ComunicazioniBean bean = captor.getValue();
        assertEquals("Titolo con Foto", bean.getTitolo());
        // Verifichiamo che il nome della foto inizi con il percorso previsto
        assertTrue(bean.getFoto().startsWith("images/uploads/"));
        assertTrue(bean.getFoto().endsWith("vacanza.jpg"));

        // 3. Verifichiamo il redirect
        verify(response).sendRedirect(contains("VisualizzaComunicazioniGruppoServlet"));
    }

    @Test
    void testDoPost_SalvaSenzaFoto() throws Exception {
        // --- GIVEN ---
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());

        when(request.getParameter("idGruppo")).thenReturn("50");
        when(request.getParameter("titolo")).thenReturn("Titolo No Foto");
        when(request.getParameter("contenuto")).thenReturn("Testo");

        // Simuliamo nessun file caricato (o file vuoto)
        // Opzione A: getPart restituisce null
        // when(request.getPart("foto")).thenReturn(null);

        // Opzione B: getPart restituisce un file vuoto (più comune nei browser)
        when(request.getPart("foto")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(0L); // Dimensione 0

        // --- WHEN ---
        servlet.doPost(request, response);

        // --- THEN ---
        ArgumentCaptor<ComunicazioniBean> captor = ArgumentCaptor.forClass(ComunicazioniBean.class);
        verify(serviceMock).creaComunicazione(captor.capture());

        ComunicazioniBean bean = captor.getValue();
        // Se non c'è foto, il codice mette stringa vuota ""
        assertEquals("", bean.getFoto());

        // Non deve provare a scrivere su disco
        verify(filePart, never()).write(anyString());
    }
}