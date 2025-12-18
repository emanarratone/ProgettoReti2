package com.uniupo.dispositivi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.model.Totem;
import com.uniupo.dispositivi.mqtt.dto.FotoScattataEvent;
import com.uniupo.dispositivi.repository.DispositivoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TotemMqttListener {

    private final MqttMessageBroker mqttBroker;
    private final DispositivoRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_FOTO_SCATTATA = "telecamera/fotoScattata";
    private static final String TOPIC_BIGLIETTO_GENERATO = "sbarra/apriSbarra";

    public TotemMqttListener(MqttMessageBroker mqttBroker, DispositivoRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_FOTO_SCATTATA, this::handleFotoScattata);


        } catch (MqttException e) {
            System.err.println("[TOTEM-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleFotoScattata(String topic, String message){

        try{
            System.out.println("[TOTEM-LISTENER] Ricevuta foto targa: " + message);

            FotoScattataEvent evento =objectMapper.readValue(message, FotoScattataEvent.class);

            List<Totem> totems = repo.findById(evento.getIdTotem())
                    .stream()
                    .filter(d -> d instanceof Totem && d.getStatusBoolean())
                    .map(d -> (Totem) d).toList();

            Totem totem = totems.get(0);

            Biglietto biglietto = new Biglietto(totem.getID(), evento.getTarga(), LocalDateTime.now(), evento.getIdCasello());

            //GENERAZIONE TOPIC APERTURA SBARRA....FARE ANCHE PAGAMENTI

        } catch (Exception e) {
            System.err.println("[TOTEM-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }

    }


    @PreDestroy
    public void cleanup() {
        try {
            if (mqttBroker.isConnected()) {
                mqttBroker.unsubscribe(TOPIC_FOTO_SCATTATA);
                mqttBroker.disconnect();
            }
        } catch (MqttException e) {
            System.err.println("[TOTEM-LISTENER] Errore disconnessione: " + e.getMessage());

        }
    }
}