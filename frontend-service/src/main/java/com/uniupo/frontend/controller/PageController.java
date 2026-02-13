package com.uniupo.frontend.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("user") == null) {
            logger.warn("Accesso negato alla dashboard: sessione nulla");
            return "redirect:/index";
        }
        return "dashboard";
    }

    @GetMapping("/multe")
    public String multe(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/index";
        return "multe";
    }

    @GetMapping("/autostrada")
    public String autostrada(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/index";
        return "autostrada";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404";
    }

    @GetMapping("/{path:[^.]*}")
    public String redirectAll(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "404";
        }
        return "redirect:/index";
    }
}