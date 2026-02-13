package com.uniupo.corsia.rabbitMQ;

import com.uniupo.corsia.model.Corsia;
import com.uniupo.corsia.repository.CorsiaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class CaselloEventListener {

    private final CorsiaRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public CaselloEventListener(CorsiaRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.CASELLO_DELETED_QUEUE)
    @Transactional
    public void handleCaselloDeleted(Integer idCasello) {
        System.out.println("Ricevuto evento: eliminazione casello ID " + idCasello);

        List<Corsia> corsie = repo.findByCasello(idCasello);

        for (Corsia c : corsie) {

            String compositeId = idCasello + ":" + c.getNumCorsia();

            System.out.println("Propago eliminazione per corsia: " + compositeId);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CORSIA_EXCHANGE,
                    RabbitMQConfig.CORSIA_ROUTING_KEY,
                    compositeId
            );

            repo.delete(c);
        }
    }
}