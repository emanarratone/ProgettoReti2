CREATE TABLE BIGLIETTO (
    id_biglietto   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    targa          VARCHAR(10) NOT NULL,
    timestamp_in   TIMESTAMP NOT NULL,
    casello_in     INT NOT NULL,
    id_totem       INT NOT NULL,
);

-- Reset opzionale per partire da ID 1
ALTER TABLE BIGLIETTO ALTER COLUMN id_biglietto RESTART WITH 1;

WITH elenco_targhe AS (
    SELECT * FROM (VALUES 
        ('AB123CD'), ('EF456GH'), ('IJ789KL'), ('MN101OP'), ('QR202ST'), 
        ('UV303WX'), ('YZ404AA'), ('BB505CC'), ('DD606EE'), ('FF707GG'),
        ('HH808II'), ('JJ909KK'), ('LL010MM'), ('NN111PP'), ('QQ212RR'),
        ('SS313TT'), ('UU414VV'), ('WW515XX'), ('YY616ZZ'), ('AC717BD'),
        ('CE818DF'), ('EG919FH'), ('GI020HJ'), ('IK121JL'), ('LM222NO'),
        ('OP323QR'), ('RS424TU'), ('UV525WX'), ('XY626ZA'), ('AZ727BC'),
        ('CD828EF'), ('GH929IJ'), ('KL030MN'), ('NP131QR'), ('ST232UV'),
        ('WX333YZ'), ('BA434CD'), ('DE535FG'), ('HI636JK')
    ) AS t(targa)
),
targhe_numerate AS (
    SELECT targa, row_number() OVER () - 1 as idx FROM elenco_targhe
)
INSERT INTO BIGLIETTO (targa, timestamp_in, casello_in, id_totem)
SELECT 
    tn.targa, 
    TIMESTAMP '2026-02-09 00:00:00' + (s.i * INTERVAL '5 minutes'), -- Un ingresso ogni 5 minuti
    (s.i % 20) + 1,  -- Simula 20 caselli diversi
    (s.i % 5) + 1    -- Simula 5 totem diversi
FROM generate_series(0, 999) AS s(i)
JOIN targhe_numerate tn ON tn.idx = (s.i % 39); -- Cicla sulle tue 39 targhe