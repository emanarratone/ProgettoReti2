-- CASELLO
CREATE TABLE CASELLO (
    id_casello    INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    sigla         VARCHAR(100) NOT NULL,
    id_autostrada INT NOT NULL,
    is_closed     BOOLEAN NOT NULL DEFAULT FALSE,
    limite        INT NOT NULL,
);

INSERT INTO CASELLO (sigla, id_autostrada, is_closed, limite) VALUES
  -- Autostrada A1 (Milano, Lombardia)
  ('SAN GIULIANO MILANESE', 1, FALSE, 130),
  ('FIORENZUOLA', 2, FALSE, 130),
  ('FIRENZE SUD', 3, FALSE, 130),
  ('ORVIETO', 4, FALSE, 130),
  ('COLLEFERRO', 5, FALSE, 130),
  ('CAPUA', 6, FALSE, 130),

  -- Autostrada A2 (Salerno, Campania)
  ('BATTIPAGLIA', 7, FALSE, 130),
  ('LAGONEGRO SUD', 8, FALSE, 130),
  ('TARSIA', 9, FALSE, 130),

  -- Autostrada A3 (Napoli, Campania)
  ('NAPOLI', 10, FALSE, 130),
  ('CASTELLAMMARE DI STABIA', 10, FALSE, 130),
  ('ANGRI', 10, FALSE, 130),

  -- Autostrada A4 (Torino, Piemonte)
  ('SANTHIA', 11, FALSE, 130),
  ('PERO', 12, FALSE, 130),
  ('MONTEBELLO', 13, FALSE, 130),
  ('MONFALCONE EST', 14, FALSE, 130),

  -- Autostrada A5 (Torino, Piemonte)
  ('IVREA', 15, FALSE, 130),
  ('AOSTA EST', 16, FALSE, 130),
  ('COURMAYEUR', 16, FALSE, 130),

  -- Autostrada A6 (Torino, Piemonte)
  ('CARMAGNOLA', 17, FALSE, 130),
  ('MONDOVI', 17, FALSE, 130),
  ('ALTARE', 18, FALSE, 130),

  -- Autostrada A7 (lano, Lombardia)
  ('ASSAGO', 19, FALSE, 130),
  ('TORTONA', 20, FALSE, 130),
  ('BUSALLA', 21, FALSE, 130),

  -- Autostrada A8 (lano, Lombardia)
  ('LAINATE-ARESE', 22, FALSE, 130),
  ('LEGNANO', 22, FALSE, 130),
  ('CASTELLANZA', 22, FALSE, 130),

  -- Autostrada A9 (Lainate, Lombardia)
  ('LAINATE', 23, FALSE, 130),
  ('ORIGGIO', 23, FALSE, 130),
  ('SARONNO', 23, FALSE, 130),

  -- Autostrada A10 (Genova, Liguria)
  ('GENOVA PEGLI', 24, FALSE, 130),
  ('ARENZANO', 24, FALSE, 130),
  ('CELLE LIGURE', 24, FALSE, 130),

  -- Autostrada A11 (Firenze, Toscana)
  ('FIRENZE OVEST', 25, FALSE, 130),
  ('SESTO FIORENTINO', 25, FALSE, 130),
  ('PISTOIA', 25, FALSE, 130),

  -- Autostrada A12 (Genova, Liguria)
  ('RAPALLO', 26, FALSE, 130),
  ('VIAREGGIO', 27, FALSE, 130),
  ('TARQUINIA', 28, FALSE, 130),

  -- Autostrada A13 (Bologna, Elia-Romagna)
  ('BOLOGNA ARCOVEGGIO', 29, FALSE, 130),
  ('FERRARA SUD', 29, FALSE, 130),
  ('OCCHIOBELLO', 30, FALSE, 130),

  -- Autostrada A14 (Bologna, Elia-Romagna)
  ('BOLOGNA BORGO PANIGALE', 31, FALSE, 130),
  ('PESARO', 32, FALSE, 130),
  ('TERAMO', 33, FALSE, 130),
  ('TERMOLI', 34, FALSE, 130),
  ('FOGGIA', 35, FALSE, 130),

  -- Autostrada A15 (Sissa Trecasali, Elia-Romagna)
  ('SISSE TRECASALI', 38, FALSE, 130),
  ('PONTREMOLI', 37, FALSE, 130),
  ('VEZZANO LIGURE', 36, FALSE, 130),

  -- Autostrada A16 (Napoli, Campania)
  ('TUFINO', 39, FALSE, 130),
  ('VALLATA', 39, FALSE, 130),
  ('CERIGNOLA OVEST', 40, FALSE, 130),

  -- Autostrada A18 (Messina, Sicilia)
  ('ROCCALUMERA', 41, FALSE, 130),
  ('TAORNIA', 41, FALSE, 130),
  ('ACIREALE', 41, FALSE, 130),

  -- Autostrada A19 (Palermo, Sicilia)
  ('VILLABATE', 42, FALSE, 130),
  ('TRABIA', 42, FALSE, 130),
  ('CATANISETTA', 42, FALSE, 130),

  -- Autostrada A20 (Messina, Sicilia)
  ('MESSINA SUD', 43, FALSE, 130),
  ('FALCONE', 43, FALSE, 130),
  ('PATTI', 43, FALSE, 130),

  -- Autostrada A21 (Torino, Piemonte)
  ('VILLANOVA', 44, FALSE, 130),
  ('VOGHERA', 45, FALSE, 130),
  ('PIACENZA OVEST', 46, FALSE, 130),

  -- Autostrada A22 (Brennero, Trentino-Alto Adige)
  ('CAMPOGALLIANO', 50, FALSE, 130),
  ('MANTOVA SUD', 47, FALSE, 130),
  ('VERONA NORD', 49, FALSE, 130),
  ('TRENTO NORD', 48, FALSE, 130),

  -- Autostrada A23 (Palmanova, Friuli-Venezia Giulia)
  ('UDINE', 51, FALSE, 130),
  ('PONTEBBA', 51, FALSE, 130),
  ('TARVISIO SUD', 51, FALSE, 130),

  -- Autostrada A24 (Roma, Lazio)
  ('SETTECANI', 52, FALSE, 130),
  ('CASTEL MADAMA', 52, FALSE, 130),
  ('LAQUILA EST', 53, FALSE, 130),

  -- Autostrada A25 (Torano)
  ('TORANA', 54, FALSE, 130),
  ('PESCINA', 55, FALSE, 130),
  ('CHIETI', 55, FALSE, 130),

  -- Autostrada A26 (Genova Voltri, Liguria)
  ('MASONE', 57, FALSE, 130),
  ('OVADA', 56, FALSE, 130),
  ('CASALE MONFERRATO NORD', 56, FALSE, 130),

  -- Autostrada A27 (Venezia, Veneto)
  ('CASALE SUL SILE', 58, FALSE, 130),
  ('TREVISO SUD', 58, FALSE, 130),
  ('VITTORIO VENETO NORD',  58, FALSE, 130),

  -- Autostrada A28 (Portogruaro, Veneto)
  ('PORTOGRUARO', 59, FALSE, 130),
  ('SESTO AL REGHENA', 60, FALSE, 130),
  ('SACILE EST', 60, FALSE, 130),

  -- Autostrada A29 (Palermo, Sicilia)
  ('CARINI', 61, FALSE, 130),
  ('BALESTRATE', 61, FALSE, 130),
  ('CASTELLAMMARE DEL GOLFO', 61, FALSE, 130),

  -- Autostrada A30 (Caserta, Campania)
  ('MADDALONI', 62, FALSE, 130),
  ('NOLA', 62, FALSE, 130),
  ('CASTEL SAN GIORGIO', 62, FALSE, 130),

  -- Autostrada A31 (Badia Polesine, Veneto)
  ('BADIA POLESINE', 63, FALSE, 130),
  ('PIACENZA DADIGE', 63, FALSE, 130),
  ('VICENZA NORD', 63, FALSE, 130),

  -- Autostrada A32 (Torino, Piemonte)
  ('AVIGLIANA EST', 64, FALSE, 130),
  ('SUSA EST', 64, FALSE, 130),
  ('BARDONECCHIA', 64, FALSE, 130),

  -- Autostrada A33 (Asti, Piemonte)
  ('FOSSANO', 65, FALSE, 130),
  ('CHERASCO', 65, FALSE, 130),
  ('CASTIGLIONE', 65, FALSE, 130),

  -- Autostrada A34 (Villesse, Friuli-Venezia Giulia)
  ('VILLESSE', 66, FALSE, 130),
  ('GRADISCA DISONZO', 66, FALSE, 130),
  ('GORIZIA', 66, FALSE, 130),

  -- Autostrada A35 (Brescia, Lombardia)
  ('TRAVAGLIATO EST', 67, FALSE, 130),
  ('BARIANO', 67, FALSE, 130),
  ('MILANO', 67, FALSE, 130),

  -- Autostrada A36 (Cassano Magnano, Lombardia)
  ('SOLBIATE OLONA', 68, FALSE, 130),
  ('CISLAGO', 68, FALSE, 130),
  ('MEDA', 68, FALSE, 130),
  ('FILAGO', 68, FALSE, 130);