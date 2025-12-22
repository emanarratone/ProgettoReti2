package com.uniupo.biglietto.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.BigliettoGeneratoEvent;
import com.uniupo.shared.mqtt.dto.FotoScattataEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.time.LocalDateTime;
import java.util.List;

public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final BigliettoRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_FOTO_SCATTATA = "telecamera/fotoScattata";
    private static final String TOPIC_APERTURA_SBARRA = "sbarra/apriSbarra";


    public mqttListener(MqttMessageBroker mqttBroker, BigliettoRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_FOTO_SCATTATA, this::handleGenerazioneBiglietto);


        } catch (MqttException e) {
            System.err.println("[TOTEM-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGenerazioneBiglietto(String topic, String message){
        try {
            System.out.println("[TICKET-LISTENER] Ricevuta richiesta generazione biglietto: " + message);

            FotoScattataEvent evento = objectMapper.readValue(message, FotoScattataEvent.class);


            Biglietto biglietto = new Biglietto(evento.getIdTotem(), evento.getTarga(), LocalDateTime.now(), evento.getIdCasello());

            repo.save(biglietto);

            BigliettoGeneratoEvent event = new BigliettoGeneratoEvent(
                    evento.getIdCorsia(),
                    evento.getIdCasello()
            );

            mqttBroker.publish(TOPIC_APERTURA_SBARRA, event);

            System.out.println("[TOTEM-LISTENER] Biglietto generato");

        } catch (Exception e) {
            System.err.println("[TOTEM-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
