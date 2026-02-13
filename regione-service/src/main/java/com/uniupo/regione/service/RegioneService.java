package com.uniupo.regione.service;

import com.uniupo.regione.model.Regione;
import com.uniupo.regione.model.dto.RegioneCreateUpdateDTO;
import com.uniupo.regione.model.dto.RegioneDTO;
import com.uniupo.regione.rabbitMQ.RabbitMQConfig;
import com.uniupo.regione.repository.RegioneRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegioneService {

    private final RegioneRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public RegioneService(RegioneRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
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
        // 1. Verifica esistenza
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Regione non trovata");
        }

        // 2. Eliminazione locale (DB Regione)
        repo.deleteById(id);

        // 3. Notifica a cascata
        // Inviamo l'ID. RabbitMQ lo consegnerà a chiunque sia in ascolto.
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REGIONE_EXCHANGE,
                RabbitMQConfig.REGIONE_ROUTING_KEY,
                id
        );

        System.out.println("Evento eliminazione Regione " + id + " inviato a RabbitMQ.");
    }

    public List<RegioneDTO> search(String query) {
        return repo.findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(query).stream()
                .map(r -> new RegioneDTO(r.getId(), r.getNome()))
                .toList();
    }
}
