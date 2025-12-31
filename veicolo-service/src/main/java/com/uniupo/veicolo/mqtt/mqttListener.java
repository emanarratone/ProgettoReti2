package com.uniupo.veicolo.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.TrovaAutoEvent;
import com.uniupo.shared.mqtt.dto.TrovaCaselliEvent;
import com.uniupo.shared.mqtt.dto.richiestaPagamentoEvent;
import com.uniupo.veicolo.model.Veicolo;
import com.uniupo.veicolo.repository.VeicoloRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;

public class mqttListener {


    private final MqttMessageBroker mqttBroker;
    private final VeicoloRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_TARGA = "auto/elaboraPagamento";
    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO = "casello/elaboraPagamento";


    public mqttListener(MqttMessageBroker mqttBroker, VeicoloRepository repo, ObjectMapper objectMapper) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            mqttBroker.connect();

            mqttBroker.subscribe(TOPIC_ELABORAZIONE_PAGAMENTO_TARGA, this::handleElaborazionePagamentoTarga);

        } catch (MqttException e) {
            System.err.println("[AUTO-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleElaborazionePagamentoTarga (String topic, String message){

        try {
            System.out.println("[AUTO-LISTENER] Ricevuta richiesta targa: " + message);

            TrovaAutoEvent evento = objectMapper.readValue(message, TrovaAutoEvent.class);
            Veicolo veicolo = repo.getById(evento.getTarga());

            TrovaCaselliEvent event = new TrovaCaselliEvent(evento.getCasello_in(), evento.getCasello_out(), veicolo.getTipoVeicolo().toString(), evento.getIdBiglietto(), evento.getCorsia());

            mqttBroker.publish(TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO, event);

        } catch (Exception e) {
            System.err.println("[AUTO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
