package com.uniupo.casello.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.casello.model.Casello;
import com.uniupo.casello.repository.CaselloRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import com.uniupo.shared.mqtt.dto.RichiestaDatiCaselloEvent;
import com.uniupo.shared.mqtt.dto.TrovaCaselliEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

@Component
public class mqttListener {


    private final MqttMessageBroker mqttBroker;
    private final CaselloRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_RICHIESTA_CONFIG = "casello/richiesta";
    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO = "casello/elaboraPagamento";
    private static final String TOPIC_CALCOLO_IMPORTO = "pagamento/calcolaImporto";
    private static final String TOPIC_RISPOSTA_CONFIG = "casello/risposta/";

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
            mqttBroker.subscribe(TOPIC_RICHIESTA_CONFIG, this::handleRichiestaConfigurazione);
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

            ElaboraDistanzaEvent event = new ElaboraDistanzaEvent(evento.getTarga(), in.getSigla(), out.getSigla(), evento.getClasse_veicolo(), evento.getIdBiglietto(), evento.getCasello_out(), evento.getCorsia(), evento.getTimestamp_in());

            mqttBroker.publish(TOPIC_CALCOLO_IMPORTO, event);

        }catch (Exception e) {
            System.err.println("[CASELLO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRichiestaConfigurazione(String topic, String message) {
        try {

            if (message.startsWith("\"") && message.endsWith("\"")) {
                message = objectMapper.readValue(message, String.class);
            }
            RichiestaDatiCaselloEvent richiesta = objectMapper.readValue(message, RichiestaDatiCaselloEvent.class);

            // Log di controllo: se vedi "null", il JSON dell'ESP32 è sbagliato
            System.out.println("[CASELLO-DEBUG] Cerco casello ID: " + richiesta.getIdCasello());

            repo.findById(richiesta.getIdCasello()).ifPresentOrElse(casello -> {
                try {
                    String jsonRisposta = objectMapper.writeValueAsString(casello);
                    mqttBroker.publish("casello/risposta/" + casello.getIdCasello(), jsonRisposta);
                    System.out.println("[CASELLO-SUCCESS] Risposta inviata!");
                } catch (Exception e) { e.printStackTrace(); }
            }, () -> {
                System.err.println("[CASELLO-WARN] Casello " + richiesta.getIdCasello() + " non trovato nel DB!");
            });

        } catch (Exception e) {
            System.err.println("[CASELLO-ERROR] Errore parsing: " + e.getMessage());
        }
    }

}
