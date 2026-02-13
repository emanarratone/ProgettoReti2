package com.uniupo.dispositivi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniupo.dispositivi.model.Telecamera;
import com.uniupo.shared.mqtt.dto.FotoScattataEvent;
import com.uniupo.shared.mqtt.dto.RichiestaBigliettoEvent;
import com.uniupo.dispositivi.repository.DispositivoRepository;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;


@Component
public class TelecameraMqttListener {

    private final MqttMessageBroker mqttBroker;
    private final DispositivoRepository dispositivoRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    private static final String TOPIC_RICHIESTA_BIGLIETTO = "totem/generaBiglietto";
    private static final String TOPIC_FOTO_SCATTATA = "telecamera/fotoScattata";

    public TelecameraMqttListener(MqttMessageBroker mqttBroker, 
                                 DispositivoRepository dispositivoRepository) {
        this.mqttBroker = mqttBroker;
        this.dispositivoRepository = dispositivoRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        try {
            mqttBroker.connect();
            
            // Sottoscrizione al topic delle richieste di biglietto
            mqttBroker.subscribe(TOPIC_RICHIESTA_BIGLIETTO, this::handleRichiestaBiglietto);
            
            System.out.println("[TELECAMERA-LISTENER] Sottoscritto al topic: " + TOPIC_RICHIESTA_BIGLIETTO);
        } catch (MqttException e) {
            System.err.println("[TELECAMERA-LISTENER] Errore connessione MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce l'evento di richiesta biglietto
     */
    private void handleRichiestaBiglietto(String topic, String message) {
        try {
            System.out.println("[TELECAMERA-LISTENER] Ricevuta richiesta biglietto: " + message);
            
            // Deserializza il messaggio
            RichiestaBigliettoEvent richiesta = objectMapper.readValue(message, RichiestaBigliettoEvent.class);
            
            // Trova la telecamera attiva per quella corsia/casello
            List<Telecamera> telecamere = dispositivoRepository
                    .findByCaselloAndCorsia(richiesta.getIdCasello(), richiesta.getIdCorsia())
                    .stream()
                    .filter(d -> d instanceof Telecamera && d.getStatusBoolean())
                    .map(d -> (Telecamera) d)
                    .toList();
            
            if (telecamere.isEmpty()) {
                System.err.println("[TELECAMERA-LISTENER] Nessuna telecamera attiva trovata per casello " 
                        + richiesta.getIdCasello() + " corsia " + richiesta.getIdCorsia());
                return;
            }
            
            Telecamera telecamera = telecamere.get(0);
            
            // Simula lo scatto della foto e riconoscimento targa
            System.out.println("[TELECAMERA-LISTENER] Telecamera " + telecamera.getID() + " sta scattando foto...");
            
            // Simula un tempo di elaborazione
            //Thread.sleep(500);
            
            // Genera una targa simulata (in produzione arriverebbe dal riconoscimento OCR)
            String targaSimulata = generaTargaSimulata();
            
            // Crea l'evento di foto scattata
            FotoScattataEvent fotoEvent = new FotoScattataEvent(
                    telecamera.getID(),
                    richiesta.getIdTotem(),
                    richiesta.getIdCorsia(),
                    richiesta.getIdCasello(),
                    targaSimulata,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    "foto_" + telecamera.getID() + "_" + System.currentTimeMillis() + ".jpg"
            );
            
            // Pubblica l'evento di foto scattata
            mqttBroker.publish(TOPIC_FOTO_SCATTATA, fotoEvent);
            
            System.out.println("[TELECAMERA-LISTENER] Foto scattata e pubblicata - Targa: " + targaSimulata);
            
        } catch (Exception e) {
            System.err.println("[TELECAMERA-LISTENER] Errore gestione richiesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera una targa simulata in formato italiano (AB123CD)
     */
    private String generaTargaSimulata() {
        String lettere1 = "" + (char)('A' + random.nextInt(26)) + (char)('A' + random.nextInt(26));
        String numeri = String.format("%03d", random.nextInt(1000));
        String lettere2 = "" + (char)('A' + random.nextInt(26)) + (char)('A' + random.nextInt(26));
        return lettere1 + numeri + lettere2;
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (mqttBroker.isConnected()) {
                mqttBroker.unsubscribe(TOPIC_RICHIESTA_BIGLIETTO);
                mqttBroker.disconnect();
            }
        } catch (MqttException e) {
            System.err.println("[TELECAMERA-LISTENER] Errore disconnessione: " + e.getMessage());
        }
    }
}

