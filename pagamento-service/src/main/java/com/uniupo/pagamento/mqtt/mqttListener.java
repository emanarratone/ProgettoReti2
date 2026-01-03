package com.uniupo.pagamento.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.pagamento.model.Pagamento;
import com.uniupo.pagamento.repository.PagamentoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.AperturaSbarraEvent;
import com.uniupo.shared.mqtt.dto.CreazioneMultaEvent;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import io.github.cdimascio.dotenv.Dotenv;

public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final PagamentoRepository repo;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String TOPIC_APERTURA_SBARRA = "sbarra/apriSbarra";
    private static final String TOPIC_CALCOLO_IMPORTO = "pagamento/calcolaImporto";
    private static final String TOPIC_MULTA = "multa/creaMulta";

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

            TempoPedaggioMulte pedaggio = calcolaPedaggio(evento.getCitta_in(), evento.getCitta_out(), getTariffa(evento.getClasse_veicolo()));

            if(pedaggio == null) throw new RuntimeException();

            Pagamento pagamento = new Pagamento(evento.getIdBiglietto(), pedaggio.getPedaggio(), true, LocalDateTime.now(), evento.getCasello_out()); //per semplicità il biglietto è già pagato

            repo.save(pagamento);

            AperturaSbarraEvent event = new AperturaSbarraEvent(evento.getCasello_out(), evento.getCorsia());

            mqttBroker.publish(TOPIC_APERTURA_SBARRA, event);

            checkMulta(evento.getCitta_in(), evento.getCitta_out(), evento.getTimestamp_in(), pagamento.getTimestampOut(), pedaggio.getDistanzaKm(), evento.getIdBiglietto(), evento.getTarga());

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

    private TempoPedaggioMulte calcolaPedaggio(String caselloIngresso, String caselloUscita, Double tariffaPerKm) {
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
                    throw new RuntimeException();
                }

                // Estrai distanza e calcola pedaggio
                long distanzaMetri = element.path("distance").path("value").asLong();
                double distanzaKm = distanzaMetri / 1000.0;

                double pedaggio = distanzaKm * tariffaPerKm;

                return new TempoPedaggioMulte(distanzaKm, Math.round(pedaggio * 100) / 100.0);  //evita chiamate ridondanti alle api

            }
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
        return null;
    }

    private static class TempoPedaggioMulte{

        private Double distanzaKm;
        private double pedaggio;

        public TempoPedaggioMulte(Double distanzaKm, double pedaggio) {
            this.distanzaKm = distanzaKm;
            this.pedaggio = pedaggio;
        }

        public double getPedaggio() {
            return pedaggio;
        }

        public void setPedaggio(double pedaggio) {
            this.pedaggio = pedaggio;
        }

        public Double getDistanzaKm() {
            return distanzaKm;
        }

        public void setDistanzaKm(Double distanzaKm) {
            this.distanzaKm = distanzaKm;
        }
    }

    private void checkMulta(String caselloIngresso, String caselloUscita, LocalDateTime time_in, LocalDateTime time_out, Double distanzaKm, Integer idBiglietto, String targa) {

        try {
            System.out.println("[PAGAMENTO-LISTENER] Elaborazione multa in corso...");

            //velocità (media) = spazio/tempo

            Duration durata = Duration.between(time_in, time_out);

            long tempo = durata.toSeconds() / 3600;

            double velocita = distanzaKm / tempo;

            CreazioneMultaEvent evento = new CreazioneMultaEvent(idBiglietto, targa);

            if (velocita >= 130.0) mqttBroker.publish(TOPIC_MULTA, evento);
        }catch (Exception e) {
            System.err.println("[PAGAMENTO-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
