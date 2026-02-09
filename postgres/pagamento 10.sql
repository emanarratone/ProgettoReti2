-- PAGAMENTO
CREATE TABLE PAGAMENTO (
    id_pagamento  INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    id_biglietto  INT NOT NULL,
    importo       DECIMAL(10,2) NOT NULL,
    stato         VARCHAR(20)   NOT NULL, -- 'PAGATO', 'PENDENTE'...
    timestamp_out TIMESTAMP     NOT NULL,
    casello_out   INT           NOT NULL,
    id_transazione INT,
);

INSERT INTO PAGAMENTO (id_biglietto, importo, stato, timestamp_out, casello_out)
SELECT 
    s.i AS id_biglietto, -- Usa gli ID da 1 a 500
    (5 + (random() * 45))::DECIMAL(10,2) AS importo, -- Importo random tra 5 e 50 euro
    CASE 
        WHEN random() < 0.8 THEN 'PAGATO' 
        ELSE 'NON_PAGATO' 
    END AS stato,
    -- Uscita calcolata: timestamp ingresso (ipotetico) + 45-90 minuti di viaggio
    TIMESTAMP '2026-01-01 00:00:00' + (s.i * INTERVAL '5 minutes') + (INTERVAL '45 minutes' + random() * INTERVAL '45 minutes') AS timestamp_out,
    (s.i % 10) + 1 AS casello_out
FROM generate_series(1, 1000) AS s(i);