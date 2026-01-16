package com.uniupo.multa.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.multa.model.Multa;
import com.uniupo.multa.repository.MultaRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.CreazioneMultaEvent;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final MultaRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_MULTA = "multa/creaMulta";


    public mqttListener(MqttMessageBroker mqttBroker, MultaRepository repo, ObjectMapper objectMapper,RestTemplate restTemplate) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init(){
        try {
            mqttBroker.connect();
            mqttBroker.subscribe(TOPIC_MULTA, this::handleGenerazioneMulta);
        } catch (MqttException e) {
                System.err.println("[MULTA-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGenerazioneMulta(String topic, String message) {
        try {
            // 1. Pulizia e lettura (già funzionante dai tuoi log)
            String cleanMessage = message;
            if (cleanMessage.startsWith("\"") && cleanMessage.endsWith("\"")) {
                cleanMessage = cleanMessage.substring(1, cleanMessage.length() - 1).replace("\\\"", "\"");
            }
            CreazioneMultaEvent evento = objectMapper.readValue(cleanMessage, CreazioneMultaEvent.class);

            // 2. CREAZIONE ENTITY PER DATABASE
            // Devi mappare i dati dell'evento nell'oggetto che Hibernate salva
            Multa nuovaMulta = new Multa();
            nuovaMulta.setTarga(evento.getTarga());
            nuovaMulta.setIdBiglietto(evento.getIdBiglietto());

            // Imposta un importo fisso o calcolato (visto che nel DB è NOT NULL)
            nuovaMulta.setImporto(150.00);
            nuovaMulta.setPagato(false);

            // 3. SALVATAGGIO EFFETTIVO
            repo.save(nuovaMulta);

            System.out.println("[MULTA-LISTENER] Record salvato correttamente nel DB per: " + evento.getTarga());

        } catch (Exception e) {
            System.err.println("[MULTA-LISTENER] Errore durante il salvataggio: " + e.getMessage());
        }
    }
}
