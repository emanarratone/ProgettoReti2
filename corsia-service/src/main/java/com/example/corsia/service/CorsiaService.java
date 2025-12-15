package com.example.corsia.service;

import com.example.corsia.model.Corsia;
import com.example.corsia.model.dto.CorsiaDTO;
import com.example.corsia.repository.CorsiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CorsiaService {

    private final CorsiaRepository repo;

    public CorsiaService(CorsiaRepository repo) {
        this.repo = repo;
    }

    public List<CorsiaDTO> getAll() {
        return repo.findAll().stream()
                .map(c -> new CorsiaDTO(c.getCasello(), c.getNumCorsia(), c.getVerso(), c.getTipo(), c.getClosed()))
                .toList();
    }

    @Transactional
    public CorsiaDTO create(CorsiaDTO dto){
        Corsia a = new Corsia(dto.getCasello(), dto.getNumCorsia(), dto.getVerso(), dto.getTipo());
        Corsia saved = repo.save(a);
        return new CorsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }

    @Transactional
    public CorsiaDTO update(Integer idCasello, Integer numCorsia, CorsiaDTO dto) {
        List<Corsia> casello = repo.findById(idCasello).stream().toList();
        Corsia existing = new Corsia();

        for(Corsia c: casello){
            if(Objects.equals(c.getNumCorsia(), numCorsia)) existing = c;
        }

        if(existing.getNumCorsia() == null || existing.getNumCorsia() != numCorsia){
            throw new IllegalArgumentException("Corsia non trovata");
        }

        existing.setVerso(dto.getVerso());
        existing.setTipo(dto.getTipo());
        existing.setClosed(dto.getClosed());

        Corsia saved = repo.save(existing);
        return new CorsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<CorsiaDTO> search(String query) {
        return repo.findCorsiaByCaselloOrderByNumCorsiaAsc(query).stream()
                .map(c -> new CorsiaDTO(c.getCasello(), c.getNumCorsia(), c.getVerso(), c.getTipo()))
                .toList();
    }
}
