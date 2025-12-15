package com.example.casello.service;

import com.example.casello.model.Casello;
import com.example.casello.model.dto.CaselloDTO;
import com.example.casello.repository.CaselloRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaselloService {

    private final CaselloRepository repo;

    public CaselloService(CaselloRepository repo) {
        this.repo = repo;
    }

    public List<CaselloDTO> getAll() {
        return repo.findAll().stream()
                .map(c -> new CaselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(),
                                         c.isClosed(), c.getLimite()))
                .toList();
    }

    @Transactional
    public CaselloDTO create(CaselloDTO dto){
        Casello a = new Casello(dto.getSigla(), dto.getIdAutostrada(), dto.isClosed(), dto.getLimite());
        Casello saved = repo.save(a);
        return new CaselloDTO(saved.getIdCasello(), saved.getSigla(), saved.getIdAutostrada(), 
                              saved.isClosed(), saved.getLimite());
    }

    @Transactional
    public CaselloDTO update(Integer id, CaselloDTO dto) {
        Casello existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Casello non trovato"));

        existing.setSigla(dto.getSigla());
        existing.setLimite(dto.getLimite());
        existing.setClosed(dto.isClosed());

        Casello saved = repo.save(existing);
        return new CaselloDTO(saved.getIdCasello(), saved.getSigla(), saved.getIdAutostrada(), 
                              saved.isClosed(), saved.getLimite());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<CaselloDTO> search(String query) {
        return repo.findCaselloBySiglaOrderBySiglaAsc(query).stream()
                .map(c -> new CaselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(), 
                                         c.isClosed(), c.getLimite()))
                .toList();
    }
}
