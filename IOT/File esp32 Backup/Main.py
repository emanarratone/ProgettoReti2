import time

import ujson

import Config

from Wifi import Wifi

from Broker import MQTTBroker

from Sbarra import Sbarra

from Schermo import Schermo

from Telepass import Telepass

from Bottone import Bottone

from GestoreBroker import GestoreBroker

from Setup import Setup

from Corsia import Corsia



# --- HARDWARE ---

sbarra = Sbarra(True, 0, 0, 0, pin_id=Config.SERVO_PIN) 

schermo = Schermo(Config.SCL_PIN, Config.SDA_PIN, Config.WIDTH, Config.HEIGHT)

telepass = Telepass(Config.TELEPASS_PIN)

wifi = Wifi(Config.SSID, Config.PASSWORD)

broker_mqtt = MQTTBroker(Config.BROKER_IP, Config.PORT_BROKER)



if not wifi.connect() or not broker_mqtt.connect():

    raise SystemExit



gestore_mqtt = GestoreBroker(broker_mqtt)

modalita_stato = 0



id_casello_iniziale = Config.ID_CASELLO_INIZIALE

numero_corsia_iniziale = Config.NUM_CORSIA



targa_veicolo = ""

id_casello_ingresso = 0

id_casello_uscita = 0

numero_corsia = 0

tipo_corsia = ""

id_totem_corrente = 101

sigla_casello_corrente = "CASELLO"



def carica_veicolo_default():

    global targa_veicolo

    print("📡 Richiesta veicolo test...")

    payload = {"targa": "DY484VY"}

    risposta = gestore_mqtt.richiedi("veicolo/richiesta", payload, "veicolo/DY484VY/risposta")

    if risposta:

        try:

            veicolo = ujson.loads(risposta)

            targa_veicolo = veicolo.get("targa", "XXXXXXX")

            print(f"✅ Veicolo: {targa_veicolo}")

        except:

            targa_veicolo = "DY484VY"

    return targa_veicolo



def carica_dati_casello_completi(id_casello):

    global id_totem_corrente, sigla_casello_corrente, tipo_corsia, numero_corsia

    

    print(f"\n🔄 CASELLO {id_casello}")

    

    # Dispositivi

    payload_disp = {"id_casello": id_casello, "num_corsia": numero_corsia_iniziale}

    broker_mqtt.publish("dispositivi/richiesta", ujson.dumps(payload_disp))

    topic_disp = f"dispositivi/{id_casello}/corsia/{numero_corsia_iniziale}/risposta"

    dati_disp = gestore_mqtt.richiedi("dispositivi/richiesta", payload_disp, topic_disp)

    

    id_totem = 101

    if dati_disp:

        try:

            lista = ujson.loads(dati_disp)

            for d in lista:

                if "TOTEM" in str(d.get("tipoDispositivo", "")).upper():

                    id_totem = d.get("id", 101)

                    print(f"✅ Totem: {id_totem}")

                    break

        except:

            pass

    

    # Casello

    payload_casello = {"id_casello": id_casello}

    broker_mqtt.publish("casello/richiesta", ujson.dumps(payload_casello))

    topic_casello = f"casello/risposta/{id_casello}"

    dati_casello = gestore_mqtt.richiedi("casello/richiesta", payload_casello, topic_casello)

    

    sigla = f"C{id_casello}"

    if dati_casello:

        try:

            casello = ujson.loads(dati_casello)

            sigla = casello.get("sigla", sigla)

            print(f"✅ Sigla: {sigla}")

        except:

            pass

    

    # Corsia

    payload_corsia = {"id_casello": id_casello, "num_corsia": numero_corsia_iniziale}

    broker_mqtt.publish("corsia/richiesta", ujson.dumps(payload_corsia))

    topic_corsia = f"casello/{id_casello}/corsia/{numero_corsia_iniziale}/risposta"

    dati_corsia = gestore_mqtt.richiedi("casello/richiesta", payload_corsia, topic_corsia)

    

    tipo = "TELEPASS"

    if dati_corsia:

        try:

            corsia = ujson.loads(dati_corsia)

            tipo = corsia.get("tipoCorsia", tipo)

            verso = corsia.get("verso", "ENTRATA")

            print(f"✅ Corsia: tipo={tipo}, verso={verso}")

        except:

            pass

    

    id_totem_corrente = id_totem

    sigla_casello_corrente = sigla

    tipo_corsia = tipo

    numero_corsia = numero_corsia_iniziale

    

    print(f"📍 '{sigla}' | Totem:{id_totem} | Tipo:{tipo}")



def schermo_base():

    schermo.setText(

        f"BENVENUTO A:",

        sigla_casello_corrente.upper(),

        f"CORSIA {numero_corsia}",

        f"{'INGRESSO' if modalita_stato==0 else 'USCITA'}",

        f"TIPO: {tipo_corsia}"

    )



print("🚀 TOTEM REALISTICO")

targa_veicolo = carica_veicolo_default()

id_casello_ingresso = id_casello_iniziale

id_casello_uscita = id_casello_iniziale + 1



carica_dati_casello_completi(id_casello_ingresso)

schermo_base()



while True:
    
    # Controllo bottone per pubblicare topic personalizzato
    if bottone.is_pressed():
        print(f"\n🚗 Veicolo {targa_veicolo}!")



        if modalita_stato == 0:  # INGRESSO

            # Fase 1: Rilevamento

            schermo.setText(

                "VEICOLO",

                "RILEVATO",

                sigla_casello_corrente.upper(),

                f"CORSIA {numero_corsia}",

                "ATTENDERE..."

            )

            time.sleep(1.2)



            # Fase 2: Elaborazione foto

            schermo.setText(

                "ELABORAZIONE",

                "FOTO TARGA",

                sigla_casello_corrente.upper(),

                f"T:{id_totem_corrente}",

                "..."

            )

            time.sleep(0.8)



            # Fase 3: Invio dati al server

            payload = {

                "idTotem": id_totem_corrente,

                "targa": targa_veicolo,

                "idCasello": id_casello_ingresso,

                "idCorsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "INGRESSO"

            }

            broker_mqtt.publish("telecamera/fotoScattata", ujson.dumps(payload))

            print("📤 Dati inviati al server...")



            # Fase 4: Conferma

            schermo.setText(

                "INGRESSO OK",

                "DATI SALVATI",

                sigla_casello_corrente.upper(),

                f"TARGA: {targa_veicolo[-7:]}",

                "BUON VIAGGIO"

            )

            time.sleep(1.5)



            # Fase 5: Apertura sbarra

            schermo.setText(

                "SBARRA",

                "APERTURA",

                sigla_casello_corrente.upper(),

                "",

                "PASSARE"

            )

            sbarra.open_bar()

            time.sleep(3)

            sbarra.close_bar()



            # Cambio stato

            modalita_stato = 1

            schermo.setText(

                "CARICAMENTO",

                "DATI USCITA",

                "ATTENDERE",

                "",

                "..."

            )

            time.sleep(1)

            carica_dati_casello_completi(id_casello_uscita)

            schermo_base()



        else:  # USCITA

            # Fase 1: Rilevamento

            schermo.setText(

                "VEICOLO",

                "RILEVATO",

                sigla_casello_corrente.upper(),

                f"CORSIA {numero_corsia}",

                "ATTENDERE..."

            )

            time.sleep(1.2)



            # Fase 2: Check database

            schermo.setText(

                "CONTROLLO",

                "INGRESSO",

                sigla_casello_corrente.upper(),

                f"ID TOTEM:{id_totem_corrente}",

                "..."

            )

            time.sleep(0.8)



            payload_biglietto = {

                "targa": targa_veicolo,

                "idCasello": id_casello_uscita,

                "idCorsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "USCITA"

            }

            risposta = gestore_mqtt.richiedi("telecamera/ottieniTarga", payload_biglietto, f"veicolo/{targa_veicolo}/risposta")



            id_biglietto = 6

            if risposta:

                try:

                    if isinstance(risposta, str): risposta = ujson.loads(risposta)

                    id_biglietto = risposta.get('idBiglietto') or risposta.get('id') or 6

                except:

                    pass



            # Fase 3: Calcolo pedaggio

            schermo.setText(

                "CALCOLO",

                "PEDAGGIO",

                sigla_casello_corrente.upper(),

                "ID BIGLIETTO:{id_biglietto}",

                "ATTENDERE..."

            )

            time.sleep(1)



            payload_pagamento = {

                "idBiglietto": id_biglietto,

                "caselloOut": id_casello_uscita,

                "isTelepass": True,

                "targa": targa_veicolo,

                "corsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "USCITA",

                "idTotem": id_totem_corrente

            }

            broker_mqtt.publish("totem/pagaBiglietto", ujson.dumps(payload_pagamento))

            print("📤 Pagamento inviato...")

            time.sleep(0.8)



            # Fase 4: Conferma pagamento

            schermo.setText(

                "PAGAMENTO",

                "CONFERMATO",

                sigla_casello_corrente.upper(),

                "ID BIGLIETTO :{id_biglietto}",

                "GRAZIE"

            )

            time.sleep(1.8)



            # Fase 5: Apertura sbarra

            schermo.setText(

                "SBARRA",

                "APERTURA",

                sigla_casello_corrente.upper(),

                "",

                "ARRIVEDERCI"

            )

            sbarra.open_bar()

            time.sleep(3)

            sbarra.close_bar()



            # Cambio stato

            modalita_stato = 0

            schermo.setText(

                "CARICAMENTO",

                "DATI INGRESSO",

                "ATTENDERE",

            )

            time.sleep(1)

            carica_dati_casello_completi(id_casello_ingresso)

            schermo_base()
    
    if telepass.veicolo_rilevato():

        print(f"\n🚗 Veicolo {targa_veicolo}!")

        

        if modalita_stato == 0:  # INGRESSO

            # Fase 1: Rilevamento

            schermo.setText(

                "VEICOLO",

                "RILEVATO",

                sigla_casello_corrente.upper(),

                f"CORSIA {numero_corsia}",

                "ATTENDERE..."

            )

            time.sleep(1.2)

            

            # Fase 2: Elaborazione foto

            schermo.setText(

                "ELABORAZIONE",

                "FOTO TARGA",

                sigla_casello_corrente.upper(),

                f"T:{id_totem_corrente}",

                "..."

            )

            time.sleep(0.8)

            

            # Fase 3: Invio dati al server

            payload = {

                "idTotem": id_totem_corrente,

                "targa": targa_veicolo,

                "idCasello": id_casello_ingresso,

                "idCorsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "INGRESSO"

            }

            broker_mqtt.publish("telecamera/fotoScattata", ujson.dumps(payload))

            print("📤 Dati inviati al server...")

            

            # Fase 4: Conferma

            schermo.setText(

                "INGRESSO OK",

                "DATI SALVATI",

                sigla_casello_corrente.upper(),

                f"TARGA: {targa_veicolo[-7:]}",

                "BUON VIAGGIO"

            )

            time.sleep(1.5)

            

            # Fase 5: Apertura sbarra

            schermo.setText(

                "SBARRA",

                "APERTURA",

                sigla_casello_corrente.upper(),

                "",

                "PASSARE"

            )

            sbarra.open_bar()

            time.sleep(3)

            sbarra.close_bar()

            

            # Cambio stato

            modalita_stato = 1

            schermo.setText(

                "CARICAMENTO",

                "DATI USCITA",

                "ATTENDERE",

                "",

                "..."

            )

            time.sleep(1)

            carica_dati_casello_completi(id_casello_uscita)

            schermo_base()



        else:  # USCITA

            # Fase 1: Rilevamento

            schermo.setText(

                "VEICOLO",

                "RILEVATO",

                sigla_casello_corrente.upper(),

                f"CORSIA {numero_corsia}",

                "ATTENDERE..."

            )

            time.sleep(1.2)

            

            # Fase 2: Check database

            schermo.setText(

                "CONTROLLO",

                "INGRESSO",

                sigla_casello_corrente.upper(),

                f"ID TOTEM:{id_totem_corrente}",

                "..."

            )

            time.sleep(0.8)

            

            payload_biglietto = {

                "targa": targa_veicolo,

                "idCasello": id_casello_uscita,

                "idCorsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "USCITA"

            }

            risposta = gestore_mqtt.richiedi("telecamera/ottieniTarga", payload_biglietto, f"veicolo/{targa_veicolo}/risposta")

            

            id_biglietto = 6

            if risposta:

                try:

                    if isinstance(risposta, str): risposta = ujson.loads(risposta)

                    id_biglietto = risposta.get('idBiglietto') or risposta.get('id') or 6

                except:

                    pass

            

            # Fase 3: Calcolo pedaggio

            schermo.setText(

                "CALCOLO",

                "PEDAGGIO",

                sigla_casello_corrente.upper(),

                "ID BIGLIETTO:{id_biglietto}",

                "ATTENDERE..."

            )

            time.sleep(1)

            

            payload_pagamento = {

                "idBiglietto": id_biglietto,

                "caselloOut": id_casello_uscita,

                "isTelepass": True,

                "targa": targa_veicolo,

                "corsia": numero_corsia,

                "tipoCorsia": tipo_corsia,

                "verso": "USCITA",

                "idTotem": id_totem_corrente

            }

            broker_mqtt.publish("totem/pagaBiglietto", ujson.dumps(payload_pagamento))

            print("📤 Pagamento inviato...")

            time.sleep(0.8)

            

            # Fase 4: Conferma pagamento

            schermo.setText(

                "PAGAMENTO",

                "CONFERMATO",

                sigla_casello_corrente.upper(),

                "ID BIGLIETTO :{id_biglietto}",

                "GRAZIE"

            )

            time.sleep(1.8)

            

            # Fase 5: Apertura sbarra

            schermo.setText(

                "SBARRA",

                "APERTURA",

                sigla_casello_corrente.upper(),

                "",

                "ARRIVEDERCI"

            )

            sbarra.open_bar()

            time.sleep(3)

            sbarra.close_bar()

            

            # Cambio stato

            modalita_stato = 0

            schermo.setText(

                "CARICAMENTO",

                "DATI INGRESSO",

                "ATTENDERE",

            )

            time.sleep(1)

            carica_dati_casello_completi(id_casello_ingresso)

            schermo_base()

    

    time.sleep(0.1)

