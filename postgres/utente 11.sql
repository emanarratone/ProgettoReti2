-- UTENTI (già identity, nessuna FK)
CREATE TABLE UTENTI (
    id_utente     INT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_admin      BOOLEAN NOT NULL DEFAULT FALSE
);

---l'inserimento verrà fatto tramite web app