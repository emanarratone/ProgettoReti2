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

gestore = GestoreBroker(broker)

# --- STEP 1: CONFIGURAZIONE VEICOLO ---
print("\n--- STEP 1: CONFIGURAZIONE VEICOLO ---")

# Definiamo la targa come variabile per riutilizzarla facilmente
# FIX: targa corretta (coerente con il veicolo usato negli script)
targa_veicolo = "DY484DX"

payload_veicolo = {
    "comando": "GET_AUTO",
    "targa" : targa_veicolo
} 

topic_risposta_auto = f"auto/{targa_veicolo}/risposta"

print(f"Richiesta auto per targa: {targa_veicolo}")
print(f"In attesa su topic: {topic_risposta_auto}")

# Invio della richiesta e attesa della risposta
res_veicolo = gestore.richiedi("auto/richiesta", payload_veicolo, topic_risposta_auto)
dati_veicolo = pulisci_dati(res_veicolo)

if dati_veicolo:
    # Nota: Java invia l'oggetto Veicolo che ha il campo 'tipoVeicolo'
    tipo = dati_veicolo.get('tipoVeicolo', 'N/A')
    print(f"✅ Veicolo caricato: Targa {targa_veicolo}, Classe {tipo}")
else:
    print("❌ ERRORE: Veicolo non trovato o timeout risposta")

"""# --- STEP 2: CONFIGURAZIONE CASELLO ---
print("\n--- STEP 2: CONFIGURAZIONE CASELLO ---")
id_casello = 4
payload_casello = {
    "comando": "GET_CASELLO",
    "id_casello": id_casello
}
# FIX: Corretta la f-string per il topic di risposta
topic_risp_casello = f"casello/risposta/{id_casello}"
res_casello = gestore.richiedi("casello/richiesta", payload_casello, topic_risp_casello)
dati_casello = pulisci_dati(res_casello)

sigla = "N/A"
if dati_casello:
    sigla = dati_casello.get('sigla', 'N/A')
    print(f"Casello {sigla} caricato correttamente.")

# --- STEP 3: CONFIGURAZIONE CORSIA ---
print("\n--- STEP 3: CONFIGURAZIONE CORSIA ---")
num_corsia = 1
payload_corsia = {
    "comando": "GET_CORSIA", 
    "id_casello": id_casello, 
    "num_corsia": num_corsia
}
# FIX: Usiamo le f-string per comporre il topic in modo corretto (senza virgole)
topic_corsia = f"casello/{id_casello}/corsia/{num_corsia}/risposta"
res_corsia = gestore.richiedi("casello/richiesta", payload_corsia, topic_corsia)
dati_corsia = pulisci_dati(res_corsia)

# --- LOGICA OPERATIVA ---
if dati_corsia:
    is_chiusa = dati_corsia.get('closed', False)
    tipo = dati_corsia.get('tipo', 'N/D')
    
    if is_chiusa:
        print("⚠️ CORSIA CHIUSA")
        schermo.setText("CORSIA CHIUSA")
        sbarra.close_bar()
    else:
        msg_benvenuto = f"BENVENUTI A {sigla}"
        print(f"✅ Corsia {tipo} attiva. {msg_benvenuto}")
        schermo.setText(msg_benvenuto)
        
        print("\n--- AVVIO SISTEMA DI TRANSITO ---")
        while True:
            if telepass.attendi_veicolo():
                print("Auto rilevata!")
                schermo.setText("BUON VIAGGIO")
                sbarra.open_bar()
                time.sleep(2.5)
                sbarra.close_bar()
                schermo.setText(msg_benvenuto)
            
            time.sleep(0.1)
else:
    print("ERRORE: Dati corsia non ricevuti dal broker.")
    schermo.setText("ERR CONFIG")"""
