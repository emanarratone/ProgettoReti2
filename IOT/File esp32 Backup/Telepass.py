from machine import Pin
import time

# --- LA TUA CLASSE ---
class Telepass:
    def __init__(self, pin_id=4):
        self.sensore = Pin(pin_id, Pin.IN)
        self._occupato_precedente = False 

    def veicolo_rilevato(self):
        # 0 = Rilevato, 1 = Libero
        stato_attuale = (self.sensore.value() == 0)
        
        # Logica: rileva solo se prima era libero e ora è occupato
        if stato_attuale and not self._occupato_precedente:
            time.sleep_ms(50) # Debounce
            if self.sensore.value() == 0:
                self._occupato_precedente = True
                return True
        
        # RESET: Se il sensore torna a 1, permetti un nuovo rilevamento
        if not stato_attuale and self._occupato_precedente:
            print("🔄 Sensore Libero: pronto per prossima auto")
            self._occupato_precedente = False
            
        return False