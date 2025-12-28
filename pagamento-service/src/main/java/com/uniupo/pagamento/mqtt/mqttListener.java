package com.uniupo.pagamento.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.pagamento.model.Pagamento;
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
import java.time.LocalDateTime;

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

            Double pedaggio = calcolaPedaggio(evento.getCitta_in(), evento.getCitta_out(), getTariffa(evento.getClasse_veicolo()));

            Pagamento pagamento = new Pagamento(evento.getIdBiglietto(), pedaggio, false, LocalDateTime.now(), evento.getCasello_out());

            repo.save(pagamento); //mi fermo qua o apro la sbarra?


        }catch (Exception e) {
            System.err.println("[PAGAMENTO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double getTariffa(String Classe){
        double val = switch (Classe) {
            case "A" -> 0.07;
            case "B" -> 0.09;
            case "C" -> 0.12;
            case "D" -> 0.16;
            case "E" -> 0.20;
            default -> 0.0;
        };
        return val;
    }

    private Double calcolaPedaggio(String caselloIngresso, String caselloUscita, Double tariffaPerKm) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("KEY_GOOGLE");

        try {

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
                    return -1.0;
                }

                // Estrai distanza e calcola pedaggio
                long distanzaMetri = element.path("distance").path("value").asLong();
                double distanzaKm = distanzaMetri / 1000.0;

                /**
                 * Aggiungere la comunicazione al microservizio per ottenere la targa e di conseguenza la classe del veicolo
                 * **/
                double pedaggio = distanzaKm * tariffaPerKm;

                return Math.round(pedaggio * 100) / 100.0; // 2 decimali
            }

        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
        return -1.0;
    }
}
