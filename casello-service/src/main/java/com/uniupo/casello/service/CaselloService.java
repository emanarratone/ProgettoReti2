package com.uniupo.casello.service;

import com.uniupo.casello.model.Casello;
import com.uniupo.casello.model.dto.CaselloDTO;
import com.uniupo.casello.rabbitMQ.RabbitMQConfig;
import com.uniupo.casello.repository.CaselloRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaselloService {

    private final CaselloRepository repo;

    private final RabbitTemplate rabbitTemplate;

    public CaselloService(CaselloRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
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
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Casello non trovato con ID: " + id);
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CASELLO_EXCHANGE,
                RabbitMQConfig.CASELLO_ROUTING_KEY,
                id
        );

        repo.deleteById(id);
        System.out.println("Casello " + id + " eliminato. Messaggio inviato alle corsie.");
    }

    public List<CaselloDTO> search(String query) {
        return repo.findCaselloBySiglaOrderBySiglaAsc(query).stream()
                .map(c -> new CaselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(), 
                                         c.isClosed(), c.getLimite()))
                .toList();
    }

    public List<CaselloDTO> getByAutostrada(Integer idAutostrada) {
        return repo.findByIdAutostradaOrderBySiglaAsc(idAutostrada).stream()
                .map(c -> new CaselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(),
                                         c.isClosed(), c.getLimite()))
                .toList();
    }

    @Transactional
    public CaselloDTO createForHighway(Integer idAutostrada, String sigla, Integer limite, Boolean chiuso) {
        Casello a = new Casello(sigla, idAutostrada, chiuso != null && chiuso, limite);
        Casello saved = repo.save(a);
        return new CaselloDTO(saved.getIdCasello(), saved.getSigla(), saved.getIdAutostrada(), saved.isClosed(), saved.getLimite());
    }

    public java.util.Optional<CaselloDTO> getById(Integer id) {
        return repo.findById(id).map(c -> new CaselloDTO(c.getIdCasello(), c.getSigla(), c.getIdAutostrada(), c.isClosed(), c.getLimite()));
    }

    @Transactional
    public CaselloDTO updateFromDTO(Integer id, CaselloDTO dto) {
        return update(id, dto);
    }
}
