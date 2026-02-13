package com.uniupo.autostrada.service;

import com.uniupo.autostrada.model.Autostrada;
import com.uniupo.autostrada.model.dto.AutostradaCreateUpdateDTO;
import com.uniupo.autostrada.model.dto.AutostradaDTO;
import com.uniupo.autostrada.rabbitMQ.RabbitMQConfig;
import com.uniupo.autostrada.repository.AutostradaRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutostradaService {

    private final AutostradaRepository repo;
    private final RabbitTemplate rabbitTemplate;
    private final WebClient webClient;

    public AutostradaService(AutostradaRepository repo, RabbitTemplate rabbitTemplate, WebClient.Builder webClientBuilder) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
        this.webClient = webClientBuilder.build();
    }

    public List<AutostradaDTO> getAll() {
        return repo.findAll().stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }

    public List<AutostradaDTO> getByRegion(Integer idRegione) {
        return repo.findByIdRegioneOrderBySiglaAsc(idRegione).stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }

    @Transactional
    public AutostradaDTO create(AutostradaCreateUpdateDTO dto){
        Autostrada a = new Autostrada(dto.getSigla(), dto.getIdRegione());
        Autostrada saved = repo.save(a);
        return new AutostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());
    }

    @Transactional
    public AutostradaDTO update(Integer id, AutostradaCreateUpdateDTO dto) {
        Autostrada existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autostrada non trovata"));

        existing.setSigla(dto.getSigla());
        existing.setIdRegione(dto.getIdRegione());
        Autostrada saved = repo.save(existing);
        return new AutostradaDTO(saved.getId(), saved.getSigla(), saved.getIdRegione());
    }

    @Transactional
    public void delete(Integer id) {

        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Autostrada non trovata");
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUTOSTRADA_EXCHANGE,
                RabbitMQConfig.AUTOSTRADA_ROUTING_KEY,
                id
        );

        repo.deleteById(id);
    }

    public List<AutostradaDTO> search(String query) {
        return repo.findAutostradasBySiglaOrderBySiglaAsc(query).stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .toList();
    }

    public List<AutostradaDTO> getTop5Unique() {
        return repo.findTop5Unique().stream()
                .map(a -> new AutostradaDTO(a.getId(), a.getSigla(), a.getIdRegione()))
                .collect(Collectors.toList());
    }
    // Ascolta l'eliminazione della regione
    @RabbitListener(queues = "regione.deleted.queue")
    @Transactional
    public void onRegioneDeleted(Integer idRegione) {

        List<Autostrada> autostrade = repo.findByIdRegioneOrderBySiglaAsc(idRegione);

        for (Autostrada a : autostrade) {

            rabbitTemplate.convertAndSend("autostrada.exchange", "autostrada.deleted", a.getId());

            repo.delete(a);
        }
    }
}
