package com.uniupo.frontend.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    // 1. Gestione ROOT: decide solo dove mandare l'utente all'inizio
    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/index";
    }

    // 2. Pagina di Login/Index
    @GetMapping("/index")
    public String index(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "index"; // Carica templates/index.html
    }

    // 3. Pagine Protette
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("user") == null) {
            logger.warn("Accesso negato alla dashboard: sessione nulla");
            return "redirect:/index"; // Torna alla pagina di login
        }
        return "dashboard"; // Carica templates/dashboard.html
    }

    @GetMapping("/multe")
    public String multe(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/index";
        return "multe"; // Carica templates/multe.html
    }

    @GetMapping("/autostrada")
    public String autostrada(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/index";
        return "autostrada"; // Carica templates/autostrada.html
    }

    // 4. Fallback: evita il 404 se l'utente sbaglia URL
    @GetMapping("/{path:[^.]*}")
    public String redirectAll(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/index";
    }
}