-- MULTA
CREATE TABLE MULTA (
    id_multa     INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    targa        VARCHAR(10) NOT NULL,
    importo      DECIMAL(10,2) NOT NULL,
    pagato       BOOLEAN DEFAULT FALSE,
    id_biglietto INT NOT NULL,
);

WITH lista_morosi AS (
    -- Simulo qui i dati che il microservizio "Sanzioni" riceverebbe tramite evento
    -- Pesco dalle tue 39 targhe originali
    SELECT 
        s.i as id_biglietto,
        t.targa
    FROM generate_series(1, 500) s(i)
    JOIN (
        SELECT targa, row_number() OVER () - 1 as idx 
        FROM (VALUES 
            ('AB123CD'), ('EF456GH'), ('IJ789KL'), ('MN101OP'), ('QR202ST'), 
            ('UV303WX'), ('YZ404AA'), ('BB505CC'), ('DD606EE'), ('FF707GG'),
            ('HH808II'), ('JJ909KK'), ('LL010MM'), ('NN111PP'), ('QQ212RR'),
            ('SS313TT'), ('UU414VV'), ('WW515XX'), ('YY616ZZ'), ('AC717BD'),
            ('CE818DF'), ('EG919FH'), ('GI020HJ'), ('IK121JL'), ('LM222NO'),
            ('OP323QR'), ('RS424TU'), ('UV525WX'), ('XY626ZA'), ('AZ727BC'),
            ('CD828EF'), ('GH929IJ'), ('KL030MN'), ('NP131QR'), ('ST232UV'),
            ('WX333YZ'), ('BA434CD'), ('DE535FG'), ('HI636JK')
        ) AS v(targa)
    ) t ON t.idx = (s.i % 39)
    WHERE (s.i % 5) = 0 -- Simulo che 1 su 5 (il 20%) non abbia pagato
)
INSERT INTO MULTA (targa, importo, pagato, id_biglietto)
SELECT 
    targa, 
    150.00, -- Importo fisso della sanzione
    FALSE,  -- Di default non pagata
    id_biglietto
FROM lista_morosi;