package com.uniupo.dispositivi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.dispositivi.model.Sbarra;
import com.uniupo.shared.mqtt.dto.AperturaSbarraEvent;
import com.uniupo.dispositivi.repository.DispositivoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SbarraMqttListener {

    private final MqttMessageBroker mqttBroker;
    private final DispositivoRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_APERTURA_SBARRA = "sbarra/apriSbarra";

    public SbarraMqttListener(MqttMessageBroker mqttBroker, DispositivoRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {

            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_APERTURA_SBARRA, this::handleAperturaSbarra);

        } catch (MqttException e) {
            System.err.println("[GATE-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAperturaSbarra(String topic, String message){

        try{
            System.out.println("[GATE-LISTENER] Ricevuta foto targa: " + message);

            AperturaSbarraEvent evento = objectMapper.readValue(message, AperturaSbarraEvent.class);

            List<Sbarra> sbarre = repo.findByCaselloAndCorsia(evento.getIdCasello(), evento.getIdCorsia())
                    .stream()
                    .filter(d -> d instanceof Sbarra && d.getStatusBoolean())
                    .map(d -> (Sbarra) d).toList();

            Sbarra sbarra = sbarre.get(0);

        } catch (Exception e) {
            System.err.println("[GATE-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
