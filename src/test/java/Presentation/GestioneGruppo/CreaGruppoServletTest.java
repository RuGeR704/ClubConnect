package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;
import Application.GestioneGruppo.GruppoService;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // AGGIUNTO: Rende Mockito meno severo
class CreaGruppoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock ServletContext context;
    @Mock Part filePart;
    @Mock GruppoService serviceMock;

    CreaGruppoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new CreaGruppoServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };
        servlet.setGruppoService(serviceMock);
    }

    @Test
    void testDoPost_CreaClub() throws Exception {
        // GIVEN
        when(request.getSession(false)).thenReturn(session);
        UtenteBean u = new UtenteBean(); u.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(u);

        // Parametri obbligatori per la logica Club
        when(request.getParameter("nome")).thenReturn("Club Tennis");
        when(request.getParameter("tipoGruppo")).thenReturn("Club");
        when(request.getParameter("importo")).thenReturn("50.0");
        when(request.getParameter("frequenza")).thenReturn("30");

        // Parametri opzionali (che la servlet legge comunque) -> Li mockiamo per evitare l'errore
        when(request.getParameter("settore")).thenReturn("Sport");
        when(request.getParameter("slogan")).thenReturn("Slogan");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        when(request.getParameter("sede")).thenReturn("Roma");
        when(request.getParameter("regole")).thenReturn("No regole");

        // Mock file upload
        when(request.getPart("logo")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(100L);
        when(filePart.getSubmittedFileName()).thenReturn("logo.png");
        when(context.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));

        // WHEN
        servlet.doPost(request, response);

        // THEN
        ArgumentCaptor<GruppoBean> captor = ArgumentCaptor.forClass(GruppoBean.class);
        verify(serviceMock).creaGruppo(captor.capture(), eq(1));

        GruppoBean captured = captor.getValue();
        assertTrue(captured instanceof ClubBean);
        assertEquals(50.0, ((ClubBean)captured).getImporto_retta());

        verify(response).sendRedirect("feedServlet");
    }
}