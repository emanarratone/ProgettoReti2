class Casello:
    def __init__(self, id_casello, sigla, id_autostrada, is_closed, limite):
        self.id_casello = id_casello
        self.sigla = sigla
        self.id_autostrada = id_autostrada
        self.is_closed = is_closed
        self.limite = limite

    def __str__(self):
        stato = "CHIUSO" if self.is_closed else "APERTO"
        return "Casello {} (ID: {}) - Stato: {} - Limite: {}km/h".format(
            self.sigla, self.id_casello, stato, self.limite
        )