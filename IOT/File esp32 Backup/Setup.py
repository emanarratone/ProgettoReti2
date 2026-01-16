import ujson

class Setup:
    def __init__(self, gestore, targa):
        self.gestore = gestore
        self.targa_veicolo = targa
        self.cache = {"veicolo": None, "casello": None, "corsia": None}

    def _pulisci(self, d):
        return d if isinstance(d, dict) or d is None else ujson.loads(d)

    def richiedi_configurazione(self, id_casello, num_corsia):
        """Usa il gestore per scaricare i dati via MQTT"""
        # 1. Veicolo
        self.cache["veicolo"] = self.gestore.richiedi("veicolo/richiesta", 
            {"comando": "GET_AUTO", "targa": self.targa_veicolo}, 
            f"veicolo/{self.targa_veicolo}/risposta")
        
        # 2. Casello
        self.cache["casello"] = self.gestore.richiedi("casello/richiesta", 
            {"comando": "GET_CASELLO", "id_casello": id_casello}, 
            f"casello/risposta/{id_casello}")
        
        # 3. Corsia
        self.cache["corsia"] = self.gestore.richiedi("casello/richiesta", 
            {"comando": "GET_CORSIA", "id_casello": id_casello, "num_corsia": num_corsia}, 
            f"casello/{id_casello}/corsia/{num_corsia}/risposta")

    def dati_configurazione(self, id_casello, num_corsia):
        """Ritorna i dati puliti dalla cache"""
        return (self._pulisci(self.cache["veicolo"]), 
                self._pulisci(self.cache["casello"]), 
                self._pulisci(self.cache["corsia"]))