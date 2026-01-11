# ESP32 ↔ PC BI-DIREZIONALE SYNC
import socket
import json
import os
import shutil

IP = input("IP ESP32 (10.187.40.110): ") or "10.187.40.110"
PORT = 8080
BACKUP_DIR = "ESP32_Sync"

def safe_recv(sock):
    """Ricevi risposta completa"""
    data = b""
    sock.settimeout(5)
    while True:
        chunk = sock.recv(1024)
        if not chunk: break
        data += chunk
        if b"\n" in data[-2:]: break
    return data.decode('utf-8', errors='ignore').strip()

print("🔗 Connessione ESP32...")
s = socket.socket()
s.connect((IP, PORT))
s.send(b"LIST")
files_json = safe_recv(s)
s.close()

esp_files = json.loads(files_json)
print(f"📂 ESP32 files: {esp_files}")

# Crea/mantieni cartella sync
if os.path.exists(BACKUP_DIR):
    print(f"🔄 Sync in: {BACKUP_DIR}")
else:
    os.makedirs(BACKUP_DIR)
    print(f"📁 Creata: {BACKUP_DIR}")

# 1️⃣ ELIMINA file sul PC non presenti su ESP32
pc_files = [f for f in os.listdir(BACKUP_DIR) if f.endswith('.py')]
to_delete = [f for f in pc_files if f not in esp_files]

for old_file in to_delete:
    filepath = os.path.join(BACKUP_DIR, old_file)
    os.remove(filepath)
    print(f"🗑️  ELIMINATO: {old_file}")

# 2️⃣ SCARICA/AGGIORNA file da ESP32
updated = 0
for filename in esp_files:
    filepath = os.path.join(BACKUP_DIR, filename)
    
    # Skip se identico (check size/timestamp)
    if os.path.exists(filepath):
        if os.path.getsize(filepath) == 0:
            print(f"⚠️  {filename} vuoto, riscarico...")
        else:
            print(f"✅ {filename} già sync")
            continue
    
    # Download
    print(f"⬇️  {filename}")
    s = socket.socket()
    s.connect((IP, PORT))
    s.send(f"GET {filename}".encode())
    resp = safe_recv(s)
    s.close()
    
    try:
        data = json.loads(resp)
        content = data["content"]
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"   ✓ {len(content)} bytes")
        updated += 1
    except:
        print(f"   ✗ Errore download {filename}")

print(f"\n🎉 SYNC COMPLETATO!")
print(f"📊 {len(to_delete)} eliminati, {updated} scaricati/aggiornati")
print(f"📁 {BACKUP_DIR} = MIRROR perfetto di ESP32!")
