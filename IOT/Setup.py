import time
import ujson
import Config
from Wifi import Wifi
from Broker import MQTTBroker
from Sbarra import Sbarra
from Schermo import Schermo
from Telepass import Telepass
from GestoreBroker import GestoreBroker

# --- FUNZIONI DI UTILITÀ ---
def pulisci_dati(dati_raw):
    if dati_raw is None:
        return None
    if isinstance(dati_raw, dict):
        return dati_raw
    if isinstance(dati_raw, str):
        try:
            return ujson.loads(dati_raw)
        except:
            return None
    return None

# --- INIZIALIZZAZIONE HARDWARE ---
sbarra = Sbarra(Config.SERVO_PIN, Config.ANGOLO_APERTO, Config.ANGOLO_CHIUSO)
schermo = Schermo(Config.SCL_PIN, Config.SDA_PIN, Config.WIDTH, Config.HEIGHT)
telepass = Telepass(Config.TELEPASS_PIN)

# --- CONNESSIONE WIFI + BROKER ---
wifi = Wifi(Config.SSID, Config.PASSWORD)
broker = MQTTBroker(Config.BROKER_IP, Config.PORT_BROKER)

print("WIFI + BROKER :")
print("Wifi ->", wifi.connect())
if broker.connect():
    print("Broker -> OK")
else:
    print("Errore: Broker non raggiungibile")

# Istanza del gestore unificato
gestore = GestoreBroker(broker)

# --- STEP 1: CONFIGURAZIONE CASELLO ---
print("\n--- STEP 1: CONFIGURAZIONE CASELLO ---")
# Nota: il microservizio Corsia risponde a questa richiesta cercando 'num_corsia IS NULL'
payload_casello = {"comando": "GET_CASELLO", "id_casello": 4}
res_casello = gestore.richiedi("casello/richiesta", payload_casello, "casello/risposta/4")
dati_casello = pulisci_dati(res_casello)

sigla = "N/A"
if dati_casello:
    sigla = dati_casello.get('sigla')
    print(f"Casello {sigla} caricato correttamente.")

# --- STEP 2: CONFIGURAZIONE CORSIA ---
print("\n--- STEP 2: CONFIGURAZIONE CORSIA ---")
payload_corsia = {
    "comando": "GET_CORSIA",
    "id_casello": 4,
    "num_corsia": 1
}
# Topic specifico per la risposta della corsia
topic_corsia = "casello/4/corsia/1/risposta"
res_corsia = gestore.richiedi("casello/richiesta", payload_corsia, topic_corsia)
dati_corsia = pulisci_dati(res_corsia)

# --- LOGICA OPERATIVA ---
print(dati_corsia)
if dati_corsia:
    # Verifichiamo se la corsia è chiusa (controllando vari nomi di campi possibili)
    is_chiusa = dati_corsia.get('closed')
    tipo = dati_corsia.get('tipo')

    if is_chiusa:
        print("⚠️ CORSIA CHIUSA")
        schermo.setText("CORSIA CHIUSA") # Usa .setText() o .setText() in base alla tua classe
        sbarra.close_bar()
    else:
        print(f"✅ Corsia {tipo} attiva. Benvenuti a {sigla}")
        schermo.setText(f"BENVENUTI A",str(sigla))

        print("\n--- AVVIO SISTEMA DI TRANSITO ---")
        while True:
            if telepass.attendi_veicolo():
                print("Auto rilevata!")
                schermo.setText("BUON VIAGGIO")
                sbarra.open_bar()
                time.sleep(2.5) # Tempo per il transito
                sbarra.close_bar()
                schermo.setText(f"BENVENUTI A",str(sigla))

            time.sleep(0.1)
else:
    print("ERRORE: Dati corsia non ricevuti dal broker.")
    schermo.setText("ERR CONFIG")
