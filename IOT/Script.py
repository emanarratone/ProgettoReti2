import network  # ← AGGIUNTO
from machine import Pin, unique_id
import time, _thread, random, ujson, ubinascii, usocket
from network import WLAN
import umqtt.simple as mqtt

# ================== CONFIG ==================
SSID = b'S23 Ultra di Salvatore'
PASSWORD = b'24022002'
MQTT_BROKER = b'10.187.40.129'
BROKER_PORT = 1883
CLIENT_ID = ubinascii.hexlify(unique_id())
CASello = b'AL_Est'

# ================== TOPICS ==================
TOPIC_FOTO_SCATTATA = b"telecamera/fotoScattata"
TOPIC_APERTURA_SBARRA = b"sbarra/apriSbarra"
TOPIC_RICHIESTA_PAGAMENTO = b"totem/pagaBiglietto"
TOPIC_ELABORAZIONE_PAGAMENTO_TARGA = b"pagamento/elaboraPagamento"
TOPIC_ELABORAZIONE_PAGAMENTO_CASELLO = b"casello/elaboraPagamento"
TOPIC_CALCOLO_IMPORTO = b"pagamento/calcolaImporto"
TOPIC_RICHIESTA_BIGLIETTO = b"totem/generaBiglietto"
TOPIC_MULTA = b"multa/creaMulta"
TOPIC_STATUS = b"casello/status"

# ================== HARDWARE ==================
ir_pin = Pin(4, Pin.IN, Pin.PULL_UP)

# ================== FSM ==================
ATTESA_INGRESSO, INGRESSO_RILEVATO, ATTESA_USCITA, USCITA_RILEVATA, FINE = range(5)
stato = ATTESA_INGRESSO
modalita_telepass = False
targa = "DY484DX"
casello_ingresso, casello_uscita = "", ""
km_percorsi, prezzo, tempo_viaggio, vel_media = 0, 0.0, 0.0, 0
ha_multa = False
T_SBARRA = 3.0
debounce = 0.0

tratte = {("MI-NORD", "AL-OVEST"): (152, 18.50), ("MI-NORD", "VC"): (85, 12.30)}

# ================== MQTT ==================
mqtt_client = None

def test_port(host):
    try:
        addr = usocket.getaddrinfo(host, 1883)[0][-1]
        s = usocket.socket(); s.settimeout(5); s.connect(addr); s.close()
        print('✅ Broker OK')
        return True
    except: print('✗ Broker KO'); return False

def mqtt_connect():
    global mqtt_client
    mqtt_client = mqtt.MQTTClient(CLIENT_ID, MQTT_BROKER, keepalive=60)
    mqtt_client.set_last_will(TOPIC_STATUS, ujson.dumps({'status':'offline'}), True)
    mqtt_client.connect()
    mqtt_client.set_callback(on_message)
    mqtt_client.subscribe(TOPIC_APERTURA_SBARRA)
    mqtt_client.subscribe(TOPIC_RICHIESTA_PAGAMENTO)
    print('✓ MQTT online')
    return True

def on_message(topic, msg):
    try:
        data = ujson.loads(msg)
        print(f'📨 {topic.decode()}: {data}')
    except: print(f'📨 {topic}: {msg}')

def pubblica(topic, data):
    global mqtt_client
    if mqtt_client and mqtt_client.sock:
        try: mqtt_client.publish(topic, ujson.dumps(data))
        except: pass

# ================== FSM ==================
def genera_tratta():
    global casello_ingresso, casello_uscita, km_percorsi, prezzo, tempo_viaggio, vel_media, ha_multa
    tratta = random.choice(list(tratte.keys()))
    casello_ingresso, casello_uscita = tratta
    km_percorsi, prezzo = tratte[tratta]
    tempo_viaggio = random.uniform(1.0, 4.5)
    vel_media = int(km_percorsi / tempo_viaggio)
    ha_multa = vel_media > 142

def update_fsm():
    global stato, debounce
    now = time.time()
    if now - debounce < 0.5: return
    debounce = now

    if ir_pin.value():
        if stato == ATTESA_INGRESSO:
            genera_tratta()
            dati = {'targa': targa, 'casello': CASello.decode(), 'timestamp': now}
            pubblica(TOPIC_RICHIESTA_BIGLIETTO, dati)
            foto = {'targa': targa, 'casello': CASello.decode()}
            pubblica(TOPIC_FOTO_SCATTATA, foto)
            print(f'🚗 Ingresso {CASello}: {targa}')
            stato = INGRESSO_RILEVATO

        elif stato == ATTESA_USCITA:
            dati = {'targa': targa, 'ingresso': casello_ingresso, 'uscita': casello_uscita, 'prezzo': prezzo}
            pubblica(TOPIC_ELABORAZIONE_PAGAMENTO_TARGA, dati)
            if ha_multa:
                pubblica(TOPIC_MULTA, {'targa': targa, 'velocita': vel_media})
            print(f'💰 Uscita: €{prezzo:.2f} {"+MULTA" if ha_multa else ""}')
            stato = USCITA_RILEVATA

# ================== COMANDI ==================
def input_thread():
    global modalita_telepass, stato
    while True:
        try:
            cmd = input().strip().upper()
            if cmd == 'T': modalita_telepass = True; stato = ATTESA_INGRESSO; print('🏎️ Telepass')
            elif cmd == 'C': modalita_telepass = False; stato = ATTESA_INGRESSO; print('💰 Contanti')
            elif cmd == 'R': stato = ATTESA_INGRESSO; print('🔄 Reset')
            elif cmd == 'S': print(f'Stato: {stato}, Modalità: {"Telepass" if modalita_telepass else "Contanti"}')
        except: time.sleep(0.1)

# ================== MAIN ==================
print("🚗 CASELLO ESP32")
wlan = WLAN(network.STA_IF)
wlan.active(True); wlan.connect(SSID, PASSWORD)
while not wlan.isconnected(): print('📶 WiFi...');
print('✅ WiFi:', wlan.ifconfig()[0])

test_port(MQTT_BROKER)
mqtt_connect()
pubblica(TOPIC_STATUS, {'status': 'online', 'casello': CASello})

print("Comandi: C/T/R/S | IR Pin25")
_thread.start_new_thread(input_thread, ())

while True:
    update_fsm()
    if mqtt_client: mqtt_client.check_msg()
    time.sleep(0.05)