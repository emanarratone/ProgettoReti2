package com.uniupo.casello.rabbitMQ;

import com.uniupo.casello.rabbitMQ.RabbitMQConfig;
import com.uniupo.casello.model.Casello;
import com.uniupo.casello.repository.CaselloRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AutostradaEventListener {

    private final CaselloRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public AutostradaEventListener(CaselloRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.AUTOSTRADA_DELETED_QUEUE)
    @Transactional
    public void handleAutostradaDeleted(Integer idAutostrada) {
        System.out.println("Ricevuto evento: eliminazione autostrada " + idAutostrada);

        // Trovo i caselli di questa autostrada
        List<Casello> caselli = repo.findByIdAutostradaOrderBySiglaAsc(idAutostrada);

        for (Casello c : caselli) {
            System.out.println("Propago eliminazione per casello ID: " + c.getIdCasello());

            // AVVISO LE CORSIE: Passo l'ID del casello
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CASELLO_EXCHANGE,
                    RabbitMQConfig.CASELLO_ROUTING_KEY,
                    c.getIdCasello()
            );

            // Elimino localmente
            repo.delete(c);
        }
    }
}