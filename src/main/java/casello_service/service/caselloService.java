package casello_service.service;


import casello_service.model.Casello;
import casello_service.model.DTO.caselloDTO;
import casello_service.repository.caselloRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class caselloService {

    private final caselloRepository repo;

    public caselloService(caselloRepository repo) {
        this.repo = repo;
    }

    public List<caselloDTO> getAll() {
        return repo.findAll().stream()
                .map(c -> new caselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(),
                                                 c.isClosed(), c.getLimite()))
                .toList();
    }

    @Transactional
    public caselloDTO create(caselloDTO dto){
        Casello a = new Casello(dto.getSigla(), dto.getIdAutostrada(), dto.isClosed(), dto.getLimite());
        Casello saved = repo.save(a);
        return new caselloDTO(saved.getIdCasello(), saved.getSigla(), saved.getIdAutostrada(), saved.isClosed(), saved.getLimite());

    }

    @Transactional
    public caselloDTO update(Integer id, caselloDTO dto) {
        Casello existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regione non trovata"));

        existing.setSigla(dto.getSigla());
        existing.setLimite(dto.getLimite());
        existing.setClosed(dto.isClosed());

        Casello saved = repo.save(existing);
        return new caselloDTO(saved.getIdCasello(), saved.getSigla(), saved.getIdAutostrada(), saved.isClosed(), saved.getLimite());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<caselloDTO> search(String query) {
        return repo.findCaselloBySiglaOrderBySiglaAsc(query).stream()
                .map(c -> new caselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(), c.isClosed(), c.getLimite()))
                .toList();
    }
}
