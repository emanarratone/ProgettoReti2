package com.uniupo.dispositivi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.model.Totem;
import com.uniupo.dispositivi.mqtt.dto.BigliettoGeneratoEvent;
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
    private final BigliettoRepository repoB;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_FOTO_SCATTATA = "telecamera/fotoScattata";
    private static final String TOPIC_BIGLIETTO_GENERATO = "sbarra/apriSbarra";

    public TotemMqttListener(MqttMessageBroker mqttBroker, DispositivoRepository repo, BigliettoRepository repoB, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.repoB = repoB;
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

    private void handleFotoScattata(String topic, String message) {

        try {
            System.out.println("[TOTEM-LISTENER] Ricevuta foto targa: " + message);

            FotoScattataEvent evento = objectMapper.readValue(message, FotoScattataEvent.class);

            List<Totem> totems = repo.findById(evento.getIdTotem())
                    .stream()
                    .filter(d -> d instanceof Totem && d.getStatusBoolean())
                    .map(d -> (Totem) d).toList();

            Totem totem = totems.get(0);

            Biglietto biglietto = new Biglietto(totem.getID(), evento.getTarga(), LocalDateTime.now(), evento.getIdCasello());

            repoB.save(biglietto);

            BigliettoGeneratoEvent event = new BigliettoGeneratoEvent(
                    evento.getIdCorsia(),
                    evento.getIdCasello()
            );

            mqttBroker.publish(TOPIC_BIGLIETTO_GENERATO, event);

            System.out.println("[TOTEM-LISTENER] Biglietto generato");

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