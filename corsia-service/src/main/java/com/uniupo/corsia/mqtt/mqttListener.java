package com.uniupo.corsia.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.corsia.*;
import com.uniupo.corsia.repository.CorsiaRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.RichiestaDatiCaselloEvent;
import com.uniupo.shared.mqtt.dto.RichiestaDatiCorsiaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

@Component
public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final CorsiaRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_RICHIESTA_CONFIG = "casello/richiesta";
    private static final String TOPIC_RISPOSTA_CONFIG = "casello/risposta";

    public mqttListener(MqttMessageBroker mqttBroker, CorsiaRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init(){
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_RICHIESTA_CONFIG, this::handleRichiestaConfigurazione);
        } catch (MqttException e) {
            System.err.println("[CASELLO-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRichiestaConfigurazione(String topic, String message) {
        try {
            if (message.startsWith("\"") && message.endsWith("\"")) {
                // Deserializza la stringa nella sua forma JSON reale
                message = objectMapper.readValue(message, String.class);
            }

            RichiestaDatiCorsiaEvent richiesta = objectMapper.readValue(message, RichiestaDatiCorsiaEvent.class);

            System.out.println("Ricevuto correttamente: Casello=" + richiesta.getIdCasello() + ", Corsia=" + richiesta.getNumCorsia());

            repo.findByCaselloAndNumCorsia(richiesta.getIdCasello(), richiesta.getNumCorsia())
                    .ifPresentOrElse(corsia -> {
                        try {
                            String jsonRisposta = objectMapper.writeValueAsString(corsia);

                            String topicRisposta = "casello/" + richiesta.getIdCasello() +
                                    "/corsia/" + richiesta.getNumCorsia() + "/risposta";

                            System.out.println("[DEBUG] Invio risposta su: " + topicRisposta);
                            mqttBroker.publish(topicRisposta, jsonRisposta);

                        } catch (Exception e) { e.printStackTrace(); }
                    }, () -> System.out.println("Corsia non trovata nel DB"));

        } catch (Exception e) {
            System.err.println("Errore parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
