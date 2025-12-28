package com.uniupo.casello.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.casello.model.Casello;
import com.uniupo.casello.repository.CaselloRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import com.uniupo.shared.mqtt.dto.TrovaCaselliEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;

public class mqttListener {


    private final MqttMessageBroker mqttBroker;
    private final CaselloRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO = "casello/elaboraPagamento";
    private static final String TOPIC_CALCOLO_IMPORTO = "pagamento/calcolaImporto";

    public mqttListener(MqttMessageBroker mqttBroker, CaselloRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init(){
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO, this::handleGenerazionePagamento);

        } catch (MqttException e) {
            System.err.println("[CASELLO-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGenerazionePagamento(String topic, String message) {
        try{
            System.out.println("[CASELLO-LISTENER] Ricevuta richiesta");

            TrovaCaselliEvent evento = objectMapper.readValue(message, TrovaCaselliEvent.class);
            Casello in = repo.getById(evento.getCasello_in());
            Casello out = repo.getById(evento.getCasello_out());

            ElaboraDistanzaEvent event = new ElaboraDistanzaEvent(in.getSigla(), out.getSigla(), evento.getClasse_veicolo(), evento.getIdBiglietto(), evento.getCasello_out());

            mqttBroker.publish(TOPIC_CALCOLO_IMPORTO, event);

        }catch (Exception e) {
            System.err.println("[CASELLO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
