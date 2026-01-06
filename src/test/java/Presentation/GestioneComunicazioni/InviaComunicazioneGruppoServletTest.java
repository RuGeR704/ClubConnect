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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviaComunicazioneGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ComunicazioneService serviceMock;

    InviaComunicazioneGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new InviaComunicazioneGruppoServlet();
        servlet.setService(serviceMock);
    }

    @Test
    void testDoPost_SalvaGruppo() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        when(session.getAttribute("utente")).thenReturn(utente);

        // Parametri completi
        when(request.getParameter("idGruppo")).thenReturn("50");
        when(request.getParameter("titolo")).thenReturn("News Gruppo");
        when(request.getParameter("contenuto")).thenReturn("Ciao gruppo");
        when(request.getParameter("foto")).thenReturn("foto.png");

        // WHEN
        servlet.doPost(request, response);

        // THEN
        ArgumentCaptor<ComunicazioniBean> captor = ArgumentCaptor.forClass(ComunicazioniBean.class);
        verify(serviceMock).creaComunicazione(captor.capture());

        ComunicazioniBean bean = captor.getValue();
        assertFalse(bean.isIsglobal()); // NON deve essere globale
        assertEquals(50, bean.getId_gruppo()); // Deve avere l'ID gruppo giusto

        verify(response).sendRedirect(org.mockito.ArgumentMatchers.contains("idGruppo=50"));
    }
}