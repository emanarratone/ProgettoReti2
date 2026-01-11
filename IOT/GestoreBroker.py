import ujson
import time

class GestoreBroker:
    def __init__(self, broker):
        self.broker = broker
        self.dati_ricevuti = None
        self.topic_corrente = None
        # Impostiamo la callback una sola volta
        self.broker.set_callback(self.callback_globale)

    def callback_globale(self, topic, msg):
        t = topic.decode()
        if t == self.topic_corrente:
            try:
                # Carichiamo il JSON una sola volta
                self.dati_ricevuti = ujson.loads(msg)
            except:
                print("Errore decodifica JSON")

    def callback_mqtt(self, topic, msg):
        topic_ricevuto = topic.decode()
        if topic_ricevuto == self.topic_atteso:
            try:
                # Decodifica il messaggio da byte a stringa e poi a dizionario
                self.dati_ricevuti = ujson.loads(msg.decode('utf-8'))
            except Exception as e:
                # Se msg è già una stringa o c'è un errore, proviamo così:
                try:
                    self.dati_ricevuti = ujson.loads(msg)
                except:
                    print("Errore critico decodifica JSON:", e)
                    self.dati_ricevuti = None

    def richiedi(self, topic_pub, payload, topic_sub):
        self.dati_ricevuti = None
        self.topic_corrente = topic_sub

        self.broker.subscribe(topic_sub)
        # CORREZIONE: ujson.dumps assicura che il backend riceva JSON vero, non una stringa quotata
        self.broker.publish(topic_pub, ujson.dumps(payload))

        tentativi = 0
        while self.dati_ricevuti is None and tentativi < 50:
            self.broker.check_msg()
            time.sleep(0.1)
            tentativi += 1

        return self.dati_ricevuti