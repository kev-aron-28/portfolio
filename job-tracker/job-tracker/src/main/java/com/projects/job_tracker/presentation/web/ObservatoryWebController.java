package com.projects.job_tracker.presentation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ObservatoryWebController {
    @GetMapping("/observatory")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Observatorio del mercado");
        model.addAttribute("pageDescription", "Registra evidencia del mercado y conserva su historial.");
        model.addAttribute("activeNav", "observatory");
        return "observatory/index";
    }
    @GetMapping("/observatory/captures")
    public String captures(Model model) {
        model.addAttribute("pageTitle", "Segmentos y capturas del observatorio");
        model.addAttribute("pageDescription", "Protocolos versionados y evidencia de cada revisión del mercado.");
        model.addAttribute("activeNav", "observatory");
        return "observatory/captures";
    }
    @GetMapping("/observatory/duplicates")
    public String duplicates(Model model) {
        model.addAttribute("pageTitle", "Revisión de duplicados del observatorio");
        model.addAttribute("pageDescription", "Identidad por fuente, uniones reversibles e historial de decisiones.");
        model.addAttribute("activeNav", "observatory");
        return "observatory/duplicates";
    }
    @GetMapping("/observatory/schedules")
    public String schedules(Model model) {
        model.addAttribute("pageTitle", "Programación del observatorio");
        model.addAttribute("pageDescription", "Capturas periódicas, recuperación e historial de tareas.");
        model.addAttribute("activeNav", "observatory");
        return "observatory/schedules";
    }
}
