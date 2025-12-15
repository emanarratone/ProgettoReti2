package com.example.autostrada.service;

import com.example.autostrada.model.Autostrada;
import com.example.autostrada.model.dto.AutostradaCreateUpdateDTO;
import com.example.autostrada.model.dto.AutostradaDTO;
import com.example.autostrada.repository.AutostradaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutostradaService {

    private final AutostradaRepository repo;

    public AutostradaService(AutostradaRepository repo) {
        this.repo = repo;
    }

    public List<AutostradaDTO> getAll() {
        return repo.findAll().stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }

    @Transactional
    public AutostradaDTO create(AutostradaCreateUpdateDTO dto){
        Autostrada a = new Autostrada(dto.getSigla(), dto.getIdRegione());
        Autostrada saved = repo.save(a);
        return new AutostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());
    }

    @Transactional
    public AutostradaDTO update(Integer id, AutostradaCreateUpdateDTO dto) {
        Autostrada existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autostrada non trovata"));

        existing.setSigla(dto.getSigla());
        existing.setIdRegione(dto.getIdRegione());
        Autostrada saved = repo.save(existing);
        return new AutostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<AutostradaDTO> search(String query) {
        return repo.findAutostradasBySiglaOrderBySiglaAsc(query).stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }
}
