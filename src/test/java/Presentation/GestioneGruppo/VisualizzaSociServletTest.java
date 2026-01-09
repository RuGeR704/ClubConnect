package Presentation.GestioneGruppo;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.GruppoService;
import Application.GestionePagamenti.PagamentoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaSociServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GruppoService gruppoService;
    @Mock
    PagamentoService pagamentiService;

    VisualizzaSociServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaSociServlet();
        servlet.setGruppoService(gruppoService);
        servlet.setPagamentiService(pagamentiService);
    }

    @Test
    void testDoGet_OrdinamentoSoci() throws Exception {
        // GIVEN
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getParameter("id")).thenReturn("10");

        // Mock Gruppo
        when(gruppoService.recuperaGruppo(10)).thenReturn(new AssociazioneBean());

        // Mock Soci: Creiamo 2 utenti
        UtenteBean admin = new UtenteBean(); admin.setId_utente(1); admin.setNome("Admin");
        UtenteBean user = new UtenteBean(); user.setId_utente(2); user.setNome("User");

        // Li restituiamo in ordine "sbagliato" (User prima di Admin)
        List<UtenteBean> sociDisordinati = new ArrayList<>();
        sociDisordinati.add(user);
        sociDisordinati.add(admin);

        when(gruppoService.recuperaSociDelGruppo(10)).thenReturn(sociDisordinati);

        // Mock Ruoli: 1 è Admin (true), 2 è User (false)
        Map<Integer, Boolean> ruoli = new HashMap<>();
        ruoli.put(1, true);
        ruoli.put(2, false);
        when(gruppoService.recuperaMappaRuoli(10)).thenReturn(ruoli);

        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        // WHEN
        servlet.doGet(request, response);

        // THEN
        // Verifichiamo che la lista passata alla JSP sia stata ORDINATA
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UtenteBean>> captor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("listaSoci"), captor.capture());

        List<UtenteBean> sociOrdinati = captor.getValue();
        assertEquals(1, sociOrdinati.get(0).getId_utente()); // Il primo deve essere Admin (ID 1)
        assertEquals(2, sociOrdinati.get(1).getId_utente()); // Il secondo deve essere User (ID 2)

        verify(dispatcher).forward(request, response);
    }
}