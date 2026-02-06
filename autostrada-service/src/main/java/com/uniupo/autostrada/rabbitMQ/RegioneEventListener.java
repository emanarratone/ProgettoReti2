package com.uniupo.autostrada.rabbitMQ;

import com.uniupo.autostrada.model.Autostrada;
import com.uniupo.autostrada.repository.AutostradaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RegioneEventListener {

    private final AutostradaRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public RegioneEventListener(AutostradaRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Questo metodo scatta quando la Regione invia un ID
    @RabbitListener(queues = RabbitMQConfig.REGIONE_DELETED_QUEUE)
    @Transactional
    public void handleRegioneDeleted(Integer idRegione) {
        System.out.println("Ricevuto evento: eliminazione regione " + idRegione);

        List<Autostrada> autostrade = repo.findByIdRegioneOrderBySiglaAsc(idRegione);

        for (Autostrada a : autostrade) {
            System.out.println("Propago eliminazione per autostrada: " + a.getId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUTOSTRADA_EXCHANGE,
                    RabbitMQConfig.AUTOSTRADA_ROUTING_KEY,
                    a.getId()
            );

            repo.delete(a);
        }
    }
}