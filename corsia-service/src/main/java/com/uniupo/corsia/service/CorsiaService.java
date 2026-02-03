package com.uniupo.corsia.service;

import com.uniupo.corsia.model.Corsia;
import com.uniupo.corsia.model.dto.CorsiaDTO;
import com.uniupo.corsia.repository.CorsiaRepository;
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
    public CorsiaDTO createForToll(Integer idCasello, String versoStr, String tipoStr, Boolean chiuso) {
        // compute next num
        Integer maxNum = repo.findMaxNumCorsiaByCasello(idCasello);
        int nextNum = (maxNum != null ? maxNum : 0) + 1;

        Corsia.Verso verso = null;
        try { verso = Corsia.Verso.valueOf(versoStr); } catch (Exception e) { verso = Corsia.Verso.ENTRATA; }

        Corsia.Tipo tipo;
        try { tipo = Corsia.Tipo.valueOf(tipoStr); } catch (Exception e) { tipo = Corsia.Tipo.MANUALE; }

        Corsia a = new Corsia(idCasello, nextNum, verso, tipo, chiuso != null && chiuso);
        Corsia saved = repo.save(a);
        return new CorsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }


    @Transactional
    public void deleteByCaselloAndNum(Integer casello, Integer numCorsia) {
        repo.deleteByCaselloAndNumCorsia(casello, numCorsia);
    }

    @Transactional
    public CorsiaDTO update(Integer idCasello, Integer numCorsia, CorsiaDTO dto) {
        List<Corsia> lista = repo.findByCaselloOrderByNumCorsiaAsc(idCasello);
        Corsia existing = lista.stream()
                .filter(c -> Objects.equals(c.getNumCorsia(), numCorsia))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Corsia non trovata"));

        existing.setVerso(dto.getVerso());
        existing.setTipo(dto.getTipo());
        existing.setClosed(dto.getClosed() != null && dto.getClosed());

        Corsia saved = repo.save(existing);
        return new CorsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<CorsiaDTO> search(Integer idCasello) {
        return repo.findByCaselloOrderByNumCorsiaAsc(idCasello).stream()
                .map(c -> new CorsiaDTO(c.getCasello(), c.getNumCorsia(), c.getVerso(), c.getTipo()))
                .toList();
    }
}
