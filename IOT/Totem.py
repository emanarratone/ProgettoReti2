class Totem(Dispositivo): # Ereditiamo da Dispositivo
    def __init__(self, schermo, ir, corsia, casello, dispositivo_id=None):
        super().__init__(
            status=True,
            corsia=corsia,
            casello=casello,
            tipo_dispositivo="TOTEM",
            dispositivo_id=dispositivo_id
        )
        self.schermo = schermo
        self.ir = ir