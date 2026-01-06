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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviaComunicazioneGlobaleServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ComunicazioneService serviceMock;

    InviaComunicazioneGlobaleServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new InviaComunicazioneGlobaleServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_SalvaGlobale() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        UtenteBean admin = new UtenteBean();
        admin.setIsadmin(true);
        when(session.getAttribute("utente")).thenReturn(admin);

        // Mock di TUTTI i parametri letti dalla servlet
        when(request.getParameter("titolo")).thenReturn("Titolo Globale");
        when(request.getParameter("contenuto")).thenReturn("Contenuto Importante");
        when(request.getParameter("foto")).thenReturn("img.jpg");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        // Catturiamo l'oggetto passato al service per controllare che sia davvero globale
        ArgumentCaptor<ComunicazioniBean> captor = ArgumentCaptor.forClass(ComunicazioniBean.class);
        verify(serviceMock).creaComunicazione(captor.capture());

        ComunicazioniBean beanSalvato = captor.getValue();
        assertTrue(beanSalvato.isIsglobal()); // Verifica che sia globale
        assertEquals("Titolo Globale", beanSalvato.getTitolo());

        verify(response).sendRedirect("VisualizzaComunicazioniGlobaliServlet");
    }
}