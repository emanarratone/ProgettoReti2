# Architettura a Microservizi - ProgettoReti2

## 📁 Struttura del Progetto

Il progetto è stato refactorato da un monolito a una vera architettura a microservizi con servizi completamente indipendenti:

```
ProgettoReti2/
├── autostrada-service/     (porta 8081)
├── casello-service/        (porta 8082)
├── corsia-service/         (porta 8083)
├── regione-service/        (porta 8084)
├── dispositivi-service/    (porta 8085)
├── biglietto-service/      (porta 8086)
├── pagamento-service/      (porta 8087)
├── multa-service/          (porta 8088)
├── veicolo-service/        (porta 8089)
├── utente-service/         (porta 8090)
└── [cartelle originali mantenute: src/, DB/, REST/, model/]
```

## 🎯 Caratteristiche dei Microservizi

Ogni servizio è:
- ✅ **Indipendente**: Proprio progetto Maven con pom.xml separato
- ✅ **Deployabile autonomamente**: Può essere avviato, fermato e aggiornato separatamente
- ✅ **Scalabile**: Ogni servizio può essere scalato indipendentemente
- ✅ **Isolato**: Package strutturati con naming Java corretto (com.example.*)

## 🚀 Come Avviare i Microservizi

### Opzione 1: Avvio Singolo Servizio

```bash
# Posizionati nella cartella del servizio
cd autostrada-service
mvn spring-boot:run
```

### Opzione 2: Avvio Tutti i Servizi

Apri terminali separati e avvia ciascun servizio:

```bash
# Terminale 1 - Autostrada
cd autostrada-service && mvn spring-boot:run

# Terminale 2 - Casello
cd casello-service && mvn spring-boot:run

# Terminale 3 - Corsia
cd corsia-service && mvn spring-boot:run

# Terminale 4 - Regione
cd regione-service && mvn spring-boot:run

# Terminale 5 - Dispositivi
cd dispositivi-service && mvn spring-boot:run

# Terminale 6 - Biglietto
cd biglietto-service && mvn spring-boot:run

# Terminale 7 - Pagamento
cd pagamento-service && mvn spring-boot:run

# Terminale 8 - Multa
cd multa-service && mvn spring-boot:run

# Terminale 9 - Veicolo
cd veicolo-service && mvn spring-boot:run

# Terminale 10 - Utente
cd utente-service && mvn spring-boot:run
```

## 🌐 Endpoint API

Ogni microservizio espone le proprie API REST:

### Autostrada Service (8081)
- `GET http://localhost:8081/highways` - Lista autostrade
- `GET http://localhost:8081/highways/search?q=A1` - Ricerca
- `POST http://localhost:8081/highways` - Crea autostrada
- `PUT http://localhost:8081/highways/{id}` - Aggiorna
- `DELETE http://localhost:8081/highways/{id}` - Elimina

### Casello Service (8082)
- `GET http://localhost:8082/toll` - Lista caselli
- `GET http://localhost:8082/toll/search?q=...`
- `POST http://localhost:8082/toll`
- `PUT http://localhost:8082/toll/{id}`
- `DELETE http://localhost:8082/toll/{id}`

### Corsia Service (8083)

### Biglietto Service (8086)
- `GET http://localhost:8086/tickets` - Lista biglietti
- `GET http://localhost:8086/tickets/{id}` - Dettaglio biglietto
- `GET http://localhost:8086/tickets/targa/{targa}` - Biglietti per targa
- `POST http://localhost:8086/tickets` - Crea biglietto
- `DELETE http://localhost:8086/tickets/{id}` - Elimina

### Pagamento Service (8087)
- `GET http://localhost:8087/payments` - Lista pagamenti
- `GET http://localhost:8087/payments/{id}` - Dettaglio pagamento
- `GET http://localhost:8087/payments/biglietto/{id}` - Pagamenti per biglietto
- `GET http://localhost:8087/payments/unpaid` - Pagamenti non effettuati
- `POST http://localhost:8087/payments` - Crea pagamento
- `PUT http://localhost:8087/payments/{id}/pay` - Segna come pagato

### Multa Service (8088)
- `GET http://localhost:8088/fines` - Lista multe
- `GET http://localhost:8088/fines/{id}` - Dettaglio multa
- `GET http://localhost:8088/fines/targa/{targa}` - Multe per targa
- `GET http://localhost:8088/fines/unpaid` - Multe non pagate
- `POST http://localhost:8088/fines` - Crea multa
- `PUT http://localhost:8088/fines/{id}/pay` - Segna come pagata
- `DELETE http://localhost:8088/fines/{id}` - Elimina

### Veicolo Service (8089)
- `GET http://localhost:8089/vehicles` - Lista veicoli
- `GET http://localhost:8089/vehicles/{targa}` - Dettaglio veicolo
- `GET http://localhost:8089/vehicles/tipo/{tipo}` - Veicoli per tipo (A,B,C,D,E)
- `POST http://localhost:8089/vehicles` - Registra veicolo
- `PUT http://localhost:8089/vehicles/{targa}` - Aggiorna veicolo
- `DELETE http://localhost:8089/vehicles/{targa}` - Elimina

### Utente Service (8090)
- `GET hdi tutti i servizi
for service in autostrada-service casello-service corsia-service regione-service \
               dispositivi-service biglietto-service pagamento-service multa-service \
               veicolo-service utente-service; do
  echo "Building $service..."
  cd $service && mvn clean package && cd ..
done
```

O singolarmente:
```bash
cd autostrada-service && mvn clean package && cd ..
cd casello-service && mvn clean package && cd ..
cd corsia-service && mvn clean package && cd ..
cd regione-service && mvn clean package && cd ..
cd dispositivi-service && mvn clean package && cd ..
cd biglietto-service && mvn clean package && cd ..
cd pagamento-service && mvn clean package && cd ..
cd multa-service && mvn clean package && cd ..
cd veicolo-service && mvn clean package && cd ..
cd utentelocalhost:8084/regions` - Lista regioni
- `GET http://localhost:8084/regions/search?q=...`
- `POST http://localhost:8084/regions`
- `PUT http://localhost:8084/regions/{id}`
- `DELETE http://localhost:8084/regions/{id}`

### Dispositivi Service (8085)
- `GET http://localhost:8085/devices/health` - Health check

## 🗄️ Database

Attualmente tutti i servizi condividono lo stesso database PostgreSQL. 

### ⚠️ Prossimi Step per una Vera Architettura a Microservizi:

1. **Database per Servizio**: Creare database/schema separati per ogni servizio
2. **API Gateway**: Aggiungere un gateway (es. Spring Cloud Gateway) come punto di ingresso unico
3. **Service Discovery**: Implementare Eureka o Consul per la registrazione dei servizi
4. **Comunicazione Asincrona**: Usare RabbitMQ o Kafka per eventi tra servizi
5. **Configurazione Centralizzata**: Spring Cloud Config per gestire le configurazioni
6. **Circuit Breaker**: Resilience4j per gestire i fallimenti
7. **Monitoring**: Prometheus + Grafana per il monitoring
8. **Containerizzazione**: Dockerfile per ogni servizio + docker-compose

## 📦 Build di Tutti i Servizi

```bash
# Build autostrada-service
cd autostrada-service && mvn clean package && cd ..

# Build casello-service
cd casello-service && mvn clean package && cd ..

# Build corsia-service
cd corsia-service && mvn clean package && cd ..

# Build regione-service
cd regione-service && mvn clean package && cd ..

# Build dispositivi-service
cd dispositivi-service && mvn clean package && cd ..
```

## 🐳 Esempio Docker Compose (Futuro)

```yaml
version: '3.8'
services:
  autostrada-service:
    build: ./autostrada-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/autostrada_db
  
  casello-service:
    build: ./casello-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/casello_db
  
  # ... altri servizi
```

## 📝 Note

- Le cartelle originali (`src/`, `DB/`, `REST/`, `model/`) sono state mantenute come richiesto
- Ogni servizio ha convenzioni di naming Java corrette (PascalCase per classi, camelCase per metodi)
- I servizi sono pronti per essere containerizzati con Docker
- È possibile aggiungere test unitari e di integrazione in ogni servizio
