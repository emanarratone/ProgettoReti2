from machine import Pin, I2C
import ssd1306
import time

class Schermo:
    def __init__(self, scl_pin=22, sda_pin=21, width=128, height=64):
        self.width = width
        self.height = height
        self.status = True
        try:
            self.i2c = I2C(0, scl=Pin(scl_pin), sda=Pin(sda_pin))
            self.oled = ssd1306.SSD1306_I2C(self.width, self.height, self.i2c)
            self.disponibile = True
        except Exception as e:
            print(f"Errore inizializzazione OLED: {e}")
            self.disponibile = False

    def setText(self, riga1="", riga2="", riga3="",riga4="",riga5="",riga6=""):
        if not self.disponibile:
            return

        self.oled.fill(0)
        if riga1: self.oled.text(riga1, 0, 0)
        if riga2: self.oled.text(riga2, 0, 10)
        if riga3: self.oled.text(riga3, 0, 20)
        if riga4: self.oled.text(riga4, 0, 30)
        if riga5: self.oled.text(riga5, 0, 40)
        if riga6: self.oled.text(riga6, 0, 50)
        self.oled.show()

    def clear(self):
        if self.disponibile:
            self.oled.fill(0)
            self.oled.show()
