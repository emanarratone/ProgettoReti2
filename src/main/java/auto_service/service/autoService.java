package auto_service.service;

import auto_service.model.DTO.autoDTO;
import auto_service.repository.autoRepository;
import autostrada_service.model.Autostrada;
import autostrada_service.model.DTO.autostradaCreateUpdateDTO;
import autostrada_service.model.DTO.autostradaDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.*;

import java.util.List;

@Service
public class autoService {


    private autoRepository repo;

    public autoService(autoRepository repo) {this.repo = repo;}

    public List<autoDTO> getAll() {
        return repo.findAll().stream()
                .map(a -> new autoDTO(a.getTarga(), a.getclasse_veicolo()))
                .toList();
    }


    @Transactional
    public autostradaDTO create(autostradaCreateUpdateDTO dto){
        Autostrada a = new Autostrada(dto.getSigla(), dto.getIdRegione());
        Autostrada saved = repo.save(a);
        return new autostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());

    }

    @Transactional
    public autostradaDTO update(Integer id, autostradaCreateUpdateDTO dto) {
        Autostrada existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regione non trovata"));

        existing.setSigla(dto.getSigla());
        existing.setIdRegione(dto.getIdRegione());//serve?
        Autostrada saved = repo.save(existing);
        return new autostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public List<autostradaDTO> search(String query) {
        return repo.findAutostradasBySiglaOrderBySiglaAsc(query).stream()
                .map(a -> new autostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }
}
