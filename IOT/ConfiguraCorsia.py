import ujson
import time

class ConfiguraCorsia:
    def __init__(self, broker, id_casello_cercato):
        self.broker = broker
        self.id_cercato = id_casello_cercato
        self.dati_casello = None
        # Configura il topic di ascolto specifico per questo casello
        self.topic_risposta = f"casello/risposta/{id_casello_cercato}"

    def callback_configurazione(self, topic, msg):
        """Questa funzione viene chiamata quando il microservizio risponde"""
        print("Dati ricevuti dal microservizio!")
        self.dati_casello = ujson.loads(msg)

    def richiedi_dati(self):
        # 1. Mi iscrivo al canale di risposta

        self.broker.set_callback(self.callback_configurazione)

        self.broker.subscribe(self.topic_risposta)

        # 2. Invio la richiesta (Query logica)
        # Cambia da "id" a "id_casello"
        richiesta = {"comando": "GET_CASELLO", "id_casello": 4}
        self.broker.publish("casello/richiesta", richiesta)
        # 3. Attendo la risposta (loop finché dati_casello è None)
        print(f"In attesa dei dati per il casello {self.id_cercato}...")
        tentativi = 0
        while self.dati_casello is None and tentativi < 50:
            self.broker.check_msg() # Controlla se sono arrivati messaggi MQTT
            time.sleep(0.1)
            tentativi += 1

        return self.dati_casello
