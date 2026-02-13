package com.uniupo.pagamento.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.pagamento.model.Pagamento;
import com.uniupo.pagamento.repository.PagamentoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import com.uniupo.shared.mqtt.dto.CreazioneMultaEvent;
import com.uniupo.shared.mqtt.dto.ElaboraDistanzaEvent;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class mqttListener {

    private final MqttMessageBroker mqttBroker;
    private final PagamentoRepository repo;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

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
        try {
            ElaboraDistanzaEvent finale = objectMapper.readValue(message, ElaboraDistanzaEvent.class);

            TempoPedaggioMulte pedaggioInfo = calcolaPedaggio(
                    finale.getCitta_in(),
                    finale.getCitta_out(),
                    getTariffa(finale.getClasse_veicolo())
            );

            if (pedaggioInfo != null) {
                LocalDateTime oraUscita = LocalDateTime.now();

                Pagamento p = new Pagamento();
                p.setIdBiglietto(finale.getIdBiglietto());
                p.setCaselloOut(finale.getCasello_out());
                p.setPrezzo(pedaggioInfo.getPedaggio());
                p.setTimestampOut(oraUscita);
                p.setStato("PAGATO");
                repo.save(p);

                // 2. Controllo Tutor con Tolleranza
                checkMulta(
                        finale.getTimestamp_in(),
                        oraUscita,
                        pedaggioInfo.getDistanzaKm(),
                        finale.getIdBiglietto(),
                        finale.getTarga()
                );

            }
        } catch (Exception e) {
            System.err.println("[PAGAMENTO] Errore nel processamento: " + e.getMessage());
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

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("[PAGAMENTO] Errore: API KEY mancante nel file .env!");
            return null;
        }

        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                    URLEncoder.encode(caselloIngresso, StandardCharsets.UTF_8),
                    URLEncoder.encode(caselloUscita, StandardCharsets.UTF_8),
                    apiKey
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            // Logga l'intera risposta di Google per vedere cosa dice
            System.out.println("[GOOGLE-RESPONSE] " + response.getBody());

            JsonNode element = root.path("rows").get(0).path("elements").get(0);
            String status = element.path("status").asText();

            if ("OK".equals(status)) {
                long distanzaMetri = element.path("distance").path("value").asLong();
                double distanzaKm = distanzaMetri / 1000.0;
                double pedaggio = distanzaKm * tariffaPerKm;
                return new TempoPedaggioMulte(distanzaKm, Math.round(pedaggio * 100) / 100.0);
            } else {
                System.err.println("[PAGAMENTO] Google Maps ha risposto con stato: " + status +
                        " per le città: " + caselloIngresso + ", " + caselloUscita);
            }
        } catch (Exception e) {
            System.err.println("[PAGAMENTO] Errore durante la chiamata API: " + e.getMessage());
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


    private void checkMulta(LocalDateTime time_in, LocalDateTime time_out, Double distanzaKm, Integer idBiglietto, String targa) {
        try {
            // Calcolo tempo impiegato in secondi
            long secondiEffettivi = Duration.between(time_in, time_out).getSeconds();

            // Configurazione limiti
            double limiteCasello = 130.0;
            double tolleranza = 1.05; // +5%
            double limiteConTolleranza = limiteCasello * tolleranza;

            //ogni 10 secondi corrispondono al limite di velocità con tolleranza es: 105km/h -> 105km percorsi in 10 second
            double tempoMinimoConsentito = (distanzaKm / limiteConTolleranza) * 10;

            System.out.println("Secondi effettivi :" + secondiEffettivi + " Tempo minimo :" + tempoMinimoConsentito + " targa : " + targa);


            if (secondiEffettivi < tempoMinimoConsentito) {
                double velocitaMedia = distanzaKm / (secondiEffettivi) * 10;
                System.out.println("!!! ECCESSO DI VELOCITÀ RILEVATO !!!");
                System.out.println("Targa: " + targa + " | Velocità Media: " + Math.round(velocitaMedia) + " km/h");

                CreazioneMultaEvent eventoMulta = new CreazioneMultaEvent(idBiglietto, targa);
                String jsonMulta = objectMapper.writeValueAsString(eventoMulta);
                mqttBroker.publish(TOPIC_MULTA, jsonMulta);
            } else {
                System.out.println("[TUTOR] Velocità entro i limiti per la targa: " + targa);
            }
        } catch (Exception e) {
            System.err.println("[MULTA] Errore nel calcolo: " + e.getMessage());
        }
    }
}
