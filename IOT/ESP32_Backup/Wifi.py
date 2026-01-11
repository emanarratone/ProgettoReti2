import network
import time
from network import WLAN

class Wifi:
    def __init__(self,SSID,Password):
        self.SSID = SSID
        self.Password = Password

    def connect(self):
        wlan = WLAN(network.STA_IF)
        wlan.active(True)
        wlan.connect(self.SSID, self.Password)
        while not wlan.isconnected():
            print('Mi sto collegando al wifi\n');
            time.sleep(1)
        return'Sono collegato al wifi:', wlan.ifconfig()[0]
    
