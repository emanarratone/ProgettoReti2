# ESP32 FILE SERVER 
# AVVIA PRIMA QUESTO FILE e poi il file su pc il file backup.py
import socket
import uos as os
import ujson as json
import network
import time
# ═════════════════ CONFIG WIFI ═════════════════
SSID = "S23 Ultra di Salvatore"      # ← CAMBIA
PASSWORD = "24022002"   # ← CAMBIA
# ═══════════════════════════════════════════════

class FileServer:
    def list_files(self):
        try:
            return [f for f in os.listdir('/') if f.endswith('.py')]
        except:
            return []
    
    def get_file(self, filename):
        try:
            with open('/'+filename) as f:
                return f.read()
        except:
            return "File non trovato"

def handle_client(client_sock):
    try:
        data = client_sock.recv(1024)
        cmd = data.decode().strip()
        print("CMD:", cmd)
        
        fs = FileServer()
        if cmd == "LIST":
            resp = json.dumps(fs.list_files())
        elif cmd.startswith("GET "):
            resp = json.dumps({"content": fs.get_file(cmd[4:].strip())})
        elif cmd.startswith("DEL "):
            try:
                os.remove('/'+cmd[4:].strip())
                resp = "✓ Cancellato"
            except:
                resp = "✗ Errore"
        else:
            resp = "LIST | GET main.py | DEL test.py"
        
        client_sock.send(resp.encode())
        print("OK:", resp[:30])
        
    except Exception as e:
        print("Client err:", e)
    finally:
        client_sock.close()

def main():
    # WiFi robusto
    wlan = network.WLAN(network.STA_IF)
    wlan.active(False)
    time.sleep(1)
    wlan.active(True)
    
    wlan.connect(SSID, PASSWORD)
    print("WiFi...")
    timeout = 20
    while not wlan.isconnected() and timeout > 0:
        time.sleep(0.5)
        timeout -= 1
    
    if wlan.isconnected():
        ip = wlan.ifconfig()[0]
        print("✅ IP:", ip)
        print("🎯 TCP 8080")
    else:
        print("❌ WiFi fallito")
        return
    
    # Server loop SEMPLICE
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(('', 8080))
    sock.listen(3)
    print("👂 In ascolto...")
    
    while True:
        try:
            client, addr = sock.accept()
            print("Client:", addr)
            handle_client(client)  # Sincrono, ma veloce
        except KeyboardInterrupt:
            print("\nStop")
            break
        except Exception as e:
            print("Server err:", e)
            time.sleep(1)

main()
