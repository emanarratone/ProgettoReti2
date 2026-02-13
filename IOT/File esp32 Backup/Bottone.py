from machine import Pin
import time

class Bottone:
    def __init__(self, pin_id=2):
        # Bottone con resistenza pull-up interna (pressed = 0, released = 1)
        self.pin = Pin(pin_id, Pin.IN, Pin.PULL_UP)
        self._pressed_previous = False
        self._last_press_time = 0
        self.debounce_time = 200  # millisecondi
    
    def is_pressed(self):
        """Ritorna True solo alla pressione del bottone (non se tenuto premuto)"""
        current_time = time.ticks_ms()
        current_state = not self.pin.value()  # Inverto perché pull-up
        
        # Controlla se c'è un nuovo click (da non premuto a premuto)
        if current_state and not self._pressed_previous:
            # Debounce: ignora click troppo rapidi
            if time.ticks_diff(current_time, self._last_press_time) > self.debounce_time:
                self._pressed_previous = True
                self._last_press_time = current_time
                return True
        
        # Reset quando il bottone viene rilasciato
        if not current_state and self._pressed_previous:
            self._pressed_previous = False
            
        return False
    
    def is_held(self):
        """Ritorna True se il bottone è attualmente tenuto premuto"""
        return not self.pin.value()  # Inverto perché pull-up