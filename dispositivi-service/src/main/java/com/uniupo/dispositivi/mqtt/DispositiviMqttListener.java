package com.uniupo.dispositivi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.repository.DispositivoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.RichiestaDatiCorsiaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DispositiviMqttListener {

    private final MqttMessageBroker mqttBroker;
    private final DispositivoRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_RICHIESTA_CONFIG = "dispositivi/richiesta";

    public DispositiviMqttListener(MqttMessageBroker mqttBroker, DispositivoRepository repo, ObjectMapper objectMapper) {
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

            System.out.println("Ricevuto correttamente: dispositivi =" );

            List<Dispositivo> dispositivi = repo.findByCaselloAndCorsia(richiesta.getIdCasello(), richiesta.getNumCorsia());

            if (!dispositivi.isEmpty()) {
                try {
                    String jsonRisposta = objectMapper.writeValueAsString(dispositivi);

                    String topicRisposta = "dispositivi/" + richiesta.getIdCasello() +
                            "/corsia/" + richiesta.getNumCorsia() + "/risposta";

                    System.out.println("[DEBUG] Invio configurazione (" + dispositivi.size() + " dispositivi) su: " + topicRisposta);
                    mqttBroker.publish(topicRisposta, jsonRisposta);

                } catch (Exception e) {
                    System.err.println("Errore durante l'invio della configurazione: " + e.getMessage());
                }
            } else {
                System.out.println("[WARN] Nessun dispositivo trovato per Casello " +
                        richiesta.getIdCasello() + ", Corsia " + richiesta.getNumCorsia());
            }

        } catch (Exception e) {
            System.err.println("Errore parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
