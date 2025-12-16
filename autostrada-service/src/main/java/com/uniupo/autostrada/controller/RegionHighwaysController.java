package com.uniupo.autostrada.controller;

import com.uniupo.autostrada.service.AutostradaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/regions")
public class RegionHighwaysController {

    private final AutostradaService service;

    public RegionHighwaysController(AutostradaService service) {
        this.service = service;
    }

    @GetMapping("/{idRegione}/highways")
    public ResponseEntity<?> getHighwaysForRegion(@PathVariable Integer idRegione) {
        return ResponseEntity.ok(service.getByRegion(idRegione));
    }
}
