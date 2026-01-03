package com.uniupo.multa.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.multa.model.Multa;
import com.uniupo.multa.repository.MultaRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.CreazioneMultaEvent;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.web.client.RestTemplate;

public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final MultaRepository repo;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String TOPIC_MULTA = "multa/creaMulta";


    public mqttListener(MqttMessageBroker mqttBroker, MultaRepository repo, ObjectMapper objectMapper,RestTemplate restTemplate) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
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

    private void handleGenerazioneMulta(String topic, String message){
        try{
            System.out.println("[MULTA-LISTENER] Ricevuta richiesta per la generazione della multa");

            CreazioneMultaEvent evento = objectMapper.readValue(message, CreazioneMultaEvent.class);

            Multa multa = new Multa(evento.getIdBiglietto(), 200.0, evento.getTarga());

            repo.save(multa);

        }catch (Exception e) {
            System.err.println("[MULTA-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
