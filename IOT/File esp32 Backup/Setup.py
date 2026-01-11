import time
import ujson
import Config
from Wifi import Wifi
from Broker import MQTTBroker
from Sbarra import Sbarra
from Schermo import Schermo
from Telepass import Telepass
from GestoreBroker import GestoreBroker
from Casello import Casello
from Corsia import Corsia 

# --- VARIABILI GLOBALI ---
targa_veicolo = "DY484VY"
id_casello_entrata = 4
id_casello_uscita = 5
num_corsia = 1  # Stesso ID per entrata/uscita

# --- FUNZIONI DI UTILITÀ ---
def pulisci_dati(dati_raw):
    """Pulisce e parsifica dati MQTT in dict JSON"""
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
print("🔌 Connessione WIFI + BROKER...")
wifi = Wifi(Config.SSID, Config.PASSWORD)
if not wifi.connect():
    print("❌ WIFI fallito!")
    exit(1)

broker = MQTTBroker(Config.BROKER_IP, Config.PORT_BROKER)
if broker.connect():
    print("✅ Broker -> OK")
else:
    print("❌ Errore: Broker non raggiungibile")
    exit(1)

gestore = GestoreBroker(broker)

# --- STEP 1: CONFIGURAZIONE VEICOLO ---
print("\n🚗 --- STEP 1: CONFIGURAZIONE VEICOLO ---")
payload_veicolo = {"comando": "GET_AUTO", "targa": targa_veicolo}
topic_risposta_auto = f"veicolo/{targa_veicolo}/risposta"

print(f"📡 Richiesta auto per targa: {targa_veicolo}")
dati_veicolo = pulisci_dati(gestore.richiedi("veicolo/richiesta", payload_veicolo, topic_risposta_auto))

if dati_veicolo and dati_veicolo.get('tipoVeicolo'):
    print(f"✅ Veicolo {targa_veicolo} è di Classe {dati_veicolo['tipoVeicolo']}")
else:
    print(f"⚠️ Nessuna risposta per targa {targa_veicolo}")

# --- STEP 2: CONFIGURAZIONE CASELLO E CORSIA ---
def configura_casello_corsia(id_casello, descrizione):
    """Funzione helper per casello + corsia"""
    print(f"\n📍 --- {descrizione} (ID: {id_casello}) ---")
    
    # 2.1 Casello di entrata
    payload_casello = {"comando": "GET_CASELLO", "id_casello": id_casello}
    topic_risp_casello = f"casello/risposta/{id_casello}"
    
    dati_casello = pulisci_dati(gestore.richiedi("casello/richiesta", payload_casello, topic_risp_casello))
    if not dati_casello:
        print(f"❌ Casello {id_casello} non trovato!")
        return None, None
    
    casello = Casello(
        id_casello,
        dati_casello.get("sigla", "N/A"),
        dati_casello.get("idAutostrada", 0),
        dati_casello.get("closed", False),
        dati_casello.get("limite", 0)
    )
    print(f"✅ Casello : {casello}")
    
    # 2.2 Corsia
    payload_corsia = {
        "comando": "GET_CORSIA",
        "id_casello": id_casello,
        "num_corsia": num_corsia
    }
    topic_corsia = f"casello/{id_casello}/corsia/{num_corsia}/risposta"
    
    dati_corsia = pulisci_dati(gestore.richiedi("casello/richiesta", payload_corsia, topic_corsia))
    if not dati_corsia:
        print(f"❌ Corsia {num_corsia} non trovata!")
        return casello, None
    
    corsia = Corsia(
        num_corsia,
        id_casello,
        dati_corsia.get("verso"),
        dati_corsia.get("tipo"),
        dati_corsia.get("closed", False)
    )
    print(f"✅ Corsia {num_corsia}: {corsia}")
    
    return casello, corsia

# Esegui configurazione per entrata e uscita
casello_entrata, corsia_entrata = configura_casello_corsia(id_casello_entrata, "CASELLO/CORSIA ENTRATA")
casello_uscita, corsia_uscita = configura_casello_corsia(id_casello_uscita, "CASELLO/CORSIA USCITA")

# --- VERIFICA FINALE ---
print("\n🏁 --- CONFIGURAZIONE COMPLETATA ---")
print(f"Veicolo: {targa_veicolo}")
print(f"Entrata: Casello {id_casello_entrata} Corsia {num_corsia} -> {corsia_entrata}")
print(f"Uscita: Casello {id_casello_uscita} Corsia {num_corsia} -> {corsia_uscita}")

if casello_entrata and corsia_entrata and casello_uscita and corsia_uscita:
    print("🎉 TUTTO OK - Pronto per simulazione!")
else:
    print("⚠️ Configurazione incompleta!")
