package Presentation.GestionePagamenti;

import Application.GestioneAccount.AccountService;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RimuoviMetodoPagamentoServlet")
public class RimuoviMetodoPagamentoServlet extends HttpServlet {

    private AccountService accountService = new AccountService();

    // Setter per i test
    public void setAccountService(AccountService service) {
        this.accountService = service;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute("utente") : null;

        // 1. Controllo Login
        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id_metodo_pagamento");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int idMetodo = Integer.parseInt(idParam);

                // 2. Chiamata al Service (Rimuove la carta dal DB)
                accountService.rimuoviMetodoPagamento(idMetodo, utente.getId_utente());
            }

            // 3. Redirect alla lista aggiornata
            response.sendRedirect("VisualizzaMetodidiPagamentoServlet");

        } catch (NumberFormatException e) {
            // Se l'ID non è valido, torniamo semplicemente alla lista
            response.sendRedirect("VisualizzaMetodidiPagamentoServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}