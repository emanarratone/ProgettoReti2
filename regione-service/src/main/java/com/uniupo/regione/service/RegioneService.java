package com.uniupo.regione.service;

import com.uniupo.regione.model.Regione;
import com.uniupo.regione.model.dto.RegioneCreateUpdateDTO;
import com.uniupo.regione.model.dto.RegioneDTO;
import com.uniupo.regione.repository.RegioneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegioneService {

    private final RegioneRepository repo;

    public RegioneService(RegioneRepository repo) {
        this.repo = repo;
    }

    public List<RegioneDTO> getAll() {
        return repo.findAll().stream()
                .map(r -> new RegioneDTO(r.getId(), r.getNome()))
                .toList();
    }

    @Transactional
    public RegioneDTO create(RegioneCreateUpdateDTO dto) {
        Regione r = new Regione(dto.getNome());
        Regione saved = repo.save(r);
        return new RegioneDTO(saved.getId(), saved.getNome());
    }

    @Transactional
    public RegioneDTO update(Integer id, RegioneCreateUpdateDTO dto) {
        Regione existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regione non trovata"));

        existing.setNome(dto.getNome());
        Regione saved = repo.save(existing);
        return new RegioneDTO(saved.getId(), saved.getNome());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<RegioneDTO> search(String query) {
        return repo.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query).stream()
                .map(r -> new RegioneDTO(r.getId(), r.getNome()))
                .toList();
    }
}
