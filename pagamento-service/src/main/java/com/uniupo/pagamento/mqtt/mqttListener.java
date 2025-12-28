package com.uniupo.pagamento.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.pagamento.repository.PagamentoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;

public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final PagamentoRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_CALCOLO_IMPORTO = "pagamento/calcolaImporto";

    public mqttListener(MqttMessageBroker mqttBroker, PagamentoRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init(){
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_CALCOLO_IMPORTO, this::handleCalcoloImporto);


        } catch (MqttException e) {
            System.err.println("[PAGAMENTO-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCalcoloImporto(String topic, String message) {
        try{
            System.out.println("[PAGAMENTO-LISTENER] Ricevuta richiesta per la generazione del pagamento");

            ElaboraDistanzaEvent evento = objectMapper.readValue(message, ElaboraDistanzaEvent.class);



        }catch (Exception e) {
            System.err.println("[PAGAMENTO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
