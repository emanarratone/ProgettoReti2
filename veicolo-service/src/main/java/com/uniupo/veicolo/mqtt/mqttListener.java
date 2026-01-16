package com.uniupo.veicolo.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import com.uniupo.shared.mqtt.dto.TrovaAutoEvent;
import com.uniupo.shared.mqtt.dto.TrovaCaselliEvent;
import com.uniupo.shared.mqtt.dto.richiestaPagamentoEvent;
import com.uniupo.veicolo.model.Veicolo;
import com.uniupo.veicolo.repository.VeicoloRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class mqttListener {


    private final MqttMessageBroker mqttBroker;
    private final VeicoloRepository repo;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_TARGA = "veicolo/elaboraPagamento";
    private static final String TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO = "casello/elaboraPagamento";
    private static final String TOPIC_GET_VEICOLO = "veicolo/richiesta";
    private static final String TOPIC_GET_VEICOLO_PAGAMENTO = "veicolo/richiediClasse";

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
            mqttBroker.subscribe(TOPIC_GET_VEICOLO, this::handleRichiestaVeicolo);
            mqttBroker.subscribe(TOPIC_GET_VEICOLO_PAGAMENTO, this::handleRichiestaClasse);

        } catch (MqttException e) {
            System.err.println("[AUTO-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRichiestaVeicolo(String topic, String message) {
        try {
            // 1. Python invia un JSON, quindi dobbiamo estrarre il campo "targa"
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(message);
            String targaRichiesta = root.get("targa").asText();

            System.out.println("[VEICOLO-SERVICE] ESP richiede info per targa: " + targaRichiesta);

            // 2. Cerco nel DB
            repo.findById(targaRichiesta).ifPresentOrElse(veicolo -> {
                try {
                    String jsonRisposta = objectMapper.writeValueAsString(veicolo);

                    // 3. COSTRUISCO IL TOPIC DINAMICO (deve coincidere con Python)

                    String topicRispostaDinamico = "veicolo/" + targaRichiesta + "/risposta";

                    mqttBroker.publish(topicRispostaDinamico, jsonRisposta);
                    System.out.println("[VEICOLO-SERVICE] Risposta inviata su " + topicRispostaDinamico + ": " + jsonRisposta);

                } catch (Exception e) {
                    System.err.println("Errore durante l'invio della risposta: " + e.getMessage());
                }
            }, () -> {
                System.out.println("[VEICOLO-SERVICE] Veicolo non trovato: " + targaRichiesta);
            });

        } catch (Exception e) {
            System.err.println("[VEICOLO-SERVICE] Errore parsing JSON dalla richiesta: " + e.getMessage());
        }
    }
    private void handleElaborazionePagamentoTarga (String topic, String message){

        try {
            System.out.println("[AUTO-LISTENER] Ricevuta richiesta targa: " + message);

            TrovaAutoEvent evento = objectMapper.readValue(message, TrovaAutoEvent.class);
            Veicolo veicolo = repo.getById(evento.getTarga());

            TrovaCaselliEvent event = new TrovaCaselliEvent(veicolo.getTarga(), evento.getCasello_in(), evento.getCasello_out(), veicolo.getTipoVeicolo().toString(), evento.getIdBiglietto(), evento.getCorsia(), evento.getTimestamp_in());

            mqttBroker.publish(TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO, event);

        } catch (Exception e) {
            System.err.println("[AUTO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRichiestaClasse(String topic, String message) {
        try {
            TrovaAutoEvent info = objectMapper.readValue(message, TrovaAutoEvent.class);

            // Recupera la classe dal proprio DB locale tramite targa
            String classe = repo.findByTarga(info.getTarga()).get().getTipoVeicolo().toString();

            // Creiamo un DTO che includa la classe (possiamo riusare ElaboraDistanzaEvent mettendo ID al posto dei nomi per ora)
            ElaboraDistanzaEvent arricchitoClasse = new ElaboraDistanzaEvent(
                    info.getTarga(),
                    info.getCasello_in().toString(), // Per ora passiamo l'ID come stringa
                    info.getCasello_out().toString(),
                    classe,
                    info.getIdBiglietto(),
                    info.getCasello_out(),
                    info.getCorsia(),
                    info.getTimestamp_in()
            );

            mqttBroker.publish("casello/richiediNomi", arricchitoClasse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
