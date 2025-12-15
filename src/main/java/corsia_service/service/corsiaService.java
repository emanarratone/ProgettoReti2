package corsia_service.service;

import corsia_service.model.Corsia;
import corsia_service.model.corsiaDTO;
import corsia_service.repository.corsiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class corsiaService {

    private final corsiaRepository repo;

    public corsiaService(corsiaRepository repo) {
        this.repo = repo;
    }

    public List<corsiaDTO> getAll() {
        return repo.findAll().stream()
                .map(c -> new corsiaDTO(c.getCasello(), c.getNumCorsia(), c.getVerso(), c.getTipo(), c.getClosed()))
                .toList();
    }

    @Transactional
    public corsiaDTO create(corsiaDTO dto){
        Corsia a = new Corsia(dto.getCasello(), dto.getNumCorsia(), dto.getVerso(), dto.getTipo());
        Corsia saved = repo.save(a);
        return new corsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }


    @Transactional
    public corsiaDTO update(Integer idCasello, Integer numCorsia, corsiaDTO dto) {
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
        return new corsiaDTO(saved.getCasello(), saved.getNumCorsia(), saved.getVerso(), saved.getTipo(), saved.getClosed());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<corsiaDTO> search(String query) {
        return repo.findCorsiaByCaselloOrderByNumCorsiaAsc(query).stream()
                .map(c -> new corsiaDTO(c.getCasello(), c.getNumCorsia(), c.getVerso(), c.getTipo()))
                .toList();
    }
}
