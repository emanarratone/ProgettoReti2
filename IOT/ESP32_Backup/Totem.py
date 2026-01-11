class Dispositivo:
    def __init__(self, status=False, corsia=0, casello=0, tipo_dispositivo="", dispositivo_id=None):
        self._id = dispositivo_id  
        self.corsia = corsia
        self.casello = casello
        self.status = status
        self.tipo_dispositivo = tipo_dispositivo