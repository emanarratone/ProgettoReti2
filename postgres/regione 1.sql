-- REGIONE
CREATE TABLE REGIONE (
    id_regione INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL
);

-- REGIONE
INSERT INTO REGIONE (nome) VALUES
  ('Valle dAosta'),
  ('Piemonte'),
  ('Liguria'),
  ('Lombardia'),
  ('Trentino-Alto Adige'),
  ('Veneto'),
  ('Friuli-Venezia Giulia'),
  ('Elia-Romagna'),
  ('Toscana'),
  ('Umbria'),
  ('Marche'),
  ('Lazio'),
  ('Abruzzo'),
  ('Molise'),
  ('Campania'),
  ('Puglia'),
  ('Basilicata'),
  ('Calabria'),
  ('Sicilia'),
  ('Sardegna');