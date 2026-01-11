from machine import Pin
import time
from Schermo import Schermo
from Sbarra import Sbarra

class Telepass:
    def __init__(self, pin_id=4):
        self.sensore = Pin(pin_id, Pin.IN)
        self.pin_id = pin_id

    def veicolo_rilevato(self):
        if self.sensore.value() == 0  : return 1

    def attendi_veicolo(self):
        print("In attesa di un veicolo")
        while not self.veicolo_rilevato():
            time.sleep(0.1)
        print("Veicolo rilevato!")
        return True