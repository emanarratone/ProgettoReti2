package regione_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import regione_service.model.DTO.regioneCreateUpdateDto;
import regione_service.model.DTO.regioneDTO;
import regione_service.model.Regione;
import regione_service.repository.regioneRepository;

import java.util.List;

@Service
public class regioneService {

    private final regioneRepository repo;

    public regioneService(regioneRepository repo) {
        this.repo = repo;
    }

    public List<regioneDTO> getAll() {
        return repo.findAll().stream()
                .map(r -> new regioneDTO(r.getId(), r.getNome()))
                .toList();
    }

    @Transactional
    public regioneDTO create(regioneCreateUpdateDto dto) {
        Regione r = new Regione(dto.getNome());
        Regione saved = repo.save(r);
        return new regioneDTO(saved.getId(), saved.getNome());
    }

    @Transactional
    public regioneDTO update(Integer id, regioneCreateUpdateDto dto) {
        Regione existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regione non trovata"));

        existing.setNome(dto.getNome());
        Regione saved = repo.save(existing);
        return new regioneDTO(saved.getId(), saved.getNome());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<regioneDTO> search(String query) {
        return repo.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query).stream()
                .map(r -> new regioneDTO(r.getId(), r.getNome()))
                .toList();
    }
}
