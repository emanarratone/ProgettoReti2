from machine import Pin, PWM
import time

class Sbarra:
    def __init__(self, pin_id=13, angolo_aperto=40, angolo_chiuso=130):
        self.pin_id = pin_id
        self.open_angle = angolo_aperto
        self.close_angle = angolo_chiuso
        self.servo = None
        self.status = False
        self.init()

    def _angle_to_duty(self, angle): #duty -> ciclo di lavoro, il servo non usa i gradi come unità di misura
        return int(1638 + (angle / 180) * (8191 - 1638))

    def init(self):
        try:
            test_pin = Pin(self.pin_id)
            self.servo = PWM(test_pin)
            self.servo.freq(50)
            self.status = True
            self.close_bar()
            return 1
        except ValueError:
            print(f"ERRORE: Il Pin {self.pin_id} non è valido!")
            self.status = False
            return 0
        except Exception as e:
            print(f"Errore imprevisto: {e}")
            self.status = False
            return 0

    def open_bar(self):
        if self.status:
            print("Apertura della sbarra")
            self.servo.duty_u16(self._angle_to_duty(self.open_angle))
            time.sleep(3)

    def close_bar(self):
        if self.status:
            print("Chiusura della sbarra")
            self.servo.duty_u16(self._angle_to_duty(self.close_angle))
            time.sleep(1)