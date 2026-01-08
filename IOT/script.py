from machine import Pin, unique_id
import time
import _thread
import random
from network import WLAN
import umqtt.simple as mqtt
import ujson
import ubinascii

# ================== CONFIG WIFI & MQTT ==================
SSID = 'S23 Ultra di Salvatore'
PASSWORD = '24022002'
BROKER_IP = '192.168.56.1'  # IP del PC/RPi con Mosquitto nell'hotspot
BROKER_PORT = 1883
CLIENT_ID = ubinascii.hexlify(unique_id())
TOPIC_PUB = b'casello/dati'
TOPIC_STATUS = b'casello/status'

# ================== CONFIG HARDWARE ==================
ir_pin = Pin(25, Pin.IN, Pin.PULL_UP)

# ================== MODALITA ==================
modalita_telepass = False

# ================== STATI FSM ==================
ATTESA_INGRESSO = 0
INGRESSO_RILEVATO = 1
ATTESA_USCITA = 2
USCITA_RILEVATA = 3
FINE = 4

stato = ATTESA_INGRESSO

# ================== DATI VEICOLO ==================
targa = "DY484DX"
casello_ingresso = ""
casello_uscita = ""
km_percorsi = 0
prezzo = 0.0
tempo_viaggio = 0.0
ha_multa = False
vel_media = 0

# ================== TIMING ==================
T_SBARRA = 3.0
debounce = 0.0

# ================== DATABASE TRATTE ==================
tratte = {
    ("MI-NORD", "AL-OVEST"): (152, 18.50),
    ("MI-NORD", "VC"): (85, 12.30),
    ("TO-OVEST", "GE-EST"): (140, 16.80),
    ("BO-NORD", "FI-SUD"): (95, 11.50)
}

# ================== MQTT FUNCTIONS ==================
mqtt_client = None

def mqtt_connect():
    global mqtt_client
    try:
        mqtt_client = mqtt.MQTTClient(CLIENT_ID, BROKER_IP, BROKER_PORT, keepalive=60)
        mqtt_client.connect()
        print('✓ MQTT Broker connesso:', BROKER_IP)
        return True
    except Exception as e:
        print('✗ MQTT errore:', e)
        return False

def pubblica_dati(tipo='uscita'):
    global mqtt_client
    try:
        if mqtt_client is None or not mqtt_client.sock:
            if not mqtt_connect():
                return

        dati = {
            'targa': targa,
            'tipo': tipo,
            'ingresso': casello_ingresso,
            'uscita': casello_uscita,
            'km_percorsi': km_percorsi,
            'prezzo': float(prezzo),
            'tempo_viaggio': float(tempo_viaggio),
            'velocita_media': vel_media,
            'ha_multa': ha_multa,
            'timestamp': time.time()
        }

        payload = ujson.dumps(dati)
        mqtt_client.publish(TOPIC_PUB, payload)
        print('✓ MQTT pubblicato:', payload[:100] + '...')

    except Exception as e:
        print('✗ MQTT publish errore:', e)

def pubblica_status(messaggio):
    try:
        if mqtt_client and mqtt_client.sock:
            payload = ujson.dumps({'status': messaggio, 'stato': stato})
            mqtt_client.publish(TOPIC_STATUS, payload)
    except:
        pass

# ================== FUNZIONI LOGICA ==================
def genera_tratta():
    global casello_ingresso, casello_uscita, km_percorsi, prezzo, tempo_viaggio, ha_multa, vel_media

    tratta = random.choice(list(tratte.keys()))
    casello_ingresso = tratta[0]
    casello_uscita = tratta[1]
    km_percorsi, prezzo = tratte[tratta]

    tempo_viaggio = random.uniform(1.0, 4.5)
    vel_media = int(km_percorsi / tempo_viaggio)
    ha_multa = vel_media > 142

# ================== FSM PRINCIPALE ==================
def update_fsm():
    global stato, debounce

    now = time.time()
    if now - debounce < 0.5:
        return

    if not modalita_telepass:  # MODALITÀ MANUALE
        if stato == ATTESA_INGRESSO:
            if ir_pin.value() == 0:
                genera_tratta()
                print("\n" + "="*40)
                print("🚗 CASELLO INGRESSO")
                print("="*40)
                print(f"🏷️  Targa:   {targa}")
                print(f"📍 Casello: {casello_ingresso}")
                print("-"*40)
                print(">>> PREMERE M per erogare biglietto <<<")
                pubblica_dati('ingresso')
                stato = INGRESSO_RILEVATO
                debounce = now

        elif stato == ATTESA_USCITA:
            if ir_pin.value() == 0:
                print("\n" + "="*40)
                print("💰 CASELLO USCITA")
                print("="*40)
                print(f"🏷️  Targa:    {targa}")
                print(f"📤 Da:       {casello_ingresso}")
                print(f"📥 A:        {casello_uscita}")
                print(f"🛣️  Distanza: {km_percorsi} km")
                print("-"*40)
                print(f"💶 PEDAGGIO:  € {prezzo:.2f}")
                print("-"*40)
                print(">>> PREMERE M per pagamento <<<")
                stato = USCITA_RILEVATA
                debounce = now

    else:  # MODALITÀ TELEPASS
        if stato == ATTESA_INGRESSO:
            if ir_pin.value() == 0:
                genera_tratta()
                print("\n" + "="*40)
                print("🏎️  INGRESSO TELEPASS")
                print("="*40)
                print(f"🏷️  Targa:   {targa}")
                print(f"📍 Casello: {casello_ingresso}")
                print("-"*40)
                print("✅ Telepass rilevato")
                print(">>> SBARRA APERTA <<<")
                pubblica_dati('ingresso_telepass')

                time.sleep(T_SBARRA)
                print(">>> SBARRA CHIUSA <<<")
                print("Buon viaggio!")
                stato = ATTESA_USCITA
                debounce = now

        elif stato == ATTESA_USCITA:
            if ir_pin.value() == 0:
                print("\n" + "="*40)
                print("🏎️  USCITA TELEPASS")
                print("="*40)
                print(f"🏷️  Targa:    {targa}")
                print(f"📤 Da:       {casello_ingresso}")
                print(f"📥 A:        {casello_uscita}")
                print(f"🛣️  Km:      {km_percorsi}")
                print(f"⏱️  Tempo:   {tempo_viaggio:.1f}h")
                print(f"🚀 Vel.med:  {vel_media} km/h")
                print("-"*40)
                print(f"💳 ADDEBITO: € {prezzo:.2f}")

                # MULTA
                if ha_multa:
                    eccesso = vel_media - 130
                    if eccesso <= 10:
                        importo = "173-695€"
                    elif eccesso <= 40:
                        importo = "543-2170€"
                    else:
                        importo = "847-3389€"
                    print("\n" + "🚨"*20)
                    print("   ECCESSO VELOCITÀ!")
                    print("🚨"*20)
                    print(f"Vel: {vel_media} km/h (Limite: 130)")
                    print(f"Sanzione: {importo}")
                else:
                    print("✅ Tutor OK")

                print("\n>>> SBARRA APERTA <<<")
                pubblica_dati('uscita_telepass')

                time.sleep(T_SBARRA)
                print(">>> SBARRA CHIUSA <<<")
                print("Arrivederci!")
                stato = FINE
                debounce = now

# ================== THREAD COMANDI ==================
def input_thread():
    global modalita_telepass, stato
    while True:
        try:
            cmd = input().strip().upper()

            if cmd == "M" and not modalita_telepass:
                if stato == INGRESSO_RILEVATO:
                    print("\n✅ Biglietto emesso")
                    print(">>> SBARRA APERTA <<<")
                    time.sleep(T_SBARRA)
                    print(">>> SBARRA CHIUSA <<<")
                    print("Buon viaggio!")
                    stato = ATTESA_USCITA
                    pubblica_status('biglietto_erogato')

                elif stato == USCITA_RILEVATA:
                    print("\n✅ Pagamento OK")
                    if ha_multa:
                        print("🚨 Multa notificata")
                    else:
                        print("✅ Tutor OK")
                    print(">>> SBARRA APERTA <<<")
                    time.sleep(T_SBARRA)
                    print(">>> SBARRA CHIUSA <<<")
                    print("Arrivederci!")
                    pubblica_dati('uscita_manuale')
                    stato = FINE

            elif cmd == "T":
                modalita_telepass = True
                stato = ATTESA_INGRESSO
                print("\n🚀 MODALITÀ TELEPASS ATTIVA")
                pubblica_status('telepass_on')

            elif cmd == "C":
                modalita_telepass = False
                stato = ATTESA_INGRESSO
                print("\n💰 MODALITÀ CONTANTI ATTIVA")
                pubblica_status('contanti_on')

            elif cmd == "R":
                stato = ATTESA_INGRESSO
                print("\n🔄 RESET COMPLETO")
                pubblica_status('reset')

            elif cmd == "S":
                print("📊 STATUS:")
                print(f"  Modalità: {'TELEPASS' if modalita_telepass else 'CONTANTI'}")
                print(f"  Stato: {stato}")
                print(f"  WiFi: {wlan.ifconfig()}")
                print(f"  MQTT: {'OK' if mqtt_client and mqtt_client.sock else 'DISCONNESSO'}")

        except Exception as e:
            print(f"Input errore: {e}")
            time.sleep(0.1)

# ================== MAIN ==================# MAIN corretto:
print("\n" + "🚗"*20)
print("CASELLO AUTOSTRADALE V2.1 ESP32")
print("🚗"*20)

# WiFi fix ESP32
import network
wlan = network.WLAN(network.STA_IF) if 'STA_IF' in dir(network.WLAN) else network.WLAN()
wlan.active(True)
print("📶 Connessione a", SSID)
wlan.connect(SSID, PASSWORD)

while not wlan.isconnected():
    time.sleep(1)
ip, _, _, _ = wlan.ifconfig()
print('✅ WiFi:', ip)
import usocket
def ping(host):
    try:
        addr = usocket.getaddrinfo(host, 1883)[0][-1]
        s = usocket.socket()
        s.settimeout(5)
        s.connect(addr)
        s.close()
        print('✅ Broker porta 1883 OK')
        return True
    except:
        print('✗ Broker non raggiungibile')
        return False

ping(BROKER_IP)

mqtt_connect()
pubblica_status('startup_ok')

print("\nComandi: C/T/M/R/S")
_thread.start_new_thread(input_thread, ())

while True:
    update_fsm()
    time.sleep(0.05)
