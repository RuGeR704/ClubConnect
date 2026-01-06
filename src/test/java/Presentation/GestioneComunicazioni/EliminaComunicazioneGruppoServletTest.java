package Presentation.GestioneComunicazioni;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneComunicazioni.ComunicazioniBean;
import Application.GestioneComunicazioni.ComunicazioneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminaComunicazioneGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ComunicazioneService serviceMock;

    EliminaComunicazioneGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new EliminaComunicazioneGruppoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_AutoreElimina() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        UtenteBean autore = new UtenteBean();
        autore.setId_utente(5); // ID 5
        when(session.getAttribute("utente")).thenReturn(autore);

        when(request.getParameter("idComunicazione")).thenReturn("20");

        // Simuliamo la comunicazione nel DB
        ComunicazioniBean comm = new ComunicazioniBean();
        comm.setId_comunicazione(20);
        comm.setId_autore(5); // Stesso ID dell'autore
        comm.setId_gruppo(100);

        when(serviceMock.recuperaComunicazione(20)).thenReturn(comm);

        // WHEN
        servlet.doPost(request, response);

        // THEN
        verify(serviceMock).rimuoviComunicazione(20);
        verify(response).sendRedirect(contains("VisualizzaComunicazioniGruppoServlet?idGruppo=100"));
    }
}