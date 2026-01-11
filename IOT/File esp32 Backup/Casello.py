class Casello:
    def __init__(self, id_casello, sigla, id_autostrada, is_closed, limite):
        self.id_casello = id_casello
        self.sigla = sigla
        self.id_autostrada = id_autostrada
        self.is_closed = is_closed
        self.limite = limite

        def __str__(self):
        return f"""id_casello:{self.id_casello} /
                sigla:{self.sigla} /
                id_autostrada:{self.id_autostrada} /
                is_closed:{self.is_closed} /
                limite:{self.limite} /
                """