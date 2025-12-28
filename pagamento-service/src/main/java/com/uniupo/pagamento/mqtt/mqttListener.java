package com.uniupo.pagamento.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.pagamento.repository.PagamentoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;

public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final PagamentoRepository repo;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final String TOPIC_CALCOLO_IMPORTO = "pagamento/calcolaImporto";

    public mqttListener(MqttMessageBroker mqttBroker, PagamentoRepository repo, ObjectMapper objectMapper,RestTemplate restTemplate) {
        this.mqttBroker = mqttBroker;
        this.repo = repo;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
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

    public double calcolaPedaggio(String caselloIngresso, String caselloUscita) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("KEY_GOOGLE");

        try {
            // Distance Matrix con NOMI caselli (stringhe dirette)
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                            "origins=%s&destinations=%s&key=%s&units=metric&mode=driving",
                    URLEncoder.encode(caselloIngresso, StandardCharsets.UTF_8),
                    URLEncoder.encode(caselloUscita, StandardCharsets.UTF_8),
                    apiKey
            );


            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode element = root.path("rows").get(0).path("elements").get(0);

                String status = element.path("status").asText();
                if (!"OK".equals(status)) {
                    return -1;
                }

                // Estrai distanza e calcola pedaggio
                long distanzaMetri = element.path("distance").path("value").asLong();
                double distanzaKm = distanzaMetri / 1000.0;

                /**
                 * Aggiungere la comunicazione al microservizio per ottenere la targa e di conseguenza la classe del veicolo
                 * **/
                //double pedaggio = distanzaKm * tariffaPerKm;

                //return Math.round(pedaggio * 100) / 100.0; // 2 decimali
            }

        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
        return -1;
    }
}
