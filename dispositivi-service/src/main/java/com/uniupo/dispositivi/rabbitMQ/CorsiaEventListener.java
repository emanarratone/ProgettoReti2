package com.uniupo.dispositivi.rabbitMQ;

import com.uniupo.dispositivi.repository.DispositivoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CorsiaEventListener {

    private final DispositivoRepository repo;

    public CorsiaEventListener(DispositivoRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitMQConfig.CORSIA_DELETED_QUEUE)
    @Transactional
    public void handleCorsiaDeleted(String compositeId) {
        try {
            // 1. Smontiamo il messaggio (es. "10:2")
            String[] parts = compositeId.split(":");
            if (parts.length != 2) return;

            Integer idCasello = Integer.parseInt(parts[0]);
            Integer numCorsia = Integer.parseInt(parts[1]);

            System.out.println("PULIZIA FINALE: Eliminazione dispositivi per Casello "
                    + idCasello + ", Corsia " + numCorsia);

            // 2. Eseguiamo l'eliminazione dei sensori/sbarre nel DB locale
            repo.deleteByCaselloAndCorsia(idCasello, numCorsia);

        } catch (Exception e) {
            System.err.println("Errore nel processare l'evento corsia: " + e.getMessage());
        }
    }
}