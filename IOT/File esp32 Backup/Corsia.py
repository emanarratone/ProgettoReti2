class Corsia:
    def __init__(self, num_corsia, id_casello, verso, tipo_corsia,is_closed):
        self.num_corsia = num_corsia
        self.id_casello = id_casello
        self.verso = verso
        self.tipo_corsia = tipo_corsia
        self.is_closed = is_closed
        
    def __str__(self):
        """Formato leggibile come Java toString()"""
        stato = "CHIUSA" if self.is_closed else "APERTA"
        return (f"Casello {self.id_casello} / "
                f"Corsia {self.num_corsia} / "
                f"Verso: {self.verso} / "
                f"Tipo: {self.tipo_corsia} / "
                f"Stato: {stato}")
    
    def __repr__(self):
        """Debug dettagliato"""
        return (f"Corsia(num_corsia={self.num_corsia}, "
                f"id_casello={self.id_casello}, "
                f"verso={self.verso}, "
                f"tipo_corsia={self.tipo_corsia}, "
                f"is_closed={self.is_closed})")