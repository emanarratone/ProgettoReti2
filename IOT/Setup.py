from Wifi import Wifi #eps32 -> si collega al wifi
from Broker import MQTTBroker #esp32 -> si collega al broker
from Sbarra import Sbarra
from Schermo import Schermo
from Telepass import Telepass # Sensore IR
from Casello import Casello
from Tipo import Tipo
from Verso import Verso
from ConfiguraCorsia import ConfiguraCorsia
import Config
import ujson

#Componenti collegati all'esp32
sbarra = Sbarra(Config.SERVO_PIN,Config.ANGOLO_APERTO,Config.ANGOLO_CHIUSO)
schermo = Schermo(Config.SCL_PIN,Config.SDA_PIN,Config.WIDTH,Config.HEIGHT)
telepass = Telepass(Config.TELEPASS_PIN)
#schermo, ir, corsia, casello, dispositivo_id=None

#Wifi + Broker
wifi = Wifi(Config.SSID,Config.PASSWORD)
broker = MQTTBroker(Config.BROKER_IP,Config.PORT_BROKER)
'''
print("\nVERIFICA DELLE COMPONENTI")
if sbarra and schermo:
    print("Sbarra -> OK / Schermo -> OK")
else:
    print("Sbarra o schermo non funzionante oppure mancante")
'''
print("WIFI + BROKER :")
print("Wifi ->", wifi.connect())
if(broker.connect()):
    print("Broker -> OK")

gestore = ConfiguraCorsia(broker, id_casello_cercato=4)
risposta_raw = gestore.richiedi_dati() # Questa è la stringa JSON
#id_casello, sigla, id_autostrada, is_closed=False,limite
if risposta_raw:
    try:
        # CONVERSIONE: Trasforma la stringa in dizionario
        dati = ujson.loads(risposta_raw)
        print(dati)
        mio_casello = Casello(
            id_casello=dati['idCasello'],
            sigla=dati['sigla'],
            id_autostrada=dati['idAutostrada'],
            is_closed=dati['closed'],
            limite=dati['limite']
        )
        print(f"Configurazione completata: Casello {mio_casello.sigla} caricato.")
        if mio_casello:
            print(mio_casello)
    except Exception as e:
        print("Errore nel formato dei dati ricevuti:", e)
else:
    print("Errore: Nessuna risposta ricevuta dal broker.")
