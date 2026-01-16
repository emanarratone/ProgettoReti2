package com.uniupo.biglietto.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.repository.BigliettoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final BigliettoRepository repo;
    @Autowired
    private final ObjectMapper objectMapper;

    public mqttListener(MqttMessageBroker mqttBroker, BigliettoRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            mqttBroker.connect();
            mqttBroker.subscribe("telecamera/fotoScattata", this::handleGenerazioneBiglietto);
            mqttBroker.subscribe("totem/pagaBiglietto", this::handlePagamentoGetBiglietto);
            mqttBroker.subscribe("telecamera/ottieniTarga", this::handleGetBiglietto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGenerazioneBiglietto(String topic, String message) {
        try {
            FotoScattataEvent evento = objectMapper.readValue(message, FotoScattataEvent.class);

            Timestamp timestamp = Timestamp.from(Instant.now());

            Biglietto biglietto = new Biglietto(
                    evento.getIdTotem(),
                    evento.getTarga(),
                    timestamp,
                    evento.getIdCasello()
            );

            repo.save(biglietto);
            System.out.println("[TICKET-LISTENER] Biglietto salvato per targa: " + evento.getTarga());
        } catch (Exception e) {
            System.err.println("[TICKET-LISTENER] Errore salvataggio: " + e.getMessage());
        }
    }
    private void handleGetBiglietto(String topic, String message) {
        try {
            // 1. Leggi la richiesta (che contiene la TARGA)
            richiestaPagamentoEvent evento = objectMapper.readValue(message, richiestaPagamentoEvent.class);
            String targa = evento.getTarga();

            System.out.println("[DEBUG] Ricevuta richiesta ID per targa: " + targa);

            // 2. Cerca l'ultimo biglietto nel DB usando la TARGA
            Biglietto b = repo.findFirstByTargaOrderByIdBigliettoDesc(targa)
                    .orElseThrow(() -> new RuntimeException("Nessun biglietto trovato per " + targa));

            // 3. Rispondi all'ESP32 sul topic che lui sta ascoltando (veicolo/TARGA/risposta)
            // Creiamo un oggetto di risposta che contenga l'idBiglietto
            String topicRisposta = "veicolo/" + targa + "/risposta";

            // Usiamo una mappa semplice o un DTO per la risposta
            java.util.Map<String, Object> risposta = new java.util.HashMap<>();
            risposta.put("idBiglietto", b.getIdBiglietto());
            risposta.put("targa", targa);
            risposta.put("idCaselloIn", b.getCaselloIn());

            mqttBroker.publish(topicRisposta, objectMapper.writeValueAsString(risposta));
            System.out.println("[DEBUG] Inviato ID " + b.getIdBiglietto() + " su " + topicRisposta);

        } catch (Exception e) {
            System.err.println("[ERROR] handleGetBiglietto fallito: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handlePagamentoGetBiglietto(String topic, String message) {
        try {
            richiestaPagamentoEvent evento = objectMapper.readValue(message, richiestaPagamentoEvent.class);
            Biglietto b = repo.findById(evento.getIdBiglietto()).orElseThrow();

            TrovaAutoEvent trova = new TrovaAutoEvent(
                    b.getTarga(),
                    b.getCaselloIn(),
                    evento.getCaselloOut(),
                    b.getIdBiglietto(),
                    evento.getCorsia(),
                    b.getTimestampIn().toLocalDateTime()
            );

            // Pubblica verso il servizio Veicoli
            mqttBroker.publish("veicolo/richiediClasse", trova);
        } catch (Exception e) { e.printStackTrace(); }
    }
}